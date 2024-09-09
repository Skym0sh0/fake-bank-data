package de.sky.regular.income.users;

import de.sky.regular.income.api.User;
import org.jooq.DSLContext;

public interface UserProvider {

	User getCurrentUser();

	User getCurrentUser(DSLContext ctx);
}
