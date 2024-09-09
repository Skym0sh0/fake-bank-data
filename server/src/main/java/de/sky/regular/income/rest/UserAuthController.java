package de.sky.regular.income.rest;

import de.sky.regular.income.api.User;
import de.sky.regular.income.api.UserLogin;
import de.sky.regular.income.api.UserRegistration;
import de.sky.regular.income.users.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor

public class UserAuthController implements generated.sky.regular.income.api.rest.UserAuthApi {
    private final UserService userService;

    @Override
    public ResponseEntity<User> registerUser(UserRegistration registration) {
        log.info("Registering user {}...", registration);

        return ResponseEntity.ok(userService.register(registration));
    }

    @Override
    public ResponseEntity<User> updateUser(UUID id, UserRegistration registration) {
        log.info("Changing user {}...", registration);

        return ResponseEntity.ok(userService.updateUser(id, registration));
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID id) {
        log.info("Deleting user {}...", id);

        userService.deleteUser(id);

        return ResponseEntity.ok()
                .build();
    }

    @Override
    public ResponseEntity<User> loginUserInfo() {
        var ctx = SecurityContextHolder.getContext();
        var auth = ctx.getAuthentication();

        return ResponseEntity.ok(userService.checkLogin(auth.getPrincipal().toString()));
    }

    @Override
    public ResponseEntity<User> loginUser(UserLogin userLogin) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public ResponseEntity<Void> logoutUser() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
