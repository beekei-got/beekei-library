package com.beekei.library.querydslBuilder.repostiory;

import com.beekei.library.querydslBuilder.dto.QuerydslSelectDTO;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class QuerydslBuilder {

    private final JPAQueryFactory jpaQueryFactory;

	public QuerydslBuilder(JPQLTemplates jpqlTemplates, EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(jpqlTemplates, entityManager);
	}

	public <T extends QuerydslSelectDTO> QuerydslBase<T> select(Class<T> selectClass) {
        QuerydslBase<T> querydslBase = new QuerydslBase<>(this, jpaQueryFactory);
        return querydslBase.select(selectClass);
    }

    public <T extends QuerydslSelectDTO> QuerydslBase<T> select(Class<T> selectClass, Expression<Long> countSelect) {
        QuerydslBase<T> querydslBase = new QuerydslBase<>(this, jpaQueryFactory);
        return querydslBase.select(selectClass).countSelect(countSelect);
    }

    public <T> QuerydslBase<T> select(Expression<T> select) {
        QuerydslBase<T> querydslBase = new QuerydslBase<>(this, jpaQueryFactory);
        return querydslBase.select(select);
    }

    public <T> QuerydslBase<T> selectFrom(EntityPath<T> selectFrom) {
        QuerydslBase<T> querydslBase = new QuerydslBase<>(this, jpaQueryFactory);
        return querydslBase.selectFrom(selectFrom);
    }

    public <T> QuerydslBase<T> from(EntityPath<T> from) {
        QuerydslBase<T> querydslBase = new QuerydslBase<>(this, jpaQueryFactory);
        return querydslBase.from(from);
    }


    public <T> Optional<T> getRow(QuerydslBase<T> querydslBase) {
        return Optional.ofNullable(querydslBase.getJpaQuery().select(querydslBase.getSelect()).fetchOne());
    }

    public <T> List<T> getList(QuerydslBase<T> querydslBase) {
        return querydslBase.getJpaQuery().select(querydslBase.getSelect()).fetch();
    }

    public <T> Page<T> getPage(QuerydslBase<T> querydslBase, PageRequest pageRequest) {
        long count = Optional.ofNullable(querydslBase.getJpaQuery()
            .select(querydslBase.getCountSelect())
            .fetchOne()).orElse(0L);
        List<T> list = count > 0 ? querydslBase.getJpaQuery()
            .select(querydslBase.getSelect())
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .fetch() : new ArrayList<>();
        return new PageImpl<>(list, pageRequest, count);
    }

}
