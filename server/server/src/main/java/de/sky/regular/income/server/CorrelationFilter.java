package de.sky.regular.income.server;

import com.google.common.base.Stopwatch;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String correlationId = Optional.ofNullable(request.getHeader("correlationid"))
                .orElseGet(() -> "generated-" + UUID.randomUUID());

        log.info(" >>> {} {} - cor-id: {}", request.getMethod(), request.getServletPath(), correlationId);
        Stopwatch sw = Stopwatch.createStarted();

        filterChain.doFilter(request, response);

        sw.stop();
        log.info(" <<< {} {} {} {} - cor-id: {}", request.getMethod(), request.getServletPath(), response.getStatus(), sw, correlationId);
    }
}
