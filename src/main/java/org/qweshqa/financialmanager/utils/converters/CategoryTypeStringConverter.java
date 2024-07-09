package org.qweshqa.financialmanager.utils.converters;

import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryTypeStringConverter implements Converter<String, CategoryType> {

    @Override
    public CategoryType convert(String source) {
        try {
            return CategoryType.valueOf(source);
        } catch (IllegalArgumentException e){
            throw new RuntimeException("Invalid value for CategoryType: " + source);
        }
    }
}
