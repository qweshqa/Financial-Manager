package org.qweshqa.financialmanager.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.OperationService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.qweshqa.financialmanager.utils.DateWrapper;
import org.qweshqa.financialmanager.utils.converters.CategoryTypeStringConverter;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.*;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/statistics")
public class StatisticController {

    private final OperationService operationService;

    private final UserService userService;

    private final CategoryService categoryService;

    private final AmountFormatter amountFormatter;

    private final CategoryTypeStringConverter categoryTypeStringConverter;

    public StatisticController(OperationService operationService, UserService userService, CategoryService categoryService, AmountFormatter amountFormatter, CategoryTypeStringConverter categoryTypeStringConverter) {
        this.operationService = operationService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.amountFormatter = amountFormatter;
        this.categoryTypeStringConverter = categoryTypeStringConverter;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewStatistic(@RequestParam(value = "p", defaultValue = "all-time") String period,
                                @RequestParam(value = "t", defaultValue = "expense") String type,
                                @RequestParam(value = "d", defaultValue = "") String day,
                                @RequestParam(value = "m", defaultValue = "") String month,
                                @RequestParam(value = "y", defaultValue = "") String year,
                                Model model){

        DateWrapper dateWrapper = new DateWrapper(LocalDate.now());

        try{
            operationService.configureStringDateValues(year, month, day, period, dateWrapper);
        } catch (DateTimeException e){
            switch (e.getMessage()){
                case "Year period error":
                    return "redirect:/statistics?p=year" +
                            "&y=" + dateWrapper.getDate().getYear();

                case "Month period error":
                    return "redirect:/statistics?p=month" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue();

                case "Day period error":
                    return "redirect:/statistics?p=day" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue() +
                            "&d=" + dateWrapper.getDate().getDayOfMonth();
            }
        }
        CategoryType categoryType = categoryTypeStringConverter.convert(type.toUpperCase());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        LocalDate date = dateWrapper.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

        float cash_flow = 0.0f;
        float total_value = 0.0f;

        switch(period){
            case "all-time":
                cash_flow = categoryService.getCategoriesTotalByUserAndType(user, CategoryType.INCOME)
                        - categoryService.getCategoriesTotalByUserAndType(user, CategoryType.EXPENSE);

                model.addAttribute("displayDate", "All time");

                model.addAttribute("expense_total", categoryService.getCategoriesTotalByUserAndType(user, CategoryType.EXPENSE));
                model.addAttribute("income_total", categoryService.getCategoriesTotalByUserAndType(user, CategoryType.INCOME));

                total_value = categoryService.getCategoriesTotalByUserAndType(user, categoryType);
                long userDaysAfterRegistration = user.getRegisterDate().datesUntil(LocalDate.now().plusDays(1)).count();

                model.addAttribute("daily_average_value", total_value / userDaysAfterRegistration);

                model.addAttribute("categories", categoryService.findAllByUserAndType(user, categoryType)
                        .stream().sorted((category1, category2)
                                -> categoryService.getCategoryTotalByUser(category1, user)
                                .compareTo(categoryService.getCategoryTotalByUser(category2, user))).toList());

                break;

            case "year":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }

                cash_flow = categoryService.getCategoriesTotalByUserAndYearAndType(user, date.getYear(), CategoryType.INCOME)
                        - categoryService.getCategoriesTotalByUserAndYearAndType(user, date.getYear(), CategoryType.EXPENSE);

                model.addAttribute("displayDate", date.getYear());

                model.addAttribute("expense_total", categoryService.getCategoriesTotalByUserAndYearAndType(user, date.getYear(), CategoryType.EXPENSE));
                model.addAttribute("income_total", categoryService.getCategoriesTotalByUserAndYearAndType(user, date.getYear(), CategoryType.INCOME));

                total_value = categoryService.getCategoriesTotalByUserAndYearAndType(user, date.getYear(), categoryType);
                model.addAttribute("daily_average_value", total_value / date.lengthOfYear());
                model.addAttribute("monthly_average_value", total_value / date.lengthOfMonth());

                int yearValue = date.getYear();
                model.addAttribute("categories", categoryService.findAllByUserAndType(user, categoryType)
                        .stream().sorted((category1, category2)
                                -> categoryService.getCategoryTotalByUserAndYear(category1, user, yearValue)
                                .compareTo(categoryService.getCategoryTotalByUserAndYear(category2, user, yearValue))).toList());

                break;

            case "month":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                if(!month.isBlank()){
                    date = date.withMonth(Integer.parseInt(month));
                }

                cash_flow = categoryService.getCategoriesTotalByUserAndYearAndMonthAndType(user, date.getYear(), date.getMonthValue(), CategoryType.INCOME)
                        - categoryService.getCategoriesTotalByUserAndYearAndMonthAndType(user, date.getYear(), date.getMonthValue(), CategoryType.EXPENSE);

                model.addAttribute("displayDate", (date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + date.getYear()));

                model.addAttribute("expense_total", categoryService.getCategoriesTotalByUserAndYearAndMonthAndType(user, date.getYear(), date.getMonthValue(), CategoryType.EXPENSE));
                model.addAttribute("income_total", categoryService.getCategoriesTotalByUserAndYearAndMonthAndType(user, date.getYear(), date.getMonthValue(), CategoryType.INCOME));

                total_value = categoryService.getCategoriesTotalByUserAndYearAndMonthAndType(user, date.getYear(), date.getMonthValue(), categoryType);
                model.addAttribute("daily_average_value", total_value / date.lengthOfMonth());

                yearValue = date.getYear();
                int monthValue = date.getMonthValue();
                model.addAttribute("categories", categoryService.findAllByUserAndType(user, categoryType)
                        .stream().sorted((category1, category2)
                                -> categoryService.getCategoryTotalByUserAndYearAndMonth(category1, user, yearValue, monthValue)
                                .compareTo(categoryService.getCategoryTotalByUserAndYear(category2, user, yearValue))).toList());

                break;

            case "day":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                if(!month.isBlank()){
                    date = date.withMonth(Integer.parseInt(month));
                }
                if(!day.isBlank()){
                    date = date.withDayOfMonth(Integer.parseInt(day));
                }

                cash_flow = categoryService.getCategoriesTotalByUserAndDateAndType(user, date, CategoryType.INCOME)
                        - categoryService.getCategoriesTotalByUserAndDateAndType(user, date, CategoryType.EXPENSE);

                model.addAttribute("displayDate", date.format(formatter));

                model.addAttribute("expense_total", categoryService.getCategoriesTotalByUserAndDateAndType(user, date, CategoryType.EXPENSE));
                model.addAttribute("income_total", categoryService.getCategoriesTotalByUserAndDateAndType(user, date, CategoryType.INCOME));

                LocalDate dateCopy = date;
                model.addAttribute("categories", categoryService.findAllByUserAndType(user, categoryType)
                        .stream().sorted((category1, category2)
                                -> categoryService.getCategoryTotalByUserAndDate(category1, user, dateCopy)
                                .compareTo(categoryService.getCategoryTotalByUserAndDate(category2, user, dateCopy))).toList());

                break;

        }

        model.addAttribute("cash_flow", cash_flow);

        model.addAttribute("type", type);

        model.addAttribute("categoryService", categoryService);

        model.addAttribute("user", user);
        model.addAttribute("settings", user.getSetting());

        model.addAttribute("date", date);
        model.addAttribute("period", period);

        model.addAttribute("amountFormatter", amountFormatter);

        return "/statistic/main";
    }

    @RequestMapping(value = "/generateChart", method = RequestMethod.GET)
    public void generateAverageValuesChart(@RequestParam("p") String period, @RequestParam("t") String type,
                              @RequestParam(value = "d", defaultValue = "") String day,
                              @RequestParam(value = "m", defaultValue = "") String month,
                              @RequestParam(value = "y", defaultValue = "") String year,
                              HttpServletResponse response) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        CategoryType categoryType = categoryTypeStringConverter.convert(type.toUpperCase());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

        List<Operation> operations = new ArrayList<>();
        switch(period){
            case "all-time":
                operations = operationService.findAllByUserAndCategoryType(user, categoryType);

                for(int i = 0; i < operations.size(); i++){
                    dataset.addValue(operations.get(i).getAmount(), "Amount", operations.get(i).getDate().format(formatter));
                }

                break;

            case "year":
                operations = operationService.findAllByUserAndYearAndCategoryType(user, Integer.parseInt(year), categoryType);

                for(int i = 0; i < operations.size(); i++){
                    dataset.addValue(operations.get(i).getAmount(), "Amount", operations.get(i).getDate().getYear() + "");
                }

                break;

            case "month":
                operations = operationService.findAllByUserAndYearAndMonthAndCategoryType(user, Integer.parseInt(year), Integer.parseInt(month), categoryType);

                for(int i = 0; i < operations.size(); i++){
                    dataset.addValue(operations.get(i).getAmount(), "Amount", operations.get(i).getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + operations.get(i).getYear());
                }

                break;

            case "day":
                operations = operationService.findAllByUserAndDateAndCategoryType(user, LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)), categoryType);

                for(int i = 0; i < operations.size(); i++){
                    dataset.addValue(operations.get(i).getAmount(), "Amount", operations.get(i).getDate().format(formatter));
                }

                break;
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "",
                "",
                "",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        chart.setBackgroundPaint(Color.decode("#15181c"));

        CategoryPlot plot = chart.getCategoryPlot();

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        if(categoryType == CategoryType.EXPENSE){
            renderer.setSeriesPaint(0, new Color(255, 124, 124));
        }
        else{
            renderer.setSeriesPaint(0, new Color(124, 255, 178));
        }

        response.setContentType("image/png");
        ChartUtils.writeChartAsPNG(response.getOutputStream(), chart, 800, 600);
    }
}