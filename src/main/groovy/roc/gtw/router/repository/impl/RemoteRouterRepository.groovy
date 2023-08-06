package roc.gtw.router.repository.impl

import com.google.common.collect.Lists
import roc.gtw.enums.Protocol
import roc.gtw.router.entity.RouterInfo
import roc.gtw.router.repository.RouterRepository

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/7 0:10
 */
class RemoteRouterRepository implements RouterRepository {
    @Override
    List<RouterInfo> list(String group) {

        def list = Lists.newArrayList()
        def info = new RouterInfo()
        info.uri = "888"
        list.add(info)
        return list
    }

    @Override
    RouterInfo selectOne(String group, String uri) {
        def info = new RouterInfo()
        info.uri = "888"
        info.protocol = Protocol.DUBBO.type
        return info
    }
}
