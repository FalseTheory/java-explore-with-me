package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.category.CategoryDto;
import ru.practicum.ewm.model.dto.category.NewCategoryDto;
import ru.practicum.ewm.service.CategoriesService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoriesService service;


    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody NewCategoryDto newCategoryDto) {
        log.info("creating category - {}", newCategoryDto);
        return service.create(newCategoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        log.info("deleting category with id - {}", catId);
        service.delete(catId);

    }

    @PatchMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable Long catId,
                              @RequestBody NewCategoryDto updatedCategory) {
        log.info("updating category with id - {}, and body - {}", catId, updatedCategory);

        return service.update(catId, updatedCategory);
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
