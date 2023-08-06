package roc.gtw.annotation

import org.springframework.stereotype.Component

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/6 23:41
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface RocGtw {

}