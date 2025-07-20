package com.finsight.app.service;

import com.finsight.app.dto.UpdateCategoryRequest;
import com.finsight.app.exception.UnauthorizedAccessException;
import com.finsight.app.model.Category;
import com.finsight.app.model.User;
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
    User user = userService.getCurrentUser(userId);

    return categoryRepository.findAll().stream()
        .filter(category -> category.getUser().getEmail().equals(user.getEmail()))
        .map(this::transformToDto)
        .collect(Collectors.toList());
  }

  public com.finsight.app.dto.Category createCategory(Category category, String userId)
      throws Exception {
    User loggedUser = userService.getCurrentUser(userId);

    category.setUser(loggedUser);

    Category createdCategory = categoryRepository.save(category);
    return transformToDto(createdCategory);
  }

  public com.finsight.app.dto.Category updateCategory(
      String categoryId, UpdateCategoryRequest updateRequest, String userId) throws Exception {
    com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);
    Category categoryToUpdate =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + categoryId));

    if (!categoryToUpdate.getUser().getId().equals(loggedUser.getId())) {
      throw new UnauthorizedAccessException("You are not authorized to update this account");
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
    com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);
    Category categoryToUpdate =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + categoryId));
    if (!categoryToUpdate.getUser().getId().equals(loggedUser.getId())) {
      throw new UnauthorizedAccessException("You are not authorized to update this account");
    }
    categoryRepository.deleteById(categoryId);
  }

  private com.finsight.app.dto.Category transformToDto(Category category) {
    return new com.finsight.app.dto.Category(
        category.getId(),
        category.getName(),
        category.getColor(),
        category.getIcon(),
        category.isDefault(),
        category.getUser().getId());
  }
}
