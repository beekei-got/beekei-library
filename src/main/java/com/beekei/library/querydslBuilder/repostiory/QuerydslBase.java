package com.beekei.library.querydslBuilder.repostiory;


import com.beekei.library.querydslBuilder.dto.QuerydslSelectDTO;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@Getter
public class QuerydslBase<T> {

    private final QuerydslBuilder querydslBuilder;
    private final JPAQuery<?> jpaQuery;
    private Expression<T> select;
    private Expression<Long> countSelect;

    public QuerydslBase(QuerydslBuilder querydslBuilder, JPAQueryFactory queryFactory) {
        this.querydslBuilder = querydslBuilder;
        this.jpaQuery = queryFactory.query();
    }

    @SneakyThrows
    public <D extends QuerydslSelectDTO> QuerydslBase<T> select(Class<D> selectClass) {
        this.select = (Expression<T>) selectClass.getMethod("constructor").invoke(selectClass.getDeclaredConstructor().newInstance());
        return this;
    }

    public QuerydslBase<T> select(Expression<T> select) {
        this.select = select;
        return this;
    }

    public QuerydslBase<T> countSelect(Expression<Long> countSelect) {
        this.countSelect = countSelect;
        return this;
    }

    public QuerydslBase<T> selectFrom(EntityPath<T> selectFrom) {
        this.select = selectFrom;
        this.jpaQuery.from(selectFrom);
        return this;
    }

    public QuerydslBase<T> from(EntityPath<?> from) {
        this.jpaQuery.from(from);
        return this;
    }

    public QuerydslBase<T> innerJoin(EntityPath<?> joinTable, Predicate on) {
        this.jpaQuery.innerJoin(joinTable).on(on);
        return this;
    }

    public <D> QuerydslBase<T> innerJoin(EntityPath<D> joinTable, Path<D> path) {
        this.jpaQuery.innerJoin(joinTable, path);
        return this;
    }

    public QuerydslBase<T> leftJoin(EntityPath<?> joinTable, Predicate on) {
        this.jpaQuery.leftJoin(joinTable).on(on);
        return this;
    }

    public QuerydslBase<T> where(Predicate predicate) {
        this.jpaQuery.where(predicate);
        return this;
    }

    public <C> QuerydslBase<T> where(C value, SimpleExpression<C> column) {
        this.jpaQuery.where(column.eq(value));
        return this;
    }

    public <C> QuerydslBase<T> optionalWhere(C value, SimpleExpression<C> column) {
        Optional.ofNullable(value).ifPresent(v -> this.where(v, column));
        return this;
    }

    public QuerydslBase<T> whereOr(String value, StringPath... columns) {
        this.jpaQuery.where(Expressions.anyOf(Arrays.stream(columns)
            .map(column -> column.eq(value))
            .toArray(BooleanExpression[]::new)));
        return this;
    }

    public QuerydslBase<T> optionalWhereOr(String value, StringPath ... columns) {
        Optional.ofNullable(value).ifPresent(v -> this.whereOr(v, columns));
        return this;
    }

    public <C> QuerydslBase<T> whereIn(Collection<C> value, SimpleExpression<C> column) {
        this.jpaQuery.where(column.in(value));
        return this;
    }

    public <C> QuerydslBase<T> optionalWhereIn(Collection<C> value, SimpleExpression<C> column) {
        Optional.ofNullable(value).ifPresent(v -> this.whereIn(v, column));
        return this;
    }

    public <C> QuerydslBase<T> whereNotIn(Collection<C> value, SimpleExpression<C> column) {
        this.jpaQuery.where(column.notIn(value));
        return this;
    }

    public <C> QuerydslBase<T> optionalWhereNotIn(Collection<C> value, SimpleExpression<C> column) {
        Optional.ofNullable(value).ifPresent(v -> this.whereNotIn(v, column));
        return this;
    }

    public QuerydslBase<T> like(String value, StringPath column) {
        this.jpaQuery.where(column.contains(value));
        return this;
    }

    public QuerydslBase<T> optionalLike(String value, StringPath column) {
        Optional.ofNullable(value).ifPresent(v -> this.like(v, column));
        return this;
    }

    public QuerydslBase<T> likeOr(String value, StringPath ... columns) {
        this.jpaQuery.where(Expressions.anyOf(Arrays.stream(columns)
            .map(column -> column.contains(value))
            .toArray(BooleanExpression[]::new)));
        return this;
    }

    public QuerydslBase<T> optionalLikeOr(String value, StringPath ... columns) {
        Optional.ofNullable(value).ifPresent(v -> this.likeOr(v, columns));
        return this;
    }

    public <D extends Comparable> QuerydslBase<T> lessThan(D value, ComparableExpression<D> column) {
        this.jpaQuery.where(column.lt(value));
        return this;
    }

    public <D extends Comparable> QuerydslBase<T> optionalLessThan(D value, ComparableExpression<D> column) {
        Optional.ofNullable(value).ifPresent(v -> this.lessThan(v, column));
        return this;
    }

    public <D extends Comparable> QuerydslBase<T> lessThanEqual(D value, ComparableExpression<D> column) {
        this.jpaQuery.where(column.loe(value));
        return this;
    }

    public <D extends Comparable> QuerydslBase<T> optionalLessThanEqual(D value, ComparableExpression<D> column) {
        Optional.ofNullable(value).ifPresent(v -> this.lessThanEqual(v, column));
        return this;
    }

    public <D extends Comparable> QuerydslBase<T> moreThan(D value, ComparableExpression<D> column) {
        this.jpaQuery.where(column.gt(value));
        return this;
    }

    public <D extends Comparable> QuerydslBase<T> optionalMoreThan(D value, ComparableExpression<D> column) {
        Optional.ofNullable(value).ifPresent(v -> this.moreThan(v, column));
        return this;
    }

    public <D extends Comparable> QuerydslBase<T> moreThanEqual(D value, ComparableExpression<D> column) {
        this.jpaQuery.where(column.goe(value));
        return this;
    }

    public <D extends Comparable> QuerydslBase<T> optionalMoreThanEqual(D value, ComparableExpression<D> column) {
        Optional.ofNullable(value).ifPresent(v -> this.moreThanEqual(v, column));
        return this;
    }

    public QuerydslBase<T> orderBy(OrderSpecifier<?>... specifiers) {
        this.jpaQuery.orderBy(specifiers);
        return this;
    }

    public QuerydslBase<T> groupBy(Expression<?>... groupBy) {
        this.jpaQuery.groupBy(groupBy);
        return this;
    }

    public <D> Optional<D> transformRow(ResultTransformer<Map<Long, D>> transformer) {
        return Optional.ofNullable(this.jpaQuery.transform(transformer))
            .flatMap(map -> map.values().stream().findFirst());
    }

    public <D> List<D> transformList(ResultTransformer<List<D>> transformer) {
        return this.jpaQuery.transform(transformer);
    }

    public Optional<T> getRow() {
        return this.querydslBuilder.getRow(this);
    }

    public List<T> getList() {
        return this.querydslBuilder.getList(this);
    }

    public Page<T> getPage(PageRequest pageRequest) {
        return this.querydslBuilder.getPage(this, pageRequest);
    }

}
