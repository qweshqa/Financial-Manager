package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Setting;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.SettingService;
import org.qweshqa.financialmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.money.CurrencyUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/settings")
public class SettingController {

    private final SettingService settingService;

    private final UserService userService;

    @Autowired
    public SettingController(SettingService settingService, UserService userService) {
        this.settingService = settingService;
        this.userService = userService;
    }

    @GetMapping("/currency")
    public String getCurrencySettings(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();

        List<String> currencies = new ArrayList<>(); {
            currencies.add("UAH");
            currencies.add("EUR");
            currencies.add("RUB");
            currencies.add("USD");
        }

        currencies.remove(user.getSetting().getCurrencyUnit());

        // header
        model.addAttribute("user", user);

        model.addAttribute("user_currency", user.getSetting().getCurrencyUnit());
        model.addAttribute("currencies", currencies);
        model.addAttribute("settings", new Setting());
        model.addAttribute("settingsId", user.getSetting().getId());

        return "/settings/currency";
    }
    @RequestMapping(value = "/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String saveCurrencySettings(@PathVariable("id") int id,
                                       @ModelAttribute("settings") Setting setting){
        settingService.updateCurrencySetup(id, setting);

        return "redirect:/settings/currency?success";
    }
}
