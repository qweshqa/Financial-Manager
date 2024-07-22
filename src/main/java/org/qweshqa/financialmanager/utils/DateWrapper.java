package org.qweshqa.financialmanager.utils;

import java.time.LocalDate;

public class DateWrapper {
    private LocalDate date;

    public DateWrapper(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
