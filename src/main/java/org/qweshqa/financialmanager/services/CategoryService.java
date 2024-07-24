package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.CategoryRepository;
import org.qweshqa.financialmanager.repositories.OperationRepository;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.qweshqa.financialmanager.utils.exceptions.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final OperationRepository operationRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, OperationRepository operationRepository) {
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
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

    public List<Category> findAllByUserAndArchivedAndType(User user, boolean archived, CategoryType type){
        return categoryRepository.findAllByUserAndArchivedAndCategoryType(user, archived, type);
    }


    public float getCategoriesTotalByUserAndArchivedAndType(User user, boolean archived, CategoryType type){
        List<Category> categories = categoryRepository.findAllByUserAndArchivedAndCategoryType(user, archived, type);

        return (float) categories.stream().mapToDouble(Category::getBalance).sum();
    }


    public List<Operation> findAllOperationsByUser(Category category, User user){
        return operationRepository.findAllByCategoryAndUser(category, user);
    }

    public List<Operation> findAllOperationsByUserAndDate(Category category, User user, LocalDate date){
        return operationRepository.findAllByDateAndUserAndCategory(date, user, category);
    }

    public List<Operation> findAllOperationsByUserAndMonth(Category category, User user, LocalDate dateWithMonth){
        List<Operation> operations = new ArrayList<>();

        for(int i = 1; i <= dateWithMonth.getMonth().maxLength(); i++){
            operations.addAll(operationRepository.findAllByDateAndUserAndCategory(dateWithMonth.withDayOfMonth(i), user, category));
        }
        return operations;
    }

    public List<Operation> findAllOperationsByUserAndYear(Category category, User user, int year){
        return operationRepository.findAllByYearAndUserAndCategory(year, user, category);
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

    @Transactional
    public void delete(int id){
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void archive(Category category){
        category.setArchived(true);
    }

    @Transactional
    public void unzip(Category category){
        category.setArchived(false);
    }

}
