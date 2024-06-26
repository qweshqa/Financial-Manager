package org.qweshqa.financialmanager.utils;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Component
public class AmountFormatter {

    public String formatAmount(float amount){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("#,##0.###", symbols);
        return formatter.format(amount);
    }
}
