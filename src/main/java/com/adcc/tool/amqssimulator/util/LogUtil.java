package com.adcc.tool.amqssimulator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志处理类
 */
public final class LogUtil {

    // log
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    /**
     * info日志
     * @param log
     */
    public static void info(String log){
        try{
            logger.info(log);
        }catch (Exception ex){
            throw ex;
        }
    }
    
    /**
     * info日志
     * @param name
     * @param log
     */
    public static void info(String name,String log){
        try{
            LoggerFactory.getLogger(name).info(log);
        }catch (Exception ex){
            throw ex;
        }
    }

    /**
     * debug日志
     * @param log
     */
    public static void debug(String log){
        try{
            logger.debug(log);
        }catch (Exception ex){
            throw ex;
        }
    }

    /**
     * debug日志
     * @param log
     * @param e
     */
    public static void debug(String log,Exception e){
        try{
            logger.debug(log,e);
        }catch (Exception ex){
            throw ex;
        }
    }

    /**
     * debug日志
     * @param name
     */
    public static void debug(String name,String log){
        try{
            LoggerFactory.getLogger(name).debug(log);
        }catch (Exception ex){
            throw ex;
        }
    }

    /**
     * debug日志
     * @param name
     * @param log
     * @param e
     */
    public static void debug(String name,String log,Exception e){
        try{
            LoggerFactory.getLogger(name).debug(log,e);
        }catch (Exception ex){
            throw ex;
        }
    }
    
    /**
     * error日志
     * @param log
     */
    public static void error(String log){
        try{
            logger.error(log);
        }catch (Exception ex){
            throw ex;
        }
    }

    /**
     * error日志
     * @param log
     */
    public static void error(String log,Exception e){
        try{
            logger.error(log,e);
        }catch (Exception ex){
            throw ex;
        }
    }

    /**
     * error日志
     * @param name
     * @param log
     */
    public static void error(String name,String log){
        try{
            LoggerFactory.getLogger(name).error(log);
        }catch (Exception ex){
            throw ex;
        }
    }

    /**
     * error日志
     * @param name
     * @param log
     * @param e
     */
    public static void error(String name,String log,Exception e){
        try{
            LoggerFactory.getLogger(name).error(log,e);
        }catch (Exception ex){
            throw ex;
        }
    }
}
