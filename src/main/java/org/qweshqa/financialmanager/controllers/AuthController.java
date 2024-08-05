package org.qweshqa.financialmanager.controllers;

import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserValidator userValidator;

    private final UserService userService;

    private final SettingService settingService;

    @Autowired
    public AuthController(UserValidator userValidator, UserService userService, SettingService settingService) {
        this.userValidator = userValidator;
        this.userService = userService;
        this.settingService = settingService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/categories";
        }

        return "auth/login";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/categories";
        }

        model.addAttribute("user", new User());

        return "auth/registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String register(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, @RequestParam("repeated_password") String password, HttpSession session){
        userValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()){
            return "auth/registration";
        }
        else if(!password.equals(user.getPassword())){
            return "redirect:/registration?passwordsDontMatch";
        }

        session.setAttribute("user", user);

        return "redirect:/registration/setup";
    }

    @RequestMapping(value = "/registration/setup", method = RequestMethod.GET)
    public String userSetup(HttpSession session){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/categories";
        }

        User user = (User) session.getAttribute("user");
        if(user == null){
            return "redirect:/registration";
        }

        return "auth/setup";
    }

    @RequestMapping(value = "/registration/setup", method = RequestMethod.POST)
    public String performUserSetup(@RequestParam("currency") String currency, HttpSession session) {

        User user = (User) session.getAttribute("user");

        userService.save(user);
        settingService.saveSettingSetup(new Setting(), user, currency);

        session.removeAttribute("user");

        return "redirect:/login";
    }
}
