package roc.gtw.configuration

import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.format.support.FormattingConversionService
import org.springframework.lang.Nullable
import org.springframework.util.ClassUtils
import org.springframework.validation.Validator
import org.springframework.web.accept.ContentNegotiationManager
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.springframework.web.servlet.handler.AbstractHandlerMapping
import org.springframework.web.servlet.mvc.method.annotation.JsonViewRequestBodyAdvice
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice
import org.springframework.web.servlet.resource.ResourceUrlProvider
import roc.gtw.handler.RocRequestMappingHandlerAdapter
import roc.gtw.handler.RocRequestMappingHandlerMapping

import java.util.function.Predicate

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/6 23:49
 */
class HandlerConfiguration {

    private static final boolean jackson2Present

    static {
        def classLoader = HandlerConfiguration.classLoader
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) &&
                ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader)
    }


    @Resource
    private WebMvcConfigurationSupport webMvcConfigurationSupport


    @Bean
    RocRequestMappingHandlerMapping rocRequestMappingHandlerMapping(
            @Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
            @Qualifier("mvcConversionService") FormattingConversionService conversionService,
            @Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
        def mapping = new RocRequestMappingHandlerMapping()
        mapping.setOrder(Integer.MIN_VALUE)
        mapping.setContentNegotiationManager(contentNegotiationManager)

        initHandlerMapping(mapping, conversionService, resourceUrlProvider)

        PathMatchConfigurer pathConfig = invoke("getPathMatchConfigurer")
        if (invokePathMatchConfigurer(pathConfig, "preferPathMatcher")) {
            Boolean useSuffixPatternMatch = pathConfig.isUseSuffixPatternMatch()
            if (useSuffixPatternMatch != null) {
                mapping.setUseSuffixPatternMatch(useSuffixPatternMatch)
            }
            Boolean useRegisteredSuffixPatternMatch = pathConfig.isUseRegisteredSuffixPatternMatch()
            if (useRegisteredSuffixPatternMatch != null) {
                mapping.setUseRegisteredSuffixPatternMatch(useRegisteredSuffixPatternMatch)
            }
        }

        Boolean useTrailingSlashMatch = pathConfig.isUseTrailingSlashMatch()
        if (useTrailingSlashMatch != null) {
            mapping.setUseTrailingSlashMatch(useTrailingSlashMatch)
        }

        def pathPrefixes = invokePathMatchConfigurer(pathConfig, "getPathPrefixes")

        if (pathPrefixes != null) {
            mapping.setPathPrefixes(pathPrefixes as Map<String, Predicate<Class<?>>>)
        }
        mapping
    }

    private void initHandlerMapping(
            @Nullable AbstractHandlerMapping mapping, FormattingConversionService conversionService,
            ResourceUrlProvider resourceUrlProvider) {

        if (mapping == null) {
            return
        }
        PathMatchConfigurer pathConfig = invoke("getPathMatchConfigurer")
        if (invokePathMatchConfigurer(pathConfig, "preferPathMatcher")) {
            mapping.setPatternParser(null)
            mapping.setUrlPathHelper(invokePathMatchConfigurer(pathConfig, "getUrlPathHelperOrDefault"))
            mapping.setPathMatcher(invokePathMatchConfigurer(pathConfig, "getPathMatcherOrDefault"))
        } else if (pathConfig.getPatternParser() != null) {
            mapping.setPatternParser(pathConfig.getPatternParser())
        }
        mapping.setInterceptors(getInterceptors(conversionService, resourceUrlProvider))
        mapping.setCorsConfigurations(invoke("getCorsConfigurations"))
    }

    private <T> T invoke(String methodName) {
        def method = WebMvcConfigurationSupport.class.getDeclaredMethod(methodName)
        method.accessible = true
        method.invoke(this.webMvcConfigurationSupport) as T
    }

    private <T> T getInterceptors(FormattingConversionService mvcConversionService,
                                  ResourceUrlProvider mvcResourceUrlProvider) {
        def method = WebMvcConfigurationSupport.class.getDeclaredMethod("getInterceptors", FormattingConversionService.class, ResourceUrlProvider.class)
        method.accessible = true
        method.invoke(this.webMvcConfigurationSupport, mvcConversionService, mvcResourceUrlProvider) as T
    }

    private <T> T invokePathMatchConfigurer(PathMatchConfigurer configurer, String methodName) {
        def method = PathMatchConfigurer.class.getDeclaredMethod(methodName)
        method.accessible = true
        method.invoke(configurer) as T
    }

    private <T> T involeAsyncSupportConfigurer(AsyncSupportConfigurer configurer, String methodName) {
        def method = AsyncSupportConfigurer.class.getDeclaredMethod(methodName)
        method.accessible = true
        method.invoke(configurer) as T
    }


    @Bean
    RocRequestMappingHandlerAdapter rocRequestMappingHandlerAdapter(
            @Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
            @Qualifier("mvcConversionService") FormattingConversionService conversionService,
            @Qualifier("mvcValidator") Validator validator) {
        def adapter = new RocRequestMappingHandlerAdapter()
        adapter.setOrder(Integer.MIN_VALUE)

        adapter.setContentNegotiationManager(contentNegotiationManager);
        adapter.setMessageConverters(invoke("getMessageConverters"));
        adapter.setWebBindingInitializer(getConfigurableWebBindingInitializer(conversionService, validator))
        adapter.setCustomArgumentResolvers(invoke("getArgumentResolvers"))
        adapter.setCustomReturnValueHandlers(invoke("getReturnValueHandlers"))

        if (jackson2Present) {
            adapter.setRequestBodyAdvice(Collections.singletonList(new JsonViewRequestBodyAdvice()));
            adapter.setResponseBodyAdvice(Collections.singletonList(new JsonViewResponseBodyAdvice()));
        }

        AsyncSupportConfigurer configurer = invoke("getAsyncSupportConfigurer")


        def taskExecutor = involeAsyncSupportConfigurer(configurer, "getTaskExecutor")
        if (taskExecutor != null) {
            adapter.setTaskExecutor(taskExecutor as AsyncTaskExecutor)
        }
        def timeout = involeAsyncSupportConfigurer(configurer, "getTimeout")
        if (timeout != null) {
            adapter.setAsyncRequestTimeout(timeout as long)
        }
        adapter.setCallableInterceptors(involeAsyncSupportConfigurer(configurer, "getCallableInterceptors"))
        adapter.setDeferredResultInterceptors(involeAsyncSupportConfigurer(configurer, "getDeferredResultInterceptors"))

        return adapter
    }

    private <T> T getConfigurableWebBindingInitializer(FormattingConversionService mvcConversionService, Validator mvcValidator) {
        def method = WebMvcConfigurationSupport.class.getDeclaredMethod("getConfigurableWebBindingInitializer", FormattingConversionService.class, Validator.class)
        method.accessible = true
        method.invoke(this.webMvcConfigurationSupport, mvcConversionService, mvcValidator) as T
    }
}
