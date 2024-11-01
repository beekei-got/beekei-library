package com.beekei.library.mockTest;

import com.beekei.library.apiVersion.ApiVersion;
import com.beekei.library.apiVersion.ApiVersionRequestMappingHandlerMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@MockMvcConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MockMvcControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	private ApplicationContext applicationContext;

	private final ObjectMapper objectMapper;

	private final Class<?> controllerClass;
	private String requestMappingPath = "";

	protected MockMvcControllerTest(Class<?> controllerClass) {
		this.controllerClass = controllerClass;
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
		this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	@SneakyThrows
	@PostConstruct
	protected void init() {
		String requestMappingPath = "";

		if (this.controllerClass.isAnnotationPresent(ApiVersion.class)) {
			try {
				ApiVersion versionAnnotation = AnnotationUtils.findAnnotation(this.controllerClass, ApiVersion.class);
				WebMvcRegistrations webMvcRegistrations = applicationContext.getBean(WebMvcRegistrations.class);
				ApiVersionRequestMappingHandlerMapping apiVersionRequestMappingHandlerMapping =
					(ApiVersionRequestMappingHandlerMapping) webMvcRegistrations.getRequestMappingHandlerMapping();
				requestMappingPath += "/" + apiVersionRequestMappingHandlerMapping.getPathPrefix() + versionAnnotation.value();
			} catch (NoSuchBeanDefinitionException ignored) {
			}
		}

		if (this.controllerClass.isAnnotationPresent(RequestMapping.class)) {
			Annotation mappingAnnotation = this.controllerClass.getAnnotation(RequestMapping.class);
			String[] path = (String[]) RequestMapping.class.getMethod("path").invoke(mappingAnnotation);
			if (path.length > 0) {
				requestMappingPath += "/" + path[0];
			} else {
				String[] value = (String[]) RequestMapping.class.getMethod("value").invoke(mappingAnnotation);
				if (value.length > 0) {
					requestMappingPath += "/" + value[0];
				}
			}
		}

		this.requestMappingPath = requestMappingPath;
	}

	protected MockMvcBase mvcTest(HttpMethod httpMethod) {
		return new MockMvcBase(this, "", httpMethod);
	}

	protected MockMvcBase mvcTest(HttpMethod httpMethod, String apiPath) {
		return new MockMvcBase(this, apiPath, httpMethod);
	}

	@SneakyThrows
	protected void run(MockMvcBase mockMvcBase) {
		HttpMethod httpMethod = mockMvcBase.getHttpMethod();
		String apiPath = mockMvcBase.getApiPath();
		String pullPath = this.requestMappingPath + (StringUtils.isNotBlank(apiPath) ? "/" + apiPath : "");

		Map<String, Object> pathVariable = mockMvcBase.getPathVariable();
		if (pathVariable != null && !pathVariable.isEmpty()) {
			for (String key : pathVariable.keySet()) {
				pullPath = pullPath.replace("{" + key + "}", String.valueOf(pathVariable.get(key)));
			}
		}

		MockHttpServletRequestBuilder servletRequest = MockMvcRequestBuilders.request(httpMethod, pullPath)
			.contentType(MediaType.APPLICATION_JSON);

		MultiValueMap<String, String> requestParams = mockMvcBase.getRequestParams();
		if (requestParams != null && !requestParams.isEmpty())
			servletRequest.params(requestParams);

		Map<String, Object> requestBody = mockMvcBase.getRequestBody();
		if (requestBody != null && !requestBody.isEmpty())
			servletRequest.content(objectMapper.writeValueAsString(requestBody));

		ResultActions resultActions = mockMvc.perform(servletRequest)
			.andExpect(MockMvcResultMatchers.status().is(mockMvcBase.getHttpStatus().value()));

		Map<String, Object> responseBody = convertResponseBodyTemplate(mockMvcBase.getResponseBody());

		if (responseBody != null && !responseBody.isEmpty()) {
			resultActions.andExpectAll(responseBody.keySet().stream()
				.map(key -> MockMvcResultMatchers.jsonPath("$." + key).value(responseBody.get(key)))
				.toArray(ResultMatcher[]::new));
		}

		resultActions.andDo(MockMvcResultHandlers.print());
	}

	protected Map<String, Object> getResponseBodyTemplate() {
		return new HashMap<>();
	}

	private Map<String, Object> convertResponseBodyTemplate(Map<String, Object> responseBody) {
		Map<String, Object> resultResponseBody = new HashMap<>();
		Map<String, Object> responseBodyTemplate = getResponseBodyTemplate();
		if (responseBodyTemplate.isEmpty()) return responseBody;

		Set.copyOf(responseBodyTemplate.keySet()).forEach(key -> {
			Object value = responseBodyTemplate.get(key);
			if (value.equals("{responseBody}")) {
				responseBody.forEach((key2, value2) -> resultResponseBody.put(key + "." + key2, value2));
			} else {
				resultResponseBody.put(key, value);
			}
		});
		return resultResponseBody;
	}

}
