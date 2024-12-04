package ru.practicum.ewm.model.util;

public record AdminSearchParams(Long[] users,
                                String[] states,
                                Long[] categories,
                                String rangeStart,
                                String rangeEnd,
                                Integer from,
                                Integer size) {
}
