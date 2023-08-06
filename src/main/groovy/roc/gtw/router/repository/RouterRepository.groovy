package roc.gtw.router.repository

import roc.gtw.router.entity.RouterInfo

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/7 0:09
 */
interface RouterRepository {

    List<RouterInfo> list(String group)

    RouterInfo selectOne(String group, String uri)

}