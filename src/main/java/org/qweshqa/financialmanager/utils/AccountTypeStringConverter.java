package org.qweshqa.financialmanager.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountTypeStringConverter implements Converter<String, AccountType> {
    @Override
    public AccountType convert(String source) {
        try {
            return AccountType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid value for AccountType: " + source);
        }
    }
}
