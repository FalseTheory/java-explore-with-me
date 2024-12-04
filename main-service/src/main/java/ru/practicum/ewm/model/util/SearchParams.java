package ru.practicum.ewm.model.util;


public record SearchParams(String text,
                           Integer[] categories,
                           Boolean paid,
                           String rangeStart,
                           String rangeEnd,
                           Boolean onlyAvailable,
                           SortType sortType,
                           Integer from,
                           Integer size) {
}
