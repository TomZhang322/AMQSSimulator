package com.adcc.tool.amqssimulator;

import com.adcc.tool.amqssimulator.util.LogUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;

/**
 * 配置类
 */
public class AppConfiguration {

    // 配置文件路径
    private static String path = System.getProperty("user.dir")+ File.separator+"conf"+ File.separator+"conf.yml";

    // 单例对象
    private static AppConfiguration instance;

    private IBMMQFactory ibmmqFactory;

    private ActiveMQFactory activeMQFactory;

    private DBFactory dbFactory;

    /**
     * 构造方法
     * */
    private AppConfiguration() {}

    /**
     * 单例方法
     * */
    public synchronized static AppConfiguration getInstance() {
        if (instance == null) {
            instance = getAppConfiguration();
        }
        return instance;
    }

    private static AppConfiguration getAppConfiguration() {
        AppConfiguration appConfiguration = null;
        FileInputStream fi = null;
        try {
            fi = new FileInputStream(new File(path));
            appConfiguration = new Yaml().loadAs(fi, AppConfiguration.class);
        } catch (Exception ex) {
            LogUtil.error(AppConfiguration.class.getName(), "getAppConfiguration() error", ex);
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
            } catch (Exception ex) {
                LogUtil.error(AppConfiguration.class.getName(), "close configFile failed!", ex);
            } finally {
                fi = null;
            }
        }
        return appConfiguration;
    }

    public IBMMQFactory getIbmmqFactory() {
        return ibmmqFactory;
    }

    public void setIbmmqFactory(IBMMQFactory ibmmqFactory) {
        this.ibmmqFactory = ibmmqFactory;
    }

    public ActiveMQFactory getActiveMQFactory() {
        return activeMQFactory;
    }

    public void setActiveMQFactory(ActiveMQFactory activeMQFactory) {
        this.activeMQFactory = activeMQFactory;
    }

    public DBFactory getDbFactory() {
        return dbFactory;
    }

    public void setDbFactory(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }
}
