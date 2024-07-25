package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Integer> {

    List<Operation> findAllByUser(User user);

    @Query("SELECT o FROM Operation o JOIN o.category c WHERE c.categoryType = :type AND o.user = :user")
    List<Operation> findAllByUserAndCategoryType(@Param("user") User user, @Param("type") CategoryType categoryType);

    @Query("SELECT o FROM Operation o JOIN o.category c WHERE c.categoryType = :type AND o.year = :year AND o.user = :user")
    List<Operation> findAllByYearAndUserAndCategoryType(@Param("year") int year, @Param("user") User user, @Param("type") CategoryType categoryType);

    @Query("SELECT o FROM Operation o JOIN o.category c WHERE c.categoryType = :type AND o.year = :year AND o.month = :month AND o.user = :user")
    List<Operation> findAllByYearAndMonthAndUserAndCategoryType(@Param("year") int year, @Param("month") int month, @Param("user") User user, @Param("type")CategoryType type);

    @Query("SELECT o FROM Operation o JOIN o.category c WHERE c.categoryType = :type AND o.date = :date AND o.user = :user")
    List<Operation> findAllByDateAndUserAndCategoryType(@Param("date") LocalDate date, @Param("user") User user, @Param("type") CategoryType categoryType);


    List<Operation> findAllByDateAndUser(LocalDate date, User user);

    List<Operation> findAllByDateAndUserAndInvolvedAccount(LocalDate date, User user, Account account);

    List<Operation> findAllByDateAndUserAndCategory(LocalDate date, User user, Category category);


    List<Operation> findAllByYearAndUser(int year, User user);

    List<Operation> findAllByYearAndUserAndInvolvedAccount(int year, User user, Account account);

    List<Operation> findAllByYearAndUserAndCategory(int year, User user, Category category);


    List<Operation> findAllByYearAndMonthAndUserAndCategory(int year, int month, User user, Category category);


    List<Operation> findAllByInvolvedAccountAndUser(Account involvedAccount, User user);

    List<Operation> findAllByCategoryAndUser(Category category, User user);
}
