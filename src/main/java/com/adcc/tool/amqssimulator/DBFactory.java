package com.adcc.tool.amqssimulator;

import com.adcc.tool.amqssimulator.util.LogUtil;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBFactory {
    // orcl为oracle数据库中的数据库名，localhost表示连接本机的oracle数据库
    // 1521为连接的端口号
    private String url = "jdbc:oracle:thin:@localhost:1521";
    private String user = "AGS";
    private String password = "AGS";
    private String sql;
    private String tableColumnName;

    private Connection conn;

    //连接数据库的方法
    public void getConnection() {
        try {
            //初始化驱动包
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //根据数据库连接字符，名称，密码给conn赋值
            conn = DriverManager.getConnection(url, user, password);

        } catch (Exception ex) {
            LogUtil.error(DBFactory.class.getName(), "getConnection() error", ex);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTableColumnName() {
        return tableColumnName;
    }

    public void setTableColumnName(String tableColumnName) {
        this.tableColumnName = tableColumnName;
    }

    //测试能否与oracle数据库连接成功
//	     public static void main(String[] args) {    
//	    	 ConnectFactory basedao=new ConnectFactory();    
//	        basedao.getConnection();    
//	        if(conn==null){    
//	            System.out.println("与oracle数据库连接失败！");    
//	        }else{    
//	            System.out.println("与oracle数据库连接成功！");    
//	        }    
//	     }    
}
