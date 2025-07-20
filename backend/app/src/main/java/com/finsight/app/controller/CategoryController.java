package com.finsight.app.controller;

import com.finsight.app.dto.Category;
import com.finsight.app.dto.UpdateCategoryRequest;
import com.finsight.app.service.CategoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/categories")
public class CategoryController {
  private final CategoryService categoryService;

  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping()
  public ResponseEntity<List<Category>> getCategories(@PathVariable String userId)
      throws Exception {
    return ResponseEntity.ok(categoryService.getCategories(userId));
  }

  @PostMapping()
  public ResponseEntity<Category> createCategory(
      @RequestBody com.finsight.app.model.Category category, @PathVariable String userId)
      throws Exception {
    com.finsight.app.dto.Category accountCreated = categoryService.createCategory(category, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(accountCreated);
  }

  @PutMapping("/{categoryId}")
  public ResponseEntity<Category> updateCategory(
      @PathVariable String categoryId,
      @RequestBody UpdateCategoryRequest updateRequest,
      @PathVariable String userId)
      throws Exception {
    com.finsight.app.dto.Category updatedCategory =
        categoryService.updateCategory(categoryId, updateRequest, userId);
    return ResponseEntity.ok(updatedCategory);
  }

  @DeleteMapping("/{categoryId}")
  public ResponseEntity<String> deleteCategory(
      @PathVariable String categoryId, @PathVariable String userId) throws Exception {
    categoryService.deleteCategory(categoryId, userId);
    return ResponseEntity.ok("Success, account with id " + categoryId + " deleted");
  }
}
