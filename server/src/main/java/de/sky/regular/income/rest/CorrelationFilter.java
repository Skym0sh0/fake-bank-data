package de.sky.regular.income.rest;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class CorrelationFilter extends HttpFilter {
    private static final Logger logger = getLogger(CorrelationFilter.class);

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String correlationid = Optional.ofNullable(request.getHeader("correlationid"))
                .orElse("unknown");

        logger.info(" >>> Request starts to {} - {}", request.getServletPath(), correlationid);

        Stopwatch sw = Stopwatch.createStarted();

        super.doFilter(request, response, chain);

        sw.stop();
        logger.info(" <<< Request finished in {} to {} {} - {}", sw, request.getServletPath(), response.getStatus(), correlationid);
    }
}
