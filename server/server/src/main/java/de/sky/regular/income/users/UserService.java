package de.sky.regular.income.users;

import static generated.sky.regular.income.Tables.USERS;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.auth.User;
import de.sky.regular.income.api.auth.UserRegistration;
import de.sky.regular.income.database.DatabaseSupplier;
import generated.sky.regular.income.tables.records.UsersRecord;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

	private User checkLogin(DSLContext ctx, String username) {
		return findUser(ctx, username)
				.map(UserService::mapToUser)
				.orElseThrow(() -> new UsernameNotFoundException(username));
	}

	public User register(UserRegistration reg) {
		var ts = ZonedDateTime.now().toOffsetDateTime();

		return db.transactionWithResult(ctx -> {
			var u = ctx.newRecord(USERS);

			u.setId(UUID.randomUUID());

			u.setUsername(reg.getUsername().toLowerCase());
			u.setPassword(passwordEncoder.encode(reg.getPassword()));

			u.setRoles("ROLE_USER");

			u.setCreatedAt(ts);
			u.setUpdatedAt(ts);

			u.insert();

			return findUser(ctx, reg.getUsername())
					.map(UserService::mapToUser)
					.orElseThrow();
		});
	}

	public void createUser(UserDetails user) {
		register(new UserRegistration(user.getUsername(), user.getPassword()));
	}

	public void updateUser(UserDetails user) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	public void deleteUser(String username) {
		db.transactionWithoutResult(ctx -> {
			ctx.deleteFrom(USERS)
					.where(USERS.USERNAME.equalIgnoreCase(username))
					.execute();
		});
	}

	public void changePassword(String oldPassword, String newPassword) {
		var username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		db.transactionWithoutResult(ctx -> {
			ctx.update(USERS)
					.set(USERS.UPDATED_AT, OffsetDateTime.now())
					.set(USERS.PASSWORD, newPassword)
					.where(USERS.USERNAME.equalIgnoreCase(username))
					.and(USERS.PASSWORD.eq(oldPassword))
					.execute();
		});
	}

	public boolean userExists(String username) {
		return db.transactionWithResult(ctx -> ctx.fetchExists(USERS, USERS.USERNAME.equalIgnoreCase(username)));
	}

	private Optional<UsersRecord> findUser(DSLContext ctx, String username) {
		return ctx.fetchOptional(USERS, USERS.USERNAME.equalIgnoreCase(username));
	}

	private static User mapToUser(UsersRecord rec) {
		return new User(rec.getId(), rec.getUsername());
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
								   @Singular List<String> roles) implements UserDetails {

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
