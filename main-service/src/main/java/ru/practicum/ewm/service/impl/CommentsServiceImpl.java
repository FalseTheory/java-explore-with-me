package ru.practicum.ewm.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.CommentsMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.QComment;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.dto.comment.CommentDto;
import ru.practicum.ewm.model.dto.comment.NewCommentDto;
import ru.practicum.ewm.model.exception.ConflictException;
import ru.practicum.ewm.model.exception.NotFoundException;
import ru.practicum.ewm.model.util.CommentSearchParams;
import ru.practicum.ewm.model.util.EventState;
import ru.practicum.ewm.repository.CommentsRepository;
import ru.practicum.ewm.repository.EventsRepository;
import ru.practicum.ewm.repository.UsersRepository;
import ru.practicum.ewm.service.CommentsService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final EventsRepository eventsRepository;
    private final UsersRepository usersRepository;
    private final CommentsMapper mapper;

    @Override
    public CommentDto create(NewCommentDto newCommentDto) {
        User user = usersRepository.findById(newCommentDto.getAuthorId())
                .orElseThrow(() -> new NotFoundException("User with id - " + newCommentDto.getAuthorId() + " not found"));
        Event event = eventsRepository.findById(newCommentDto.getEventId())
                .orElseThrow(() -> new NotFoundException("Event with id - " + newCommentDto.getEventId() + " not found"));

        if (event.getState() == EventState.PENDING) {
            throw new ConflictException("You cant comment on unpublished event");
        }
        Comment comment = mapper.mapNewToComment(newCommentDto, event, user);


        return mapper.mapToDto(commentsRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllForEvent(Long userId, Long eventId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - " + userId + " not found"));
        eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id - " + eventId + " not found"));
        BooleanExpression query = QComment.comment.event.id.eq(eventId);
        List<CommentDto> commentDtoList = new ArrayList<>();
        commentsRepository.findAll(query).iterator()
                .forEachRemaining(comment -> commentDtoList.add(mapper.mapToDto(comment)));

        return commentDtoList;
    }

    @Override
    public void delete(Long userId, Long commentId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - " + userId + " not found"));
        Comment comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id - " + commentId + " not found"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("user don't have access to this comment");
        }
        commentsRepository.deleteById(commentId);
    }

    @Override
    public void delete(Long commentId) {
        commentsRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAll(CommentSearchParams params, Pageable pageable) {
        BooleanBuilder query = new BooleanBuilder();
        QComment qComment = QComment.comment;
        if(params.users()!=null) {
            query.or(qComment.author.id.in(params.users()));
        }
        if(params.events()!=null) {
            query.or(qComment.event.id.in(params.users()));
        }
        if(params.edited()){
            query.and(qComment.edited.isTrue());
        }else {
            query.and(qComment.edited.isFalse());
        }
        return commentsRepository.findAll(query, pageable).stream()
                .map(mapper::mapToDto)
                .toList();
    }

    @Override
    public CommentDto update(Long commentId, NewCommentDto updateBody) {
        usersRepository.findById(updateBody.getAuthorId())
                .orElseThrow(() -> new NotFoundException("User with id - " + updateBody.getAuthorId() + " not found"));
        Comment comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id - " + commentId + " not found"));
        if (!comment.getAuthor().getId().equals(updateBody.getAuthorId())) {
            throw new ConflictException("user don't have access to this comment");
        }
        comment.setText(updateBody.getText());
        comment.setEdited(true);
        return mapper.mapToDto(comment);
    }
}
