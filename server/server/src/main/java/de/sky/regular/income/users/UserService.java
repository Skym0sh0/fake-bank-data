package de.sky.regular.income.users;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.auth.AuthenticationToken;
import de.sky.regular.income.api.auth.User;
import de.sky.regular.income.api.auth.UserRegistration;
import de.sky.regular.income.database.DatabaseSupplier;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final DatabaseConnection db;
    private final PasswordEncoder passwordEncoder;

    private final List<MyUser> users = new ArrayList<>();

    @Autowired
    public UserService(DatabaseSupplier supplier, PasswordEncoder passwordEncoder) {
        this(supplier.get(), passwordEncoder);

        register(new UserRegistration("peter", "12345678"), true);
        register(new UserRegistration("hans", "12345678"), true);
        register(new UserRegistration("admin", "12345678"), true);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUser(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public AuthenticationToken checkLogin(String username) {
        return findUser(username)
                .map(u -> new AuthenticationToken(u.getId(), u.getUsername()))
                .orElseThrow();
    }

    public User register(UserRegistration reg) {
        return register(reg, false);
    }

    public User register(UserRegistration reg, boolean dontEncrypt) {
        if (userExists(reg.getUsername()))
            throw new RuntimeException("User already exists: " + reg.getUsername());

        users.add(
                MyUser.builder()
                        .id(UUID.randomUUID())
                        .username(reg.getUsername())
                        .password(dontEncrypt ? "{noop}" + reg.getPassword() : passwordEncoder.encode(reg.getPassword()))
                        .build()
        );

        return findUser(reg.getUsername())
                .map(u -> new User(u.getId(), u.getUsername()))
                .orElseThrow();
    }

    public void createUser(UserDetails user) {
        register(new UserRegistration(user.getUsername(), user.getUsername()));
    }

    public void updateUser(UserDetails user) {
    }

    public void deleteUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
    }

    public void changePassword(String oldPassword, String newPassword) {
        var username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        findUser(username)
                .filter(u -> u.getPassword().equals(oldPassword))
                .orElseThrow(() -> new RuntimeException("Old password does not match"))
                .setPassword(newPassword);
    }

    public boolean userExists(String username) {
        return findUser(username).isPresent();
    }

    private Optional<MyUser> findUser(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findAny();
    }

    @Builder
    @Data
    private static class MyUser implements UserDetails {
        final UUID id;
        final String username;
        String password;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String toString() {
            return getUsername();
        }
    }
}
