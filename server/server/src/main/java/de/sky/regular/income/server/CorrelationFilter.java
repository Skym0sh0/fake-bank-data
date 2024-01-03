package de.sky.regular.income.server;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@Order(0)
public class CorrelationFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            super.doFilter(request, response, chain);
            return;
        }

        String correlationid = Optional.ofNullable(request.getHeader("correlationid"))
                .orElse("unknown");

        log.info(" >>> {} {} - cor-id: {}", request.getMethod(), request.getServletPath(), correlationid);
        Stopwatch sw = Stopwatch.createStarted();

        super.doFilter(request, response, chain);

        sw.stop();
        log.info(" <<< {} {} {} {} - cor-id: {}", request.getMethod(), request.getServletPath(), response.getStatus(), sw, correlationid);
    }
}
