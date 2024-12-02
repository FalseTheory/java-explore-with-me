package ru.practicum.ewm.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.dto.ParamHitDto;
import ru.practicum.ewm.dto.StatDto;
import ru.practicum.ewm.mapper.EventsMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.QEvent;
import ru.practicum.ewm.model.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.model.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.model.exception.BadParametersException;
import ru.practicum.ewm.model.exception.ConflictException;
import ru.practicum.ewm.model.exception.ForbiddenException;
import ru.practicum.ewm.model.util.AdminSearchParams;
import ru.practicum.ewm.model.util.EventState;
import ru.practicum.ewm.model.util.EventStateAdminAction;
import ru.practicum.ewm.model.util.SearchParams;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.NewEventDto;
import ru.practicum.ewm.model.exception.NotFoundException;
import ru.practicum.ewm.repository.CategoriesRepository;
import ru.practicum.ewm.repository.EventsRepository;
import ru.practicum.ewm.repository.UsersRepository;
import ru.practicum.ewm.service.EventsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class EventsServiceImpl implements EventsService {

    private final EventsMapper mapper;
    private final UsersRepository usersRepository;
    private final EventsRepository eventsRepository;
    private final CategoriesRepository categoriesRepository;
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatClient statClient;


    @Override
    public EventFullDto create(Long id, NewEventDto newEventDto) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user with id -" + id + " not found"));
        Category category = categoriesRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("category with id -" + id + " not found"));
        Event event = eventsRepository.save(mapper.mapNewToEvent(user, category, newEventDto));

        return mapper.mapEventToFullDto(event);

    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAll(SearchParams params, ParamHitDto paramHitDto) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        final String baseUri = paramHitDto.getUri();
        if (params.rangeStart() != null && params.rangeEnd() != null) {
            start = LocalDateTime.parse(params.rangeStart(), datePattern);
            end = LocalDateTime.parse(params.rangeEnd(), datePattern);
            if (start.isAfter(end)) {
                throw new BadParametersException("EndTime cannot be before startTime");
            }
        }
        statClient.hit(paramHitDto);
        QEvent qEvent = QEvent.event;
        BooleanBuilder query = new BooleanBuilder();
        Pageable pageable;
        query.and(qEvent.state.eq(EventState.PUBLISHED));
        if (params.text() != null) {
            query.and(qEvent.description.contains(params.text())).or(qEvent.annotation.contains(params.text()));
        }
        if (params.categories() != null) {
            query.and(qEvent.category.id.in(params.categories()));
        }
        if (params.paid() != null) {
            query.and(qEvent.paid.eq(params.paid()));
        }
        if (start != null) {
            query.and(qEvent.eventDate
                    .after(start));
        }
        if (end != null) {
            query.and(qEvent.eventDate
                    .before(end));
        }
        if (params.onlyAvailable()) {
            query.andNot(qEvent.confirmedRequests.eq(QEvent.event.participationLimit));
        }
        switch (params.sortType()) {
            case EVENT_DATE ->
                    pageable = PageRequest.of(params.from(), params.size(), new QSort(qEvent.eventDate.desc()));

            case VIEWS -> pageable = PageRequest.of(params.from(), params.size(), new QSort(qEvent.views.desc()));
            case null, default -> pageable = PageRequest.of(params.from(), params.size());
        }

        List<Event> eventList = eventsRepository.findAll(query, pageable).getContent();
        for (Event event : eventList) {
            paramHitDto.setUri(baseUri + "/" + event.getId());
            statClient.hit(paramHitDto);
        }
        return eventList
                .stream()
                .map(mapper::mapEventToFullDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAll(AdminSearchParams params) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (params.rangeStart() != null && params.rangeEnd() != null) {
            start = LocalDateTime.parse(params.rangeStart(), datePattern);
            end = LocalDateTime.parse(params.rangeEnd(), datePattern);
            if (start.isAfter(end)) {
                throw new BadParametersException("EndTime cannot be before startTime");
            }
        }
        QEvent qEvent = QEvent.event;
        BooleanBuilder query = new BooleanBuilder();
        Pageable pageable = PageRequest.of(params.from(), params.size());

        if (params.users() != null) {
            query.and(qEvent.initiator.id.in(params.users()));

        }
        if (params.states() != null) {
            query.and(qEvent.state.stringValue().in(params.states()));
        }
        if (params.categories() != null) {
            query.and(qEvent.category.id.in(params.categories()));
        }
        if (start != null) {
            query.and(qEvent.eventDate.after(start));
        }
        if (end != null) {
            query.and(qEvent.eventDate.before(end));
        }

        return eventsRepository.findAll(query, pageable).stream()
                .map(mapper::mapEventToFullDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAll(Long userId, Pageable pageable) {
        BooleanExpression userEvents = QEvent.event.initiator.id.eq(userId);
        return eventsRepository.findAll(userEvents, pageable).stream()
                .map(mapper::mapEventToFullDto).toList();
    }

    @Override
    public EventFullDto get(Long id, ParamHitDto paramHitDto) {
        BooleanExpression expression = QEvent.event.id.eq(id).and(QEvent.event.state.eq(EventState.PUBLISHED));
        Event event = eventsRepository.findOne(expression)
                .orElseThrow(() -> new NotFoundException("event with id -" + id + " not found"));

        statClient.hit(paramHitDto);
        LocalDateTime fromStart = LocalDateTime.of(0, 1, 1, 0, 0);
        List<StatDto> list = statClient.getStat(fromStart.format(datePattern),
                LocalDateTime.now().plusDays(1).format(datePattern), List.of(paramHitDto.getUri()), true);
        event.setViews((long) list.size());


        return mapper.mapEventToFullDto(eventsRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto get(Long userId, Long eventId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id -" + userId + " not found"));
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event with id -" + eventId + " not found"));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("user with id - " + userId + " dont have access to event - " + eventId);
        }
        return mapper.mapEventToFullDto(event);
    }

    @Override
    public EventFullDto update(UpdateEventUserRequest updateBody) {
        usersRepository.findById(updateBody.getUserId())
                .orElseThrow(() -> new NotFoundException("user with id -" + updateBody.getUserId() + " not found"));
        Event event = eventsRepository.findById(updateBody.getEventId())
                .orElseThrow(() -> new NotFoundException("event with id -" + updateBody.getEventId() + " not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("modifying this event is not allowed");
        }
        if (updateBody.getCategory() != null) {
            Category category = categoriesRepository.findById(updateBody.getCategory())
                    .orElseThrow(() -> new NotFoundException("category with id -" + updateBody.getCategory() + " not found"));
            event.setCategory(category);
        }
        if (updateBody.getAnnotation() != null) {
            event.setAnnotation(updateBody.getAnnotation());
        }
        if (updateBody.getDescription() != null) {
            event.setDescription(updateBody.getDescription());
        }
        if (updateBody.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateBody.getEventDate(), datePattern));
        }
        if (updateBody.getLocation() != null) {
            event.setLat(updateBody.getLocation().getLat());
            event.setLon(updateBody.getLocation().getLon());
        }
        if (updateBody.getPaid() != null) {
            event.setPaid(updateBody.getPaid());
        }
        if (updateBody.getParticipantLimit() != null) {
            event.setParticipationLimit(updateBody.getParticipantLimit());
        }
        if (updateBody.getRequestModeration() != null) {
            event.setRequestModeration(updateBody.getRequestModeration());
        }
        switch (updateBody.getStateAction()) {
            case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
            case null, default -> {
            }
        }
        if (updateBody.getTitle() != null) {
            event.setTitle(updateBody.getTitle());
        }

        return mapper.mapEventToFullDto(eventsRepository.save(event));
    }

    @Override
    public EventFullDto publish(Long eventId, UpdateEventAdminRequest updateBody) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event with id -" + eventId + " not found"));
        if (updateBody.getStateAction() == EventStateAdminAction.PUBLISH_EVENT && event.getState() != EventState.PENDING) {
            throw new ConflictException("only pending events can be published");
        }
        if (updateBody.getStateAction() == EventStateAdminAction.REJECT_EVENT && event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("already published events can't be rejected");
        }
        if (updateBody.getEventDate() != null) {
            LocalDateTime eventTime = LocalDateTime.parse(updateBody.getEventDate(), datePattern);
            event.setEventDate(eventTime);
        }
        if (updateBody.getAnnotation() != null) {
            event.setAnnotation(updateBody.getAnnotation());
        }
        if (updateBody.getCategory() != null) {
            if (!Objects.equals(updateBody.getCategory(), event.getCategory().getId())) {

                Category category = categoriesRepository.findById(updateBody.getCategory())
                        .orElseThrow(() -> new NotFoundException("category with id -" + updateBody.getCategory() + " not found"));
                event.setCategory(category);
            }
        }
        if (updateBody.getDescription() != null) {
            event.setDescription(updateBody.getDescription());
        }
        if (updateBody.getLocation() != null) {
            event.setLon(updateBody.getLocation().getLon());
            event.setLat(updateBody.getLocation().getLat());
        }
        if (updateBody.getParticipantLimit() != null) {
            event.setParticipationLimit(updateBody.getParticipantLimit());
        }
        if (updateBody.getTitle() != null) {
            event.setTitle(updateBody.getTitle());
        }
        if (updateBody.getPaid() != null) {
            event.setPaid(updateBody.getPaid());
        }
        switch (updateBody.getStateAction()) {
            case PUBLISH_EVENT -> {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            case REJECT_EVENT -> event.setState(EventState.CANCELED);
            case null, default -> {
            }
        }

        return mapper.mapEventToFullDto(eventsRepository.save(event));
    }
}
