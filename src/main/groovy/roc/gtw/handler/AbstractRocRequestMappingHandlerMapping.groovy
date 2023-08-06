package roc.gtw.handler

import jakarta.annotation.Resource
import org.springframework.aop.support.AopUtils
import org.springframework.core.MethodIntrospector
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.lang.Nullable
import org.springframework.util.ClassUtils
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils
import org.springframework.web.servlet.mvc.condition.RequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import roc.gtw.annotation.RocGtw
import roc.gtw.annotation.RocRequestMapping
import roc.gtw.common.Env
import roc.gtw.handler.spring.SpringRequestMappingHandlerMapping
import roc.gtw.router.RouterService
import roc.gtw.router.entity.RouterInfo

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/6 23:26
 */
abstract class AbstractRocRequestMappingHandlerMapping extends SpringRequestMappingHandlerMapping {


    @Resource
    protected RouterService routerService

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, RocGtw.class)
    }

    @Override
    protected void detectHandlerMethods(Object handler) {
        Class<?> handlerType
        if (handler instanceof String) {
            handlerType = obtainApplicationContext().getType(handler)
        } else {
            handlerType = handler.class
        }
        if (handlerType != null) {
            Class<?> userType = ClassUtils.getUserClass(handlerType)
            def list = routerService.list(Env.GROUP)
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach {
                    def methods = selectMethods(userType, it)
                    methods.forEach((method, mapping) -> {
                        Method invocableMethod = AopUtils.selectInvocableMethod(method, userType)
                        registerHandlerMethod(handler, invocableMethod, mapping)
                    });
                }
            }
        }

    }

    Map<Method, RequestMappingInfo> selectMethods(Class<?> userType, RouterInfo routerInfo) {
        MethodIntrospector.selectMethods(userType,
                (MethodIntrospector.MetadataLookup<RequestMappingInfo>) (method) -> {
                    try {
                        getMappingForMethod(method, userType, routerInfo)
                    }
                    catch (Throwable ex) {
                        throw new IllegalStateException("Invalid mapping on handler class [" +
                                userType.getName() + "]: " + method, ex)
                    }
                })
    }

    RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType, RouterInfo routerInfo) {
        RequestMappingInfo info = createRequestMappingInfo(method, routerInfo)
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType, routerInfo)
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
            if (info.isEmptyMapping()) {
                info = info.mutate().paths("", "/").options(this.config).build()
            }
            String prefix = getPathPrefix(handlerType)
            if (prefix != null) {
                info = RequestMappingInfo.paths(prefix).options(this.config).build().combine(info)
            }
        }
        return info;
    }

    RequestMappingInfo createRequestMappingInfo(AnnotatedElement element, RouterInfo routerInfo) {
        RocRequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RocRequestMapping.class)
        RequestCondition<?> condition
        if (element instanceof Class<?>) {
            condition = getCustomTypeCondition(element)
        } else {
            condition = getCustomMethodCondition((Method) element)
        }
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition, routerInfo) : null)
    }

    RequestMappingInfo createRequestMappingInfo(
            RocRequestMapping requestMapping, @Nullable RequestCondition<?> customCondition, RouterInfo routerInfo) {
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(resolveEmbeddedValuesInPatterns(new String[]{routerInfo.getUri()}))
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(StringUtils.tokenizeToStringArray(routerInfo.produces, ","))
                .mappingName(requestMapping.name())
        if (customCondition != null) {
            builder.customCondition(customCondition)
        }
        return builder.options(this.config).build()
    }
}
