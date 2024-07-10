package org.qweshqa.financialmanager.utils.converters;

import org.qweshqa.financialmanager.utils.enums.OperationType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OperationTypeStringConverter implements Converter<String, OperationType> {
    @Override
    public OperationType convert(String source) {
        try {
            return OperationType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid value for OperationType: " + source);
        }
    }
}
