package com.beekei.library.mockTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@AutoConfigureMockMvc
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ MockMvcConfig.Customizer.class })
public @interface MockMvcConfig {

	class Customizer {
		@Bean
		MockMvcBuilderCustomizer utf8Config() {
			return builder ->
				builder.addFilters(new CharacterEncodingFilter("UTF-8", true));
		}
	}

}
