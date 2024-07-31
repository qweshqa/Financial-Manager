package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Setting;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.AccountService;
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
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserValidator userValidator;

    private final UserService userService;

    private final SettingService settingService;

    private final AccountService accountService;

    private int user_id;

    @Autowired
    public AuthController(UserValidator userValidator, UserService userService, SettingService settingService,
                          AccountService accountService) {
        this.userValidator = userValidator;
        this.userService = userService;
        this.settingService = settingService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/finances/show";
        }

        return "auth/login";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/finances/show";
        }

        model.addAttribute("user", new User());

        return "auth/registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String register(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model){
        userValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()){
            return "auth/registration";
        }
        userService.save(user);

        this.user_id = user.getId();

        return "redirect:/registration/setup";
    }

    @RequestMapping(value = "/registration/setup", method = RequestMethod.GET)
    public String userSetup(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/finances/show";
        }

        return "auth/setup";
    }

    @RequestMapping(value = "/registration/setup", method = RequestMethod.POST)
    public String performUserSetup(@RequestParam("currency") String currency) {

        settingService.saveSettingSetup(new Setting(), userService.findUserById(user_id), currency);

        return "redirect:/login";
    }
}
