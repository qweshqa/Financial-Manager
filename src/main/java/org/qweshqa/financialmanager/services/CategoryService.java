package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.CategoryRepository;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllByUser(User user){
        return categoryRepository.findAllByUser(user);
    }

    public Optional<Category> findById(int id){
        return categoryRepository.findById(id);
    }

    public List<Category> findAllByTypeAndUser(CategoryType type, User user){
        return categoryRepository.findAllByCategoryTypeAndUser(type, user);
    }
}
