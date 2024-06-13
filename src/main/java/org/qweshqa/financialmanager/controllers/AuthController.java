package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;

@Controller
public class AuthController {

    private final UserValidator userValidator;

    private final UserService userService;

    @Autowired
    public AuthController(UserValidator userValidator, UserService userService) {
        this.userValidator = userValidator;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(){
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user){
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String register(@ModelAttribute("user") @Valid User user, BindingResult bindingResult){
        userValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()){
            return "auth/registration";
        }
        userService.save(user);

        return "redirect:/finances/" + LocalDate.now();
    }
}
