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


    public float getCategoriesTotalByUserAndType(User user, CategoryType type){
        return (float) operationRepository.findAllByUserAndCategoryType(user, type)
                .stream().mapToDouble(Operation::getAmount).sum();
    }

    public float getCategoriesTotalByUserAndYearAndType(User user, int year, CategoryType type){
        return (float) operationRepository.findAllByYearAndUserAndCategoryType(year, user, type)
                .stream().mapToDouble(Operation::getAmount).sum();
    }

    public float getCategoriesTotalByUserAndYearAndMonthAndType(User user, int year, int month, CategoryType type){
        return (float) operationRepository.findAllByYearAndMonthAndUserAndCategoryType(year, month, user, type)
                .stream().mapToDouble(Operation::getAmount).sum();
    }

    public float getCategoriesTotalByUserAndDateAndType(User user, LocalDate date, CategoryType categoryType){
        return (float) operationRepository.findAllByDateAndUserAndCategoryType(date, user, categoryType)
                .stream().mapToDouble(Operation::getAmount).sum();
    }

    public float getCategoryTotalByUser(Category category, User user){
        return (float) operationRepository.findAllByCategoryAndUser(category, user)
                .stream().mapToDouble(Operation::getAmount).sum();
    }

    public float getCategoryTotalByUserAndYear(Category category, User user, int year){
        return (float) operationRepository.findAllByYearAndUserAndCategory(year, user, category)
                .stream().mapToDouble(Operation::getAmount).sum();
    }

    public float getCategoryTotalByUserAndYearAndMonth(Category category, User user, int year, int month){
        return (float) operationRepository.findAllByYearAndMonthAndUserAndCategory(year, month, user, category)
                .stream().mapToDouble(Operation::getAmount).sum();
    }

    public float getCategoryTotalByUserAndDate(Category category, User user, LocalDate date){
        return (float) operationRepository.findAllByDateAndUserAndCategory(date, user, category)
                .stream().mapToDouble(Operation::getAmount).sum();
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
