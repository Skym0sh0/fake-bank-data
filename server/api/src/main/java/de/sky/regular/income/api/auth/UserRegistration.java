package de.sky.regular.income.api.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistration {
    public String firstname;
    public String lastname;

    public String username;
    public String password;
}
