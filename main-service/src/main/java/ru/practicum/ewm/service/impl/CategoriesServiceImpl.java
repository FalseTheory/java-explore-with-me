package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.CategoriesMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.dto.category.CategoryDto;
import ru.practicum.ewm.model.dto.category.NewCategoryDto;
import ru.practicum.ewm.model.exception.NotFoundException;
import ru.practicum.ewm.repository.CategoriesRepository;
import ru.practicum.ewm.service.CategoriesService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository repository;
    private final CategoriesMapper mapper;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        return mapper.mapToCategoryDto(repository.save(mapper.mapToCategory(newCategoryDto)));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public CategoryDto update(Long id, NewCategoryDto updatedCategory) {
        Category oldCat = repository.findById(id)
                .orElseThrow((() -> new NotFoundException("category with id -" + id + " not found")));
        oldCat.setName(updatedCategory.getName());
        return mapper.mapToCategoryDto(repository.save(oldCat));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Pageable pageable) {
        Page<Category> categories = repository.findAll(pageable);
        return categories.get().map(mapper::mapToCategoryDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("category with id -" + id + " not found"));
        return mapper.mapToCategoryDto(category);
    }
}
