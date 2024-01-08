package de.sky.regular.income.api.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    public UUID id;
    public String username;

    public String firstname;
    public String lastname;
}
