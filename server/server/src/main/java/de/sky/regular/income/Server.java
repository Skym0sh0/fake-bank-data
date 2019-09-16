package de.sky.regular.income;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class Server {
    @Bean
    @Primary
    public ObjectMapper serializingObjectMapper() {
        return new ObjectMapper() {
            {
                registerModule(new JavaTimeModule());
                registerModule(new Jdk8Module());

                enable(SerializationFeature.INDENT_OUTPUT);

                disable(MapperFeature.USE_GETTERS_AS_SETTERS);
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                configure(JsonParser.Feature.ALLOW_COMMENTS, true);
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }
        };
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        return loggingFilter;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Server.class, args);
    }
}
