package com.finsight.app.service;

import com.finsight.app.dto.UpdateCategoryRequest;
import com.finsight.app.exception.UnauthorizedAccessException;
import com.finsight.app.model.Category;
import com.finsight.app.repository.CategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final UserService userService;

  @Autowired
  public CategoryService(CategoryRepository categoryRepository, UserService userService) {
    this.categoryRepository = categoryRepository;
    this.userService = userService;
  }

  public List<com.finsight.app.dto.Category> getCategories(String userId) throws Exception {
    userService.getCurrentUser(userId);

    return categoryRepository.findByUserId(userId).stream()
        .map(this::transformToDto)
        .collect(Collectors.toList());
  }

  public com.finsight.app.dto.Category createCategory(Category category, String userId)
      throws Exception {
    userService.getCurrentUser(userId);

    category.setUserId(userId);

    Category createdCategory = categoryRepository.save(category);
    return transformToDto(createdCategory);
  }

  public com.finsight.app.dto.Category updateCategory(
      String categoryId, UpdateCategoryRequest updateRequest, String userId) throws Exception {
    userService.getCurrentUser(userId);

    Category categoryToUpdate =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

    if (!categoryToUpdate.getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You are not authorized to update this category");
    }

    if (updateRequest.getColor() != null) {
      categoryToUpdate.setColor(updateRequest.getColor());
    }
    if (updateRequest.getIcon() != null) {
      categoryToUpdate.setIcon(updateRequest.getIcon());
    }
    if (updateRequest.getName() != null) {
      categoryToUpdate.setName(updateRequest.getName());
    }

    Category updatedCategory = categoryRepository.save(categoryToUpdate);
    return transformToDto(updatedCategory);
  }

  public void deleteCategory(String categoryId, String userId) throws Exception {
    userService.getCurrentUser(userId);

    Category categoryToUpdate =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    if (!categoryToUpdate.getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You are not authorized to delete this category");
    }
    categoryRepository.deleteById(categoryId);
  }

  private com.finsight.app.dto.Category transformToDto(Category category) {
    return new com.finsight.app.dto.Category(
        category.getCategoryId(),
        category.getName(),
        category.getColor(),
        category.getIcon(),
        category.isDefault(),
        category.getUserId());
  }
}
