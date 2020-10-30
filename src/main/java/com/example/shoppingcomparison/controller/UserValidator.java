package com.example.shoppingcomparison.controller;

import com.example.shoppingcomparison.auth.ApplicationUserDetails;
import com.example.shoppingcomparison.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    private ApplicationUserService applicationUserService;

    @Autowired
    public UserValidator(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return ApplicationUserDetails.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ApplicationUserDetails user = (ApplicationUserDetails) o;

//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
//        if (user.getUsername().length() < 6 || user.getUsername().length() > 32) {
//            errors.rejectValue("username", "Size.userForm.username", "Error from Validator");
//        }

//        if (applicationUserService.loadUserByUsername(user.getUserName()) != null) {
//            errors.rejectValue("userName", "Duplicate.userForm.userName");
//        }

//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
//        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
//            errors.rejectValue("password", "Size.userForm.password");
//        }

//        if (!user.getPasswordConfirm().equals(user.getPassword())) {
//            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
//        }
    }
}
