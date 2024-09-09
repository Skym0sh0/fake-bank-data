package de.sky.regular.income.users;

import de.sky.regular.income.database.DatabaseConnection;
import de.sky.regular.income.api.User;
import de.sky.regular.income.api.UserRegistration;
import de.sky.regular.income.database.DatabaseSupplier;
import generated.sky.regular.income.tables.records.UsersRecord;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

import static generated.sky.regular.income.Tables.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService, UserProvider {

    private final DatabaseConnection db;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(DatabaseSupplier supplier, PasswordEncoder passwordEncoder) {
        this(supplier.get(), passwordEncoder);
    }

    @Override
    public User getCurrentUser() {
        return db.transactionWithResult(ctx -> {
            var userCtx = SecurityContextHolder.getContext();
            return this.checkLogin(userCtx.getAuthentication().getPrincipal().toString());
        });
    }

    @Override
    public User getCurrentUser(DSLContext ctx) {
        var userCtx = SecurityContextHolder.getContext();
        return this.checkLogin(ctx, userCtx.getAuthentication().getPrincipal().toString());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return db.transactionWithResult(ctx ->
                findUser(ctx, username)
                        .map(UserService::mapToUserDetails)
                        .orElseThrow(() -> new UsernameNotFoundException(username))
        );
    }

    public User checkLogin(String username) {
        return db.transactionWithResult(ctx -> checkLogin(ctx, username));
    }

    public User register(UserRegistration reg) {
        var ts = ZonedDateTime.now().toOffsetDateTime();

        return db.transactionWithResult(ctx -> {
            var u = ctx.newRecord(USERS);

            u.setId(UUID.randomUUID());

            u.setUsername(reg.getUsername().toLowerCase());
            u.setPassword(passwordEncoder.encode(reg.getPassword()));

            u.setFirstname(reg.getFirstname());
            u.setLastname(reg.getLastname());

            u.setRoles("ROLE_USER");

            u.setCreatedAt(ts);
            u.setUpdatedAt(ts);

            u.insert();

            return fetchUser(ctx, u.getId());
        });
    }

    public User updateUser(UUID id, UserRegistration reg) {
        var ts = ZonedDateTime.now().toOffsetDateTime();

        return db.transactionWithResult(ctx -> {
            if (!Objects.equals(id, getCurrentUser(ctx).getId()))
                throw new IllegalStateException("Current user can only change itself");

            var rec = ctx.selectFrom(USERS)
                    .where(USERS.ID.eq(id))
                    .forUpdate()
                    .fetchOptional()
                    .orElseThrow();

            rec.setUsername(reg.getUsername().toLowerCase());
            rec.setPassword(passwordEncoder.encode(reg.getPassword()));

            rec.setFirstname(reg.getFirstname());
            rec.setLastname(reg.getLastname());

            rec.setUpdatedAt(ts);

            rec.update();

            return fetchUser(ctx, id);
        });
    }

    public void deleteUser(UUID id) {
        db.transactionWithoutResult(ctx -> {
            if (!Objects.equals(id, getCurrentUser(ctx).getId()))
                throw new IllegalStateException("Current user can only change itself");

            ctx.deleteFrom(FINANCIAL_TRANSACTION)
                    .where(FINANCIAL_TRANSACTION.OWNER_ID.eq(id))
                    .execute();
            ctx.deleteFrom(BANK_STATEMENT)
                    .where(BANK_STATEMENT.OWNER_ID.eq(id))
                    .execute();
            ctx.deleteFrom(TURNOVER_ROW)
                    .where(TURNOVER_ROW.OWNER_ID.eq(id))
                    .execute();
            ctx.deleteFrom(TURNOVER_FILE_IMPORT)
                    .where(TURNOVER_FILE_IMPORT.OWNER_ID.eq(id))
                    .execute();
            ctx.deleteFrom(CATEGORY)
                    .where(CATEGORY.OWNER_ID.eq(id))
                    .execute();

            ctx.deleteFrom(USERS)
                    .where(USERS.ID.eq(id))
                    .execute();
        });
    }

    private User checkLogin(DSLContext ctx, String username) {
        return findUser(ctx, username)
                .map(UserService::mapToUser)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private User fetchUser(DSLContext ctx, UUID id) {
        return ctx.fetchOptional(USERS, USERS.ID.eq(id))
                .map(UserService::mapToUser)
                .orElseThrow(() -> new UsernameNotFoundException("ID=" + id));
    }

    private Optional<UsersRecord> findUser(DSLContext ctx, String username) {
        return ctx.fetchOptional(USERS, USERS.USERNAME.equalIgnoreCase(username));
    }

    private static User mapToUser(UsersRecord rec) {
        return User.builder()
                .id(rec.getId())
                .username(rec.getUsername())
                .firstname(rec.getFirstname())
                .lastname(rec.getLastname())
                .build();
    }

    private static UserDetails mapToUserDetails(UsersRecord rec) {
        return DBPersistedUser.builder()
                .id(rec.getId())
                .username(rec.getUsername())
                .password(rec.getPassword())
                .roles(Arrays.asList(StringUtils.split(rec.getRoles(), ",")))
                .build();
    }

    @Builder
    private record DBPersistedUser(UUID id, String username, String password,
                                   @Singular List<String> roles)
            implements UserDetails {

        private DBPersistedUser {
            roles = new ArrayList<>(roles);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        @Override
        public String getUsername() {
            return username();
        }

        @Override
        public String getPassword() {
            return password();
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
            return username();
        }
    }
}
