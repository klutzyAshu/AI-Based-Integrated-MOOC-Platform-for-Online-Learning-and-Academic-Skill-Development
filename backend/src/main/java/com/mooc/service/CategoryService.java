package com.mooc.service;

import com.mooc.dto.CategoryRequest;
import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.Category;
import com.mooc.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) { this.categoryRepository = categoryRepository; }

    public List<Category> findAll() { return categoryRepository.findAll(org.springframework.data.domain.Sort.by("name").ascending()); }

    public Category create(CategoryRequest request) {
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) throw new IllegalArgumentException("A category with this name already exists");
        return categoryRepository.save(Category.builder().name(name).description(request.description()).build());
    }

    public Category update(Long id, CategoryRequest request) {
        Category category = findById(id);
        String name = request.name().trim();
        if (!category.getName().equalsIgnoreCase(name) && categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("A category with this name already exists");
        }
        category.setName(name);
        category.setDescription(request.description());
        return categoryRepository.save(category);
    }

    public void delete(Long id) { categoryRepository.delete(findById(id)); }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Category", id));
    }
}
