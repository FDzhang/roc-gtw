package roc.gtw.annotation

import org.springframework.context.annotation.Import
import roc.gtw.configuration.HandlerConfiguration
import roc.gtw.configuration.RouterConfiguration

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/7 1:08
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import(value = [HandlerConfiguration.class, RouterConfiguration.class])
@interface EnableRocGtw {
    //TODO 支持某些简单应用使用local配置
    boolean remote() default true

}