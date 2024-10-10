package com.beekei.library.querydslBuilder.repostiory;

import com.querydsl.jpa.JPQLTemplates;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuerydslBuilderTemplates {

	DEFAULT(JPQLTemplates.DEFAULT);

	private final JPQLTemplates jpqlTemplates;

}
