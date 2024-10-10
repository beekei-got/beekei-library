package com.beekei.library.querydslBuilder.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public interface QuerydslBuilderConfig {

	@Bean
	JPAQueryFactory jpaQueryBuilderFactory();

}
