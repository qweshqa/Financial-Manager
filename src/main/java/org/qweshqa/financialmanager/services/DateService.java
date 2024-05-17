package org.qweshqa.financialmanager.services;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DateService {

    public String formatDate(String date){

        Pattern pattern = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
        Matcher matcher = pattern.matcher(date);

        if (matcher.matches()) {
            String year = matcher.group(1);
            String month = String.format("%02d", Integer.parseInt(matcher.group(2)));
            String day = String.format("%02d", Integer.parseInt(matcher.group(3)));
            return year + "-" + month + "-" + day;
        } else {
            System.out.println("Неправильный формат даты");
            return date;
        }
    }

    public List<MonthDay> getMonthDaysInList(Month month){

        int lastDayOfMonth = month.length(false);
        return IntStream.rangeClosed(1, lastDayOfMonth)
                .mapToObj(dayOfMonth -> MonthDay.of(month, dayOfMonth))
                .filter(monthDay -> !monthDay.atYear(LocalDate.now().getYear()).isAfter(LocalDate.now()))
                .collect(Collectors.toList());
    }
}
