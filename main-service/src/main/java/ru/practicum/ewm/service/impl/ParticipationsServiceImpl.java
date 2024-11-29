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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationsServiceImpl implements ParticipationsService {

    private final ParticipationMapper mapper;
    private final UsersRepository usersRepository;
    private final ParticipationRepository participationRepository;
    private final EventsRepository eventsRepository;

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
    @Transactional
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
        participation.setCreated(now);
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

        return mapper.mapToDto(participation);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        Participation participation = participationRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("request with id - " + requestId + " not found"));
        System.out.println(participation.getRequester().getId());
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
    @Transactional
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
        if (limit == 0 && !event.getRequestModeration()) {
            List<Participation> participationList = participationRepository.findAllById(updateRequest.getRequestIds());
            for (Participation participation : participationList) {
                participation.setStatus(RequestStatus.CONFIRMED);
                result.getConfirmedRequests().add(mapper.mapToDto(participation));
                participants++;
            }
            participationRepository.saveAll(participationList);
            event.setConfirmedRequests(participants);
            eventsRepository.save(event);
            return result;

        } else if (limit == 0) {
            List<Participation> participationList = participationRepository.findAllById(updateRequest.getRequestIds());
            for (Participation participation : participationList) {
                if (participation.getStatus() != RequestStatus.PENDING) {
                    throw new ConflictException("You can only change pending requests");
                }
                participation.setStatus(updateRequest.getStatus());
                if (participation.getStatus() == RequestStatus.CONFIRMED) {
                    participants++;
                    result.getConfirmedRequests().add(mapper.mapToDto(participation));
                }
                if (participation.getStatus() == RequestStatus.REJECTED) {
                    result.getRejectedRequests().add(mapper.mapToDto(participation));
                }

            }
            participationRepository.saveAll(participationList);
            event.setConfirmedRequests(participants);
            eventsRepository.save(event);
            return result;
        } else {
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
}
