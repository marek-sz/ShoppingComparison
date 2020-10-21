package com.example.shoppingcomparison.controller;

import com.example.shoppingcomparison.auth.ApplicationUserDetails;
import com.example.shoppingcomparison.auth.ApplicationUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final ApplicationUserService applicationUserService;
    private final UserValidator userValidator;

    public UserController(ApplicationUserService applicationUserService, UserValidator userValidator) {
        this.applicationUserService = applicationUserService;
        this.userValidator = userValidator;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new ApplicationUserDetails());
        return "registration";
    }

//    @PostMapping("/registration")
//    public String registration(@ModelAttribute("userForm") ApplicationUserDetails userForm,
//                               BindingResult bindingResult) {
//        userValidator.validate(userForm, bindingResult);
//
//        if (bindingResult.hasErrors()) {
//            return "registration";
//        }
//
//        applicationUserService.addUser(userForm);
//        return "redirect:/";
//    }

    @PostMapping("/registration")
    public String submit(@ModelAttribute ApplicationUserDetails userForm, Model model) {
        model.addAttribute("userForm", userForm);
        applicationUserService.addUser(userForm);
        return "redirect:/";
    }

    //todo: implement this
    @GetMapping("/addToFavorites")
    public String addToFavorites() {
        return "index";
    }
}