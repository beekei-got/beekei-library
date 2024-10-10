package com.beekei.library.querydslBuilder.config;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class DefaultQuerydslBuilderConfig implements QuerydslBuilderConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public JPAQueryFactory jpaQueryBuilderFactory() {
        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

}
