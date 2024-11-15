package com.boardgo.config;

import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLTemplates;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SQLTemplatesConfig {

    @Bean
    public SQLTemplates sqlTemplates() {
        return new MySQLTemplates();
    }
}
