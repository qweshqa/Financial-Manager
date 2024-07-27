package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findAllByUser(User user);

    List<Category> findAllByUserAndCategoryType(User user, CategoryType type);

    List<Category> findAllByUserAndArchivedAndCategoryType(User user, boolean archived, CategoryType type);
}
