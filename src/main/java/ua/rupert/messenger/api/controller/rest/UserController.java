package ua.rupert.messenger.api.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.rupert.messenger.api.service.AuthenticationUserDetailsService;
import ua.rupert.messenger.store.dto.RegisterUserDto;
import ua.rupert.messenger.store.dto.UserDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final AuthenticationUserDetailsService userService;

    @PostMapping(ApiPath.REGISTER_USER)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody RegisterUserDto registerUserDto) {
        return userService.createUser(registerUserDto);
    }

    @GetMapping(ApiPath.GET_AUTHENTICATION_USER)
    private ResponseEntity<UserDto> getAuthenticationSessionUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserDetails(authentication.getName()));
    }

    private static class ApiPath{
        private static final String REGISTER_USER = "/registration";
        private static final String LOGIN_USER = "/loginUser";
        private static final String GET_AUTHENTICATION_USER = "/authenticationUser";
        private static final String GET_CHAT_HISTORY = "/chatHistory";
        private static final String GET_NEW_USER = "/user";
    }
}
