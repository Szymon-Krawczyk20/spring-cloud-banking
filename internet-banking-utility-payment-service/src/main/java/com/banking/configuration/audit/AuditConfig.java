package com.banking.configuration.audit;

import com.banking.configuration.filter.AppAuthUserFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<String> myAuditorProvider() {
        return new AuditorAwareConfig();
    }

    @Bean
    public FilterRegistrationBean<AppAuthUserFilter> authUserFilter() {
        FilterRegistrationBean<AppAuthUserFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AppAuthUserFilter());
        registrationBean.addUrlPatterns("/api/*");

        return registrationBean;
    }

}
