package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.category.CategoryDto;
import ru.practicum.ewm.model.dto.category.NewCategoryDto;
import ru.practicum.ewm.service.CategoriesService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoriesController {
    private final CategoriesService service;


    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("creating category - {}", newCategoryDto);
        CategoryDto createdCategory = service.create(newCategoryDto);
        log.info("created successfully");
        return createdCategory;
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long catId) {
        log.info("deleting category with id - {}", catId);
        service.delete(catId);
        log.info("category with id - {} has been deleted", catId);

    }

    @PatchMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable @Positive Long catId,
                              @RequestBody @Valid NewCategoryDto updatedCategory) {
        log.info("updating category with id - {}, and body - {}", catId, updatedCategory);
        CategoryDto updated = service.update(catId, updatedCategory);
        log.info("updated successfully");
        return updated;
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("retrieving all categories");
        return service.getAll(PageRequest.of(from, size));

    }

    @GetMapping("/categories/{catId}")
    public CategoryDto get(@PathVariable Long catId) {
        log.info("retrieving category witn id - {}", catId);
        return service.getById(catId);
    }

}
