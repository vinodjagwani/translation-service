/**
 * Author: Vinod Jagwani
 */
package se.digitaltolk.translation.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.netty.LogbookServerHandler;
import reactor.netty.http.server.HttpServer;

@Configuration(proxyBeanMethods = false)
public class LogbookFluxConfig {

    @Bean
    public Logbook logbook() {
        return Logbook.create();
    }

    @Bean
    @ConditionalOnClass(HttpServer.class)
    @ConditionalOnMissingBean(name = "logbookNettyServerCustomizer")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @ConditionalOnProperty(name = "logbook.filter.enabled", havingValue = "true", matchIfMissing = true)
    public NettyServerCustomizer nettyServerCustomizer(final Logbook logbook) {
        return httpServer -> httpServer
                .doOnConnection(connection -> connection.addHandlerLast(new LogbookServerHandler(logbook)));
    }

}
