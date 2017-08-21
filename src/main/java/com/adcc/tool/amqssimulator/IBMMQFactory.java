package com.adcc.tool.amqssimulator;

/**
 * IBMMQ配置工厂
 */
public class IBMMQFactory {

    private String host;

    private int port;

    private String queueManager;

    private String channel;

    private String recvQueue;

    private String sendQueue;

    // ActiveMode
    private int activeMode;

    // 连接超时时间
    private long timeout;

    // 最大连接数
    private int maxConnections;

    // 最大空闲连接数
    private int maxIdelConnections;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRecvQueue() {
        return recvQueue;
    }

    public void setRecvQueue(String recvQueue) {
        this.recvQueue = recvQueue;
    }

    public String getSendQueue() {
        return sendQueue;
    }

    public void setSendQueue(String sendQueue) {
        this.sendQueue = sendQueue;
    }

    public int getActiveMode() {
        return activeMode;
    }

    public void setActiveMode(int activeMode) {
        this.activeMode = activeMode;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxIdelConnections() {
        return maxIdelConnections;
    }

    public void setMaxIdelConnections(int maxIdelConnections) {
        this.maxIdelConnections = maxIdelConnections;
    }
}
