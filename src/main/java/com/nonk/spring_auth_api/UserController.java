package com.nonk.spring_auth_api;

import com.nonk.spring_auth_api.api.ApiResponse;
import com.nonk.spring_auth_api.api.LoginRequest;
import com.nonk.spring_auth_api.api.RegistrationRequest;
import com.nonk.spring_auth_api.api.SubmitRestorationCodeRequest;
import com.nonk.spring_auth_api.exceptions.*;
import com.nonk.spring_auth_api.user.User;
import com.nonk.spring_auth_api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse register(@RequestBody RegistrationRequest request) {
        try {
            // Input data cleanup.
            request.setFirstName(request.getFirstName().trim());
            request.setLastName(request.getLastName().trim());
            request.setPhoneNumber(request.getPhoneNumber().trim());
            request.setEmail(request.getEmail().trim());

            userService.register(request);

            return ApiResponse.SUCCESS;
        } catch (InvalidPasswordException e) {
            return new ApiResponse(1, e);
        } catch (InvalidCredentialsException e) {
            return new ApiResponse(2, e);
        } catch (EmailInUseException e) {
            return new ApiResponse(3, e);
        }
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest request) {
        try {
            User user = userService.retrieveUser(request.getEmail().trim());

            if (!userService.matchPassword(user, request.getPassword())) {
                throw new IncorrectPasswordException();
            }

            return ApiResponse.SUCCESS;
        } catch (UserNotFoundException e) {
            return new ApiResponse(1, e);
        } catch (IncorrectPasswordException e) {
            return new ApiResponse(2, e);
        }
    }

    @PostMapping("/restore")
    public ApiResponse restorePassword(@RequestBody String email) {
        try {
            userService.restorePassword(email.trim());
            return ApiResponse.SUCCESS;
        } catch (UserNotFoundException e) {
            return new ApiResponse(1, e);
        }
    }

    @PostMapping("/restore/submit")
    public ApiResponse submitRestorationCode(@RequestBody SubmitRestorationCodeRequest request) {
        try {
            String email = request.getEmail().trim();
            String restorationCode = request.getRestorationCode().trim();
            String newPassword = request.getNewPassword().trim();

            userService.submitRestorationCode(email, restorationCode, newPassword);

            return ApiResponse.SUCCESS;
        } catch (UserNotFoundException e) {
            return new ApiResponse(1, e);
        } catch (InvalidPasswordException e) {
            return new ApiResponse(2, e);
        } catch (RestorationCodeExpiredException e) {
            return new ApiResponse(3, e);
        } catch (RestorationCodeMismatchException e) {
            return new ApiResponse(4, e);
        }
    }
}
