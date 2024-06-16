package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Setting;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.SettingService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.time.LocalDate;

@Controller
public class AuthController {

    private final UserValidator userValidator;

    private final UserService userService;

    private final SettingService settingService;

    private int user_id;

    @Autowired
    public AuthController(UserValidator userValidator, UserService userService, SettingService settingService) {
        this.userValidator = userValidator;
        this.userService = userService;
        this.settingService = settingService;
    }

    @GetMapping("/login")
    public String login(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/finances/" + LocalDate.now();
        }

        return "auth/login";
    }

    @GetMapping("/registration")
    public String registration(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/finances/" + LocalDate.now();
        }

        model.addAttribute("user", new User());

        return "auth/registration";
    }

    @PostMapping("/registration")
    public String register(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model){
        userValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()){
            return "auth/registration";
        }
        userService.save(user);
        this.user_id = user.getId();

        return "redirect:/registration/setup";
    }

    @GetMapping("/registration/setup")
    public String userSetup(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/finances/" + LocalDate.now();
        }

        return "auth/setup";
    }

    @PostMapping("/registration/setup")
    public String performUserSetup(@RequestParam("currency") String currency,
                                   @RequestParam(value = "general-balance", defaultValue = "0" ) int balance,
                                   @RequestParam(value = "display-name", defaultValue = "", required = false) String displayName) {

        CurrencyUnit currencyUnit = Monetary.getCurrency(currency);
        settingService.saveSettingSetup(new Setting(), userService.findUserById(user_id).get(), currencyUnit);
        userService.findUserById(user_id).get().setBalance(balance);

        return "redirect:/login";
    }
}
