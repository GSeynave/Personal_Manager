package gse.home.personalmanager.security.config;

import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter() {
        FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();

        // Use the Tomcat RateLimitFilter which uses the interface you showed
        registrationBean.setFilter(new RateLimitFilter());

        // Apply to specific URL patterns (e.g., all API endpoints)
        registrationBean.addUrlPatterns("/v1/*");

        // CONFIGURATION:
        // 1. bucketRequests: The number of requests allowed (matches setRequests)
        registrationBean.addInitParameter("bucketRequests", "200");

        // 2. bucketDuration: The window in seconds (matches setDuration)
        registrationBean.addInitParameter("bucketDuration", "60");

        // 3. enforce: If false, it just logs; if true, it blocks with 429 (default true)
        registrationBean.addInitParameter("enforce", "true");

        // 4. statusCode: The HTTP status code to return when limited (default 429)
        registrationBean.addInitParameter("statusCode", "429");

        return registrationBean;
    }
}
