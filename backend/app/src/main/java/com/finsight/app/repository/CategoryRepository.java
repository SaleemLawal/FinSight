package com.finsight.app.repository;

import com.finsight.app.model.Category;
import com.finsight.app.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
  List<Category> findByUser(User user);
}
