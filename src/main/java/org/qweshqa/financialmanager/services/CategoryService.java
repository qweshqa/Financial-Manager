package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.CategoryRepository;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.qweshqa.financialmanager.utils.exceptions.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category findById(int id){
        Optional<Category> category = categoryRepository.findById(id);

        if(category.isEmpty()){
            throw new CategoryNotFoundException("Category with id " + id + " doesn't exist.");
        }

        return category.get();
    }

    public List<Category> findAllByUser(User user){
        return categoryRepository.findAllByUser(user);
    }

    public List<Category> findAllByTypeAndUser(CategoryType type, User user){
        return categoryRepository.findAllByCategoryTypeAndUser(type, user);
    }

    @Transactional
    public void save(Category category){
        categoryRepository.save(category);
    }

    @Transactional
    public void update(Category toUpdate, Category updated){
        updated.setId(toUpdate.getId());
        categoryRepository.save(updated);
    }
}
