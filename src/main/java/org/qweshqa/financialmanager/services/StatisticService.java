package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.CategoryRepository;
import org.qweshqa.financialmanager.repositories.OperationRepository;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticService {

    private final CategoryRepository categoryRepository;

    private final OperationRepository operationRepository;

    public StatisticService(CategoryRepository categoryRepository, OperationRepository operationRepository) {
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }



}
