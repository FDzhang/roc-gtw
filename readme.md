一个通用的分布式网关，设想支持：
1.接口在线注册、注销
2.接口可以分为通用接口、接口编排、文件上传等
2.支持多种协议，dubbo，http等
3.支持服务编排，将接口抽象成资源，随意组合编排接口，并行、串行调用，并支持使用groovy脚本对接口入参响应进行编辑。
4.当前仅支持springmvc模式，计划支持spring-webflux
5.接口管控端

对springMVC的定制化改造，具体逻辑可见 roc.gtw.handler.RocRequestMappingHandlerMapping，
roc.gtw.handler.RocRequestMappingHandlerAdapter

当前项目下依赖了spring-web的全额依赖，后续可逐步删除，保留必要组件即可，这里依赖是为了方便测试

@EnableRocGtw 开启通用路由 
1