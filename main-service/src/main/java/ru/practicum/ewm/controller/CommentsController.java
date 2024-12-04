package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.comment.CommentDto;
import ru.practicum.ewm.model.dto.comment.NewCommentDto;
import ru.practicum.ewm.model.util.CommentSearchParams;
import ru.practicum.ewm.service.CommentsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentsController {

    private final CommentsService service;

    @PostMapping("/users/{userId}/events/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable @Positive Long userId,
                             @PathVariable @Positive Long eventId,
                             @RequestBody @Valid NewCommentDto newCommentDto) {
        newCommentDto.setAuthorId(userId);
        newCommentDto.setEventId(eventId);
        log.info("creating comment - {}", newCommentDto);
        CommentDto commentsList = service.create(newCommentDto);
        log.info("created successfully");
        return commentsList;
    }

    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getAllUserCommentsForEvent(@PathVariable @Positive Long userId,
                                                       @RequestParam @Positive Long eventId) {
        log.info("retrieving all user - {} comments for event - {}", userId, eventId);
        return service.getAllForEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDto update(@PathVariable @Positive Long userId,
                             @PathVariable @Positive Long commentId,
                             @RequestBody @Valid NewCommentDto commentDto) {
        commentDto.setAuthorId(userId);
        log.info("updating comment - {}, with body - {}", commentId, commentDto);
        return service.update(commentId, commentDto);
    }

    @GetMapping("/admin/comments")
    public List<CommentDto> getAllComments(@RequestParam(required = false) Long[] users,
                                           @RequestParam(required = false) Long[] events,
                                           @RequestParam(defaultValue = "false") Boolean edited,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        CommentSearchParams searchParams = new CommentSearchParams(users, events, edited);
        return service.getAll(searchParams, PageRequest.of(from, size));
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserComment(@PathVariable @Positive Long userId,
                                  @PathVariable @Positive Long commentId) {
        log.info("user with id - {}, deleting comment with id - {}", userId, commentId);
        service.delete(userId, commentId);
        log.info("deleted successfully");
    }

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long commentId) {
        log.info("deleting comment with id - {}", commentId);
        service.delete(commentId);
        log.info("deleted successfully");
    }

}
