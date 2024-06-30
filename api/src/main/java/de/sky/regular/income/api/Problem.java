package de.sky.regular.income.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class Problem {

    public ZonedDateTime timestamp;
    public String path;
    public int status;
    public String error;
    public String errorDetails;
}
