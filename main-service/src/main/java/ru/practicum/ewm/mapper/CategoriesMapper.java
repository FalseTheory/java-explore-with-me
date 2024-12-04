package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.dto.category.CategoryDto;
import ru.practicum.ewm.model.dto.category.NewCategoryDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoriesMapper {
    CategoryDto mapToCategoryDto(Category category);

    Category mapToCategory(NewCategoryDto newCategoryDto);
}
