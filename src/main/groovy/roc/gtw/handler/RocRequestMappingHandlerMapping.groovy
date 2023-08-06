package roc.gtw.handler

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import roc.gtw.common.CommonUtils

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/6 23:28
 */
class RocRequestMappingHandlerMapping extends AbstractRocRequestMappingHandlerMapping {


    @Override
    protected void handleMatch(RequestMappingInfo info, String lookupPath, HttpServletRequest request) {
        def router = routerService.router(lookupPath)
        request.setAttribute(CommonUtils.ROUTER_INFO, router)
        super.handleMatch(info, lookupPath, request)
    }


}
