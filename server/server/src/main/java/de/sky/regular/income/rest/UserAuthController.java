package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.auth.AuthenticationToken;
import de.sky.regular.income.api.auth.User;
import de.sky.regular.income.api.auth.UserLogin;
import de.sky.regular.income.api.auth.UserRegistration;
import de.sky.regular.income.database.DatabaseSupplier;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/user-auth")
@Slf4j
public class UserAuthController {
    private final DatabaseConnection db;

    public UserAuthController(DatabaseConnection db) {
        this.db = Objects.requireNonNull(db);
    }

    @Autowired
    public UserAuthController(DatabaseSupplier supplier) {
        this(supplier.getDatabase());
    }

    private static final List<MyUser> DEFAULT_USERS = List.of(
            MyUser.builder().id(UUID.randomUUID()).username("peter").password("12345678").build(),
            MyUser.builder().id(UUID.randomUUID()).username("hansi").password("12345678").build(),
            MyUser.builder().id(UUID.randomUUID()).username("admin").password("12345678").build()
    );

    private final List<MyUser> users = new ArrayList<>(DEFAULT_USERS);

    @SneakyThrows
    @PostMapping("register")
    public User registerUser(@RequestBody UserRegistration registration) {
        Thread.sleep(2500);
        log.info("Registering user {}...", registration);

        if (users.stream().anyMatch(u -> u.getUsername().equals(registration.getUsername())))
            throw new RuntimeException("username already in use");

        var usr = MyUser.builder()
                .id(UUID.randomUUID())
                .username(registration.getUsername())
                .password(registration.getPassword())
                .build();

        users.add(usr);

        return new User(usr.getId(), usr.getUsername());
    }

    @SneakyThrows
    @PostMapping("login")
    public AuthenticationToken login(@RequestBody UserLogin login) {
        Thread.sleep(2500);
        log.info("Logging user in {}...", login);

        return users.stream()
                .filter(u -> u.getUsername().equals(login.getUsername()))
                .filter(u -> u.getPassword().equals(login.getPassword()))
                .findAny()
                .map(usr -> new AuthenticationToken(usr.getId(), usr.getUsername()))
                .orElseThrow(() -> new RuntimeException("Username/Password does not exist"));
    }

    @SneakyThrows
    @DeleteMapping("logout")
    public void logout() {
        Thread.sleep(2500);
        log.info("Logging user out ...");
    }

    @Builder
    @Value
    private static class MyUser {
        UUID id;
        String username;
        String password;
    }
}
