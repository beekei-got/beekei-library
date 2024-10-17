package com.beekei.library.apiVersion;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final String pathPrefix;
    private final String versionPrefix;

    public ApiVersionRequestMappingHandlerMapping(String pathPrefix, String versionPrefix) {
        this.pathPrefix = pathPrefix;
        this.versionPrefix = versionPrefix;
    }

    public ApiVersionRequestMappingHandlerMapping(String versionPrefix) {
        this.pathPrefix = null;
        this.versionPrefix = versionPrefix;
    }

    @Override
    public RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if(info == null) return null;

        ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        if(methodAnnotation != null) {
            RequestCondition<?> methodCondition = getCustomMethodCondition(method);
            // Concatenate our ApiVersion with the usual request mapping
            info = createApiVersionInfo(methodAnnotation, methodCondition).combine(info);
        } else {
            ApiVersion typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);

            if(typeAnnotation != null) {
                RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);
                // Concatenate our ApiVersion with the usual request mapping
                info = createApiVersionInfo(typeAnnotation, typeCondition).combine(info);
            }
        }

        return info;
    }

    private RequestMappingInfo createApiVersionInfo(ApiVersion annotation, RequestCondition<?> customCondition) {
        int value = annotation.value();
        String[] patterns = { (StringUtils.isBlank(this.pathPrefix) ? "" : this.pathPrefix + "/") + versionPrefix + value };
        return new RequestMappingInfo(
            new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(), useSuffixPatternMatch(), useTrailingSlashMatch(), getFileExtensions()),
            new RequestMethodsRequestCondition(),
            new ParamsRequestCondition(),
            new HeadersRequestCondition(),
            new ConsumesRequestCondition(),
            new ProducesRequestCondition(),
            customCondition);
    }

}
