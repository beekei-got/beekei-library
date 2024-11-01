package com.beekei.library.mockTest;

import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MockMvcBase {

	private final MockMvcControllerTest mockMvcControllerTest;
	private final String apiPath;
	private final HttpMethod httpMethod;
	private final Map<String, Object> pathVariable = new HashMap<>();
	private final MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
	private HttpStatus httpStatus = HttpStatus.OK;
	private final Map<String, Object> requestBody = new HashMap<>();
	private final Map<String, Object> responseBody = new HashMap<>();

	public MockMvcBase(MockMvcControllerTest mockMvcControllerTest, String apiPath, HttpMethod httpMethod) {
		this.mockMvcControllerTest = mockMvcControllerTest;
		this.apiPath = apiPath;
		this.httpMethod = httpMethod;
	}

	public MockMvcBase pathVariable(Map.Entry<String, Object> ... values) {
		for (Map.Entry<String, Object> value : values) {
			this.pathVariable.put(value.getKey(), String.valueOf(value.getValue()));
		}
		return this;
	}

	public MockMvcBase requestParam(Map.Entry<String, Object> ... params) {
		for (Map.Entry<String, Object> param : params) {
			this.requestParams.add(param.getKey(), String.valueOf(param.getValue()));
		}
		return this;
	}

	public MockMvcBase requestBody(Map.Entry<String, Object> ... bodys) {
		for (Map.Entry<String, Object> body : bodys) {
			this.requestBody.put(body.getKey(), body.getValue());
		}
		return this;
	}

	public MockMvcBase requestBody(Map<String, Object> body) {
		this.requestBody.putAll(body);
		return this;
	}

	public MockMvcBase responseHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
		return this;
	}

	public MockMvcBase responseBody(Map.Entry<String, Object> ... bodys) {
		for (Map.Entry<String, Object> body : bodys) {
			this.responseBody.put(body.getKey(), body.getValue());
		}
		return this;
	}

	public MockMvcBase responseBody(Map<String, Object> ... bodys) {
		for (Map<String, Object> body : bodys) {
			this.responseBody.putAll(body);
		}
		return this;
	}

	public void run() {
		this.mockMvcControllerTest.run(this);
	}

}
