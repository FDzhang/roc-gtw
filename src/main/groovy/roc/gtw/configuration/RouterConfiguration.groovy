package roc.gtw.configuration

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import roc.gtw.router.RouterService
import roc.gtw.router.repository.RouterRepository
import roc.gtw.router.repository.impl.RemoteRouterRepository

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/7 0:59
 */

class RouterConfiguration {


    @Bean
    RouterService routerService() {
        new RouterService()
    }

    @Bean
    @ConditionalOnMissingBean(RouterRepository.class)
    RouterRepository remoteRouterRepository() {
        new RemoteRouterRepository()
    }

}
