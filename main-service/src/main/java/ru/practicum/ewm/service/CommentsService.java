package ru.practicum.ewm.service;

import ru.practicum.ewm.model.dto.comment.CommentDto;
import ru.practicum.ewm.model.dto.comment.NewCommentDto;
import ru.practicum.ewm.model.util.CommentSearchParams;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentsService {

    CommentDto create(NewCommentDto newCommentDto);

    List<CommentDto> getAllForEvent(Long userId, Long eventId);

    void delete(Long userId, Long commentId);

    void delete(Long commentId);

    List<CommentDto> getAll(CommentSearchParams searchParams, Pageable pageable);

    CommentDto update(Long commentId, NewCommentDto updateBody);
}
