package roc.gtw.router

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import jakarta.annotation.PostConstruct
import jakarta.annotation.Resource
import roc.gtw.common.Env
import roc.gtw.router.entity.RouterInfo
import roc.gtw.router.repository.RouterRepository

import java.util.concurrent.TimeUnit

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/6 23:53
 */
class RouterService {


    LoadingCache<String, RouterInfo> cache


//    @Resource
//    private RouterCacheLoader routerCacheLoader

    @Resource
    private RouterRepository repository


    @PostConstruct
    private void initCache() {
        if (cache == null) {
            cache = CacheBuilder.newBuilder()
                    .maximumSize(10000)
                    .expireAfterWrite(3000, TimeUnit.SECONDS)
                    .expireAfterAccess(3000, TimeUnit.SECONDS)
                    .build(new CacheLoader<String, RouterInfo>() {
                        @Override
                        RouterInfo load(String key) throws Exception {
                            return repository.selectOne(Env.GROUP, key)
                        }
                    })
        }
    }


    List<RouterInfo> list(String routerGroup) {
        repository.list(routerGroup)
    }

    RouterInfo router(String uri) {
        def routerInfo = cache.get(uri)
        if (routerInfo == null) {
            throw new RuntimeException("路由不存在")
        }
        return routerInfo
    }

}