package com.e2call.db;

import java.io.Serializable;
import java.sql.*;

/**
 *
 * @author FEDE
 */
public class MysqlManager implements Serializable {
    
    private static Connection conn = null;
    private static String dburl = "jdbc:mysql://localhost:3306/mydb";
    private static String username = "root";
    private static String password = "root";
    
    public static Connection GetMysqlConn() throws Exception {
                
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }

        conn = DriverManager.getConnection(dburl,username,password);        
        return conn;
    }
}
