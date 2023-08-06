package roc.gtw.controller

import com.google.common.collect.Maps
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*
import roc.gtw.annotation.RocGtw
import roc.gtw.annotation.RocRequestMapping
import roc.gtw.common.CommonUtils
import roc.gtw.common.Env
import roc.gtw.router.entity.RouterInfo

import java.time.LocalDateTime

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/6 23:39
 */
@RocGtw
class RocController {

    @RocRequestMapping(method = [RequestMethod.GET, RequestMethod.POST], consumes = ["text/plain", "application/*"])
    @ResponseBody
    Object call(@PathVariable Map<String, Object> variables,
                @RequestParam Map<String, Object> parameters,
                @RequestBody(required = false) Map<String, Object> requestBody,
                HttpServletRequest request,
                HttpServletResponse response) {

        if (empty(requestBody)) {
            requestBody = Maps.newHashMap()
        }

        if (!empty(variables)) {
            requestBody.putAll(variables)
        }
        if (!empty(parameters)) {
            requestBody.putAll(parameters)
        }

        RouterInfo routerInfo = request.getAttribute(CommonUtils.ROUTER_INFO) as RouterInfo
        requestBody.put("now", LocalDateTime.now())
        requestBody.put("now_ms", System.currentTimeMillis())
        requestBody.put("protocol", routerInfo.protocol)
        requestBody.put("group", Env.GROUP)

        //TODO 根据不同的protocol，通过不同的策略调用业务接口，暂时支持 dubbo（泛化调用），
        // http，krpc（改造krpc，使得其支持泛化调用）

        return requestBody
    }


    private static boolean empty(Map map) {
        return map == null || map.isEmpty()
    }
}
