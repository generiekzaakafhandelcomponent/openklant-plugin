package com.ritense.valtimoplugins.openklant.security

import com.ritense.valtimo.contract.authentication.AuthoritiesConstants.ADMIN
import com.ritense.valtimo.contract.security.config.HttpConfigurerConfigurationException
import com.ritense.valtimo.contract.security.config.HttpSecurityConfigurer
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.stereotype.Component

@Order(1)
@Component
class KotlinHttpSecurityConfigurer : HttpSecurityConfigurer {
    override fun configure(http: HttpSecurity) {
        try {
            http.authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(HttpMethod.GET, "/api/open-klant/v1/**")
                    .hasAnyAuthority(ADMIN)
                    .requestMatchers(HttpMethod.POST, "/api/open-klant/v1/**")
                    .hasAnyAuthority(ADMIN)
                    .requestMatchers(HttpMethod.PUT, "/api/open-klant/v1/**")
                    .hasAnyAuthority(ADMIN)
            }
        } catch (e: Exception) {
            throw HttpConfigurerConfigurationException(e)
        }
    }

}
