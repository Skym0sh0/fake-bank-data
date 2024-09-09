package de.sky.regular.income.rest;

import de.sky.regular.income.api.User;
import de.sky.regular.income.api.UserLogin;
import de.sky.regular.income.api.UserRegistration;
import de.sky.regular.income.users.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserAuthController {
    private final UserService userService;

    @PostMapping("/user/register")
    public User registerUser(@RequestBody UserRegistration registration) {
        log.info("Registering user {}...", registration);

        return userService.register(registration);
    }

    @PatchMapping("/user/{id}/details")
    public User changeUser(@PathVariable("id") UUID id, @RequestBody UserRegistration registration) {
        log.info("Changing user {}...", registration);

        return userService.updateUser(id, registration);
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable("id") UUID id) {
        log.info("Deleting user {}...", id);

        userService.deleteUser(id);
    }

    @GetMapping("/auth/login")
    public User login() {
        var ctx = SecurityContextHolder.getContext();
        var auth = ctx.getAuthentication();

        return userService.checkLogin(auth.getPrincipal().toString());
    }

    @PostMapping("/auth/login")
    public User login(@RequestBody UserLogin login) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @DeleteMapping("/auth/logout")
    public void logout() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
