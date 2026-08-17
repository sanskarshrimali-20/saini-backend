package com.saini.app.saini.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(private val jwtInterceptor: JwtInterceptor) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:uploads/")
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns(
                "/api/saini/biodata/**", 
                "/api/saini/shortlist/**", 
                "/api/saini/subscription/**", 
                "/api/saini/auth/update-profile", 
                "/api/saini/auth/change-password", 
                "/api/saini/auth/privacy-settings",
                "/api/saini/auth/me"
            )
            .excludePathPatterns("/api/saini/auth/login", "/api/saini/auth/signup", "/api/saini/auth/forgot-password", "/api/saini/auth/reset-password")
    }
}
