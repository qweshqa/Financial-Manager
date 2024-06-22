package org.qweshqa.financialmanager.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FinanceTypeStringConverter implements Converter<String, FinanceType> {
    @Override
    public FinanceType convert(String source) {
        try {
            return FinanceType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid value for FinanceType: " + source);
        }
    }
}
