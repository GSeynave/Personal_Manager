package gse.home.personalmanager.security.config;

import org.apache.catalina.filters.RateLimitFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitConfigTest {

    private final RateLimitConfig rateLimitConfig = new RateLimitConfig();

    @Test
    void shouldCreateRateLimitFilter() {
        // When
        FilterRegistrationBean<RateLimitFilter> registrationBean = rateLimitConfig.rateLimitFilter();

        // Then
        assertThat(registrationBean).isNotNull();
        assertThat(registrationBean.getFilter()).isInstanceOf(RateLimitFilter.class);
        assertThat(registrationBean.getUrlPatterns()).contains("/v1/*");
        assertThat(registrationBean.getInitParameters())
                .containsEntry("bucketRequests", "200")
                .containsEntry("bucketDuration", "60")
                .containsEntry("enforce", "true")
                .containsEntry("statusCode", "429");
    }
}
