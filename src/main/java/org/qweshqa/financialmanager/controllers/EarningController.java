package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Earning;
import org.qweshqa.financialmanager.services.DateService;
import org.qweshqa.financialmanager.services.EarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/earning")
public class EarningController {
    private final DateService dateService;

    private final EarningService earningService;

    @Autowired
    public EarningController(EarningService earningService, DateService dateService) {
        this.earningService = earningService;
        this.dateService = dateService;
    }

    @GetMapping("/today")
    public String getEarning(Model model){
        List<Earning> earningList = earningService.index(LocalDate.now());

        String monthNameLowerCase = LocalDate.now().getMonth().toString().substring(1).toLowerCase();
        String monthNameFirstLetter = LocalDate.now().getMonth().toString().substring(0, 1).toUpperCase();

        model.addAttribute("monthName", monthNameFirstLetter + monthNameLowerCase);
        // date navigation
        model.addAttribute("day", LocalDate.now());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(LocalDate.now().getMonth()));

        // spending total
        model.addAttribute("earning_total", earningService.getEarningTotalByDate(LocalDate.now()));

        // index
        model.addAttribute("earningList", earningList);

        // spending to create
        model.addAttribute("newEarning", new Earning());

        return "earning/list";
    }
    @GetMapping("/{date}")
    public String getEarningByDate(@PathVariable("date") String date, Model model){
        date = dateService.formatDate(date);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        List<Earning> earningList = earningService.index(localDate);

        String monthNameLowerCase = localDate.getMonth().toString().substring(1).toLowerCase();
        String monthNameFirstLetter = localDate.getMonth().toString().substring(0, 1).toUpperCase();
        model.addAttribute("monthName", monthNameFirstLetter + monthNameLowerCase);

        // date navigation
        model.addAttribute("day", localDate);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(localDate.getMonth()));

        // spending total
        model.addAttribute("earning_total", earningService.getEarningTotalByDate(localDate));

        // index
        model.addAttribute("earningList", earningList);

        // spending to create
        model.addAttribute("newEarning", new Earning());

        return "earning/list";
    }

    @PostMapping("/today")
    public String addEarning(@ModelAttribute("newEarning") Earning earning){
        earning.setDate(LocalDate.now());
        earningService.save(earning);

        return "redirect:/earning/today";
    }

    @PostMapping("/{id}")
    public String deleteEarning(@PathVariable("id") int id){
        earningService.delete(id);

        return "redirect:/earning/today";
    }
}
