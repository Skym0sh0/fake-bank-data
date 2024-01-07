package de.sky.regular.income.rest;

import de.sky.regular.income.api.auth.AuthenticationToken;
import de.sky.regular.income.api.auth.User;
import de.sky.regular.income.api.auth.UserLogin;
import de.sky.regular.income.api.auth.UserRegistration;
import de.sky.regular.income.users.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class UserAuthController {
    private final UserService userService;

    @SneakyThrows
    @PostMapping("/user/register")
    public User registerUser(@RequestBody UserRegistration registration) {
        Thread.sleep(2500);
        log.info("Registering user {}...", registration);

        return userService.register(registration);
    }

    @GetMapping("/auth/login")
    public AuthenticationToken login() {
        var ctx = SecurityContextHolder.getContext();
        var auth = ctx.getAuthentication();

        return userService.checkLogin(auth.getPrincipal().toString());
    }

    @PostMapping("/auth/login")
    public AuthenticationToken login(@RequestBody UserLogin login) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @DeleteMapping("/auth/logout")
    public void logout() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
