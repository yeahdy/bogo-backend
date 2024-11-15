package com.boardgo.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.SQLTemplates;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class JPASQLQueryFactory {
    private final EntityManager entityManager;
    private final SQLTemplates sqlTemplates;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    public JPASQLQueryFactory(EntityManager entityManager, SQLTemplates sqlTemplates) {
        this.entityManager = entityManager;
        this.sqlTemplates = sqlTemplates;
    }

    public JPASQLQuery<?> createQuery() {
        return new JPASQLQuery<>(entityManager, sqlTemplates);
    }
}
