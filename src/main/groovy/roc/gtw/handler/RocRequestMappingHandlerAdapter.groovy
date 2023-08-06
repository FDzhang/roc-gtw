package roc.gtw.handler

import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import roc.gtw.annotation.RocRequestMapping

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/6 23:28
 */
class RocRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

    @Override
    protected boolean supportsInternal(HandlerMethod handlerMethod) {
        handlerMethod.method.isAnnotationPresent(RocRequestMapping.class)
    }
}
