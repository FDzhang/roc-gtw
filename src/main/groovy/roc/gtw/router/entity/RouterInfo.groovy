package roc.gtw.router.entity

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/6 23:54
 */
class RouterInfo {

    long id
    long serviceId
    /**
     * 用户组
     */
    String group
    String serviceName
    String uri
    String produces

    /**
     * @see roc.gtw.enums.Protocol
     */
    int protocol

}
