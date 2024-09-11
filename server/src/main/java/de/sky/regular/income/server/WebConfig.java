package de.sky.regular.income.server;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new IndexFallbackResourceResolver());
    }

    @Slf4j
    private static class IndexFallbackResourceResolver extends PathResourceResolver {
        @Override
        protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
            // Give PathResourceResolver a chance to resolve a resource on its own.
            Resource resource = super.resolveResourceInternal(request, requestPath, locations, chain);

            log.debug(">>> Resolve resource {} + {} => {}", request.getServletPath(), requestPath, resource);

            if (resource == null) {
                // If resource wasn't found, use index.html file.
                resource = super.resolveResourceInternal(request, "index.html", locations, chain);
            }
            return resource;
        }
    }
}