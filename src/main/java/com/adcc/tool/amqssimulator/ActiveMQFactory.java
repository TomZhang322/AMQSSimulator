package com.adcc.tool.amqssimulator;


/**
 * ACTIVEMQ配置工厂
 */
public class ActiveMQFactory {

    // 连接地址
    private String url;

    // 用户名
    private String user;

    // 密码
    private String password;

    // 接收队列
    private String recvQueue;

    // 最大连接数
    private int maxConnections;

    // 最大活跃数
    private int maxActive;

    // 空闲时间
    private int idelTimeout;

    // 超时时间
    private int expiryTimeout;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecvQueue() {
        return recvQueue;
    }

    public void setRecvQueue(String recvQueue) {
        this.recvQueue = recvQueue;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getIdelTimeout() {
        return idelTimeout;
    }

    public void setIdelTimeout(int idelTimeout) {
        this.idelTimeout = idelTimeout;
    }

    public int getExpiryTimeout() {
        return expiryTimeout;
    }

    public void setExpiryTimeout(int expiryTimeout) {
        this.expiryTimeout = expiryTimeout;
    }
}
