package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.dto.category.CategoryDto;
import ru.practicum.ewm.model.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoriesService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(Long id);

    CategoryDto update(Long id, NewCategoryDto updatedCategory);

    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto getById(Long id);
}
