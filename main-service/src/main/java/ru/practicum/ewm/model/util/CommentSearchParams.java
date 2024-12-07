package ru.practicum.ewm.model.util;

public record CommentSearchParams(Long[] users, Long[] events, Boolean edited) {
}
