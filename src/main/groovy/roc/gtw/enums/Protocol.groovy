package roc.gtw.enums

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/6 23:57
 */
enum Protocol {

    DUBBO(1, "dubbo接口")
    ;
    final int type
    final String desc

    Protocol(int type, String desc) {
        this.type = type
        this.desc = desc
    }
}