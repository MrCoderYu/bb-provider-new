package io.bbex.bb.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.datasource.hikari")
public class RouterHikariProperties {

    private int connectTimeOut;
    private int idleTimeout;
    private int maxLifeTime;
    private int validationTimeOut;
    private int initializationFailTimeout;
    private int keepavlieTime;

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getMaxLifeTime() {
        return maxLifeTime;
    }

    public void setMaxLifeTime(int maxLifeTime) {
        this.maxLifeTime = maxLifeTime;
    }

    public int getValidationTimeOut() {
        return validationTimeOut;
    }

    public void setValidationTimeOut(int validationTimeOut) {
        this.validationTimeOut = validationTimeOut;
    }

    public int getInitializationFailTimeout() {
        return initializationFailTimeout;
    }

    public void setInitializationFailTimeout(int initializationFailTimeout) {
        this.initializationFailTimeout = initializationFailTimeout;
    }

    public int getKeepavlieTime() {
        return keepavlieTime;
    }

    public void setKeepavlieTime(int keepavlieTime) {
        this.keepavlieTime = keepavlieTime;
    }
}
