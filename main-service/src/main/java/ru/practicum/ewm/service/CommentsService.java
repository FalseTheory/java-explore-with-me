package ru.practicum.ewm.service;

import ru.practicum.ewm.model.dto.comment.CommentDto;
import ru.practicum.ewm.model.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentsService {

    CommentDto create(NewCommentDto newCommentDto);

    List<CommentDto> getAllForEvent(Long userId, Long eventId);

    void delete(Long userId, Long commentId);

    void delete(Long commentId);

    CommentDto update(Long commentId, NewCommentDto updateBody);
}
