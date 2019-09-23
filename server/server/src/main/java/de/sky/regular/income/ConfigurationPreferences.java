package de.sky.regular.income;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(value = "config")
@Validated
public class ConfigurationPreferences {
    @NotNull
    @Valid
    private final JdbcSettings jdbc = new JdbcSettings();

    public JdbcSettings getJdbc() {
        return jdbc;
    }

    public static class JdbcSettings {
        @NotBlank
        private String url;
        @NotNull
        private String user;
        @NotNull
        private String password;
        @NotNull
        private String schema;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }
    }
}
