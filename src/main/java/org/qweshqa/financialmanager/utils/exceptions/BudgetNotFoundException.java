package org.qweshqa.financialmanager.utils.exceptions;

public class BudgetNotFoundException extends RuntimeException{
    public BudgetNotFoundException(String message) {
        super(message);
    }
}
