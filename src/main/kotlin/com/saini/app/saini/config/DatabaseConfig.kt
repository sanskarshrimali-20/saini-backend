package com.saini.app.saini.config

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.net.URI
import javax.sql.DataSource

@Configuration
class DatabaseConfig {

    @Bean
    @Primary
    fun dataSource(): DataSource {
        val databaseUrl = System.getenv("SPRING_DATASOURCE_URL") 
            ?: System.getenv("DATABASE_URL")
            ?: return DataSourceBuilder.create()
                .url("jdbc:mysql://localhost:3306/saini_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC")
                .username("saini_user")
                .password("saini_pass")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build()

        return if (databaseUrl.startsWith("mysql://")) {
            val uri = URI(databaseUrl)
            val userInfo = uri.userInfo ?: ":"
            val parts = userInfo.split(":")
            val username = parts.getOrElse(0) { "" }
            val password = parts.getOrElse(1) { "" }
            val jdbcUrl = "jdbc:mysql://${uri.host}:${if (uri.port != -1) uri.port else 3306}${uri.path}?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"
            
            DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build()
        } else {
            DataSourceBuilder.create()
                .url(databaseUrl)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build()
        }
    }
}
