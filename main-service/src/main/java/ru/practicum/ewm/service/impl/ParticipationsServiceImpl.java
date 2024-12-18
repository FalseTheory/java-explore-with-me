package ru.practicum.ewm.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.ParticipationMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.exception.ConflictException;
import ru.practicum.ewm.model.exception.ForbiddenException;
import ru.practicum.ewm.model.exception.NotFoundException;
import ru.practicum.ewm.model.util.EventState;
import ru.practicum.ewm.model.util.RequestStatus;
import ru.practicum.ewm.repository.EventsRepository;
import ru.practicum.ewm.repository.ParticipationRepository;
import ru.practicum.ewm.repository.UsersRepository;
import ru.practicum.ewm.service.ParticipationsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationsServiceImpl implements ParticipationsService {

    private final ParticipationMapper mapper;
    private final UsersRepository usersRepository;
    private final ParticipationRepository participationRepository;
    private final EventsRepository eventsRepository;

    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllForUser(Long id) {
        usersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user with id - " + id + " not found"));

        BooleanExpression allUserReq = QParticipation.participation.requester.id.eq(id);

        List<Participation> participationList = new ArrayList<>();
        participationRepository.findAll(allUserReq).iterator().forEachRemaining(participationList::add);
        return participationList.stream().map(mapper::mapToDto).toList();
    }

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        LocalDateTime now = LocalDateTime.now();
        Participation participation = new Participation();
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id - " + userId + " not found"));
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event with id - " + eventId + " not found"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event with id - " + eventId + "is in unpublished state");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("You can't make a participation request for your own event");
        }
        if (event.getParticipationLimit() > 0 && event.getParticipationLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException("Event have reached participants limit");
        }
        BooleanExpression expression = QParticipation.participation.requester.id.eq(userId)
                .and(QParticipation.participation.event.eq(event));
        if (participationRepository.findOne(expression).isPresent()) {
            throw new ConflictException("You cannot create more than one request for save event");
        }
        participation.setEvent(event);
        participation.setRequester(user);
        participation.setCreated(LocalDateTime.parse(now.format(pattern), pattern));
        if (event.getParticipationLimit() == 0) {
            participation.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventsRepository.save(event);
        }
        if (!event.getRequestModeration()) {
            participation.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventsRepository.save(event);
        }

        participation = participationRepository.save(participation);
        System.out.println(participation.getCreated());

        return mapper.mapToDto(participation);
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        Participation participation = participationRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("request with id - " + requestId + " not found"));
        if (!participation.getRequester().getId().equals(userId)) {
            throw new ForbiddenException("user with id - " + userId + " dont have access to this request");
        }
        participation.setStatus(RequestStatus.CANCELED);
        return mapper.mapToDto(participationRepository.save(participation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserEventsRequests(Long userId, Long eventId) {
        BooleanBuilder query = new BooleanBuilder();
        query.and(QParticipation.participation.event.id.eq(eventId));
        query.and(QEvent.event.initiator.id.eq(userId));
        List<Participation> participationList = new ArrayList<>();
        participationRepository.findAll(query).iterator().forEachRemaining(participationList::add);

        return participationList.stream().map(mapper::mapToDto).toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateUserEventsRequests(EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        Event event = eventsRepository.findById(updateRequest.getEventId())
                .orElseThrow(() -> new NotFoundException("event with id - " + updateRequest.getEventId() + " not found"));
        usersRepository.findById(updateRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("user with id - " + updateRequest.getUserId() + " not found"));
        int limit = event.getParticipationLimit();
        int participants = event.getConfirmedRequests();
        if (limit > 0 && participants == limit) {
            throw new ConflictException("Event has reached participants limit");
        }
        RequestStatus status = updateRequest.getStatus();
        List<Participation> participationList = participationRepository.findAllById(updateRequest.getRequestIds());
        for (Participation participation : participationList) {
            if (participation.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("You can only change pending requests");
            }
            if (status == RequestStatus.CONFIRMED && limit > participants) {
                participation.setStatus(status);
                participants++;
                result.getConfirmedRequests().add(mapper.mapToDto(participation));
            } else if (status == RequestStatus.CONFIRMED && limit == participants) {
                participation.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(mapper.mapToDto(participation));
            } else {
                participation.setStatus(status);
                result.getRejectedRequests().add(mapper.mapToDto(participation));
            }

        }
        event.setConfirmedRequests(participants);
        eventsRepository.save(event);
        participationRepository.saveAll(participationList);
        return result;
    }

}

