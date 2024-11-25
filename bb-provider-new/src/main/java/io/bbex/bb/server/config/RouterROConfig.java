/*
 ************************************
 * @项目名称: bb-shard
 * @文件名称: RouterConfig
 * @Date 2018/08/22
 * @Author will.zhao@bbex.io
 * @Copyright（C）: 2018 BitBili Inc.   All rights reserved.
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 **************************************
 */
package io.bbex.bb.server.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.prometheus.PrometheusHistogramMetricsTrackerFactory;
import io.bbex.base.mysql.BBMysqlDataSource;
import io.bbex.bb.common.properties.RouterHikariProperties;
import io.bbex.exchange.mybatis.interceptor.MybatisPerfInterceptor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

@Configuration
@Slf4j
@MapperScan(basePackages = "io.bbex.bb.common.mapper.router.ro", sqlSessionFactoryRef = "routerroSessionFactory")
public class RouterROConfig {

    @Value("${spring.datasource.ro.url}")
    private String url;

    @Value("${spring.datasource.ro.username}")
    private String username;

    @Value("${spring.datasource.ro.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle}")
    private int minIdle;

    @Autowired
    private RouterHikariProperties routerHikariProperties;

    private final MybatisProperties myBatisProperties;

    public RouterROConfig(MybatisProperties myBatisProperties) {
        this.myBatisProperties = myBatisProperties;
    }

    @Bean(name = "routerroDataSource")
    public DataSource routerDataSource(PrometheusHistogramMetricsTrackerFactory prometheusHistogramMetricsTrackerFactory) {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .url(url).username(username).password(password).driverClassName(driverClassName)
                .type(HikariDataSource.class).build();
        dataSource.setMaximumPoolSize(maxPoolSize);
        dataSource.setMinimumIdle(minIdle);
        dataSource.setConnectionTimeout(routerHikariProperties.getConnectTimeOut());
        dataSource.setIdleTimeout(routerHikariProperties.getIdleTimeout());
        dataSource.setMaxLifetime(routerHikariProperties.getMaxLifeTime());
        dataSource.setValidationTimeout(routerHikariProperties.getValidationTimeOut());
        dataSource.setInitializationFailTimeout(routerHikariProperties.getInitializationFailTimeout());
        dataSource.setKeepaliveTime(routerHikariProperties.getKeepavlieTime());
        log.info("[DEBUG] create the BBMysqlDataSource for bb-server.");

        dataSource.setPoolName("router-ro");
        dataSource.setMetricsTrackerFactory(prometheusHistogramMetricsTrackerFactory);

        return dataSource;
    }

    @Bean(name = "routerroSqlSessionFactory")
    public SqlSessionFactory routerSessionFactory(@Qualifier("routerroDataSource") DataSource routerDataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setVfs(SpringBootVFS.class);
        bean.setDataSource(routerDataSource);
        bean.setPlugins(new MybatisPerfInterceptor[]{new MybatisPerfInterceptor()});
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        if (myBatisProperties.getConfiguration() != null) {
            BeanUtils.copyProperties(myBatisProperties.getConfiguration(), configuration);
        }
        bean.setConfiguration(configuration);
        return bean.getObject();
    }

//    @Bean
//    public PlatformTransactionManager routerTransactionManager(@Qualifier("routerroDataSource") DataSource routerDataSource) {
//        return new DataSourceTransactionManager(routerDataSource);
//    }
//
//    @Bean
//    public DatabaseMetrics databaseMetrics(@Qualifier("routerDataSource") DataSource routerDataSource) {
//        return new DatabaseMetrics(routerDataSource);
//    }

}
