package com.finsight.app.controller;

import com.finsight.app.dto.Category;
import com.finsight.app.dto.UpdateCategoryRequest;
import com.finsight.app.service.CategoryService;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/categories")
public class CategoryController {
  private final CategoryService categoryService;

  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping()
  public ResponseEntity<List<Category>> getCategories(HttpServletRequest request)
      throws Exception {
      String userId = (String) request.getSession().getAttribute("userId");
      if (userId == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    return ResponseEntity.ok(categoryService.getCategories(userId));
  }

  @PostMapping()
  public ResponseEntity<Category> createCategory(
      @RequestBody com.finsight.app.model.Category category, HttpServletRequest request)
      throws Exception {
      String userId = (String) request.getSession().getAttribute("userId");
      if (userId == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    com.finsight.app.dto.Category accountCreated = categoryService.createCategory(category, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(accountCreated);
  }

  @PutMapping("/{categoryId}")
  public ResponseEntity<Category> updateCategory(
      @PathVariable String categoryId,
      @RequestBody UpdateCategoryRequest updateRequest, HttpServletRequest request)
      throws Exception {
      String userId = (String) request.getSession().getAttribute("userId");
      if (userId == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    com.finsight.app.dto.Category updatedCategory =
        categoryService.updateCategory(categoryId, updateRequest, userId);
    return ResponseEntity.ok(updatedCategory);
  }

  @DeleteMapping("/{categoryId}")
  public ResponseEntity<String> deleteCategory(
      @PathVariable String categoryId, HttpServletRequest request) throws Exception {
      String userId = (String) request.getSession().getAttribute("userId");
      if (userId == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    categoryService.deleteCategory(categoryId, userId);
    return ResponseEntity.ok("Success, account with id " + categoryId + " deleted");
  }
}
