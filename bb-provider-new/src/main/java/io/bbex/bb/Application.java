package io.bbex.bb;

import com.zaxxer.hikari.metrics.prometheus.PrometheusHistogramMetricsTrackerFactory;
import io.bbex.base.grpc.metrics.TomcatConnectPoolMetricsSupport;
import io.bbex.base.metrics.PrometheusMetricsCollector;
import io.bbex.bb.server.config.GrpcClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Map;

/**
 * @author wangxuefei
 */
@SpringBootApplication
@Slf4j
@EnableScheduling
@ComponentScan(basePackages = "io.bbex", excludeFilters =
        {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "io.bbex.match.leader.election.config.*"),
         @ComponentScan.Filter(type = FilterType.REGEX, pattern = "io.bbex.match.leader.election.kafka.*")
        }
)
public class Application {

    public static final String ALERT = "[ALERT]";

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        return loggingFilter;
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> tomcatPoolMetricsSupport(TomcatConnectPoolMetricsSupport tomcatConnectPoolMetricsSupport) {
        return factory -> {
            if (factory instanceof TomcatServletWebServerFactory) {
                log.info("Customizer for add Tomcat connect pool metrics support");
                ((TomcatServletWebServerFactory) factory).addConnectorCustomizers(tomcatConnectPoolMetricsSupport);
            } else {
                log.warn("NOT Tomcat, so do not add connect pool metrics support");
            }
        };
    }

    /**
     * do not use ContextRefreshEvent, because tomcat init executor after that event
     *
     * @param applicationContext
     * @return
     */
    @Bean
    public ApplicationListener<ApplicationReadyEvent> registerPoolMetrics(ApplicationContext applicationContext) {
        return event -> {
            log.info("[INFO] on ApplicationReadyEvent, add all pool to metrics ");
            Map<String, ThreadPoolTaskExecutor> taskExecutorMap =  applicationContext.getBeansOfType(ThreadPoolTaskExecutor.class);
            for (String name : taskExecutorMap.keySet()) {
                ThreadPoolTaskExecutor executor = taskExecutorMap.get(name);
                log.info("register to metrics ThreadPoolTaskExecutor: {} = {}", name, executor);
                PrometheusMetricsCollector.registerThreadPoolExecutor(executor.getThreadPoolExecutor(), name);
            }

            Map<String, TomcatConnectPoolMetricsSupport> tomcatMetricsSupports = applicationContext.getBeansOfType(TomcatConnectPoolMetricsSupport.class);
            for (TomcatConnectPoolMetricsSupport support : tomcatMetricsSupports.values()) {
                log.info("addTomcatConnectPoolMetrics for: {} ", support);
                support.addTomcatConnectPoolMetrics();
            }
        };
    }

    @Bean
    public GrpcClientProperties grpcClientProperties() {
        return new GrpcClientProperties();
    }

    @Bean
    public PrometheusHistogramMetricsTrackerFactory prometheusHistogramMetricsTrackerFactory() {
        return new PrometheusHistogramMetricsTrackerFactory();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

