package roc.gtw

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import roc.gtw.annotation.EnableRocGtw

/**
 * zcc_dev@163.com
 * @author CCZ
 * 2023/8/7 1:21
 */
@SpringBootApplication
@EnableRocGtw
class Application {
    static void main(String[] args) {
        SpringApplication.run(Application.class, args)
    }
}
