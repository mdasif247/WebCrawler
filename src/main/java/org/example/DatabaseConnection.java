package org.example;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

public class DatabaseConnection {
    static Connection connection=null;
    public static Connection getConnection(){
        if(connection!=null){
            return connection;
        }
        String db="searchperson";
        String user="root";
        String pwd="240799@Ias";
        return getConnection(db,user,pwd);
    }
    private static Connection getConnection(String db,String user,String pwd){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver"); //com.mysql is the library i have added and will extract it.
            //sets up the connection to the mysql server
            connection= DriverManager.getConnection("jdbc:mysql://localhost/"+db+"?user="+user+"&password="+pwd);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return connection;
    }
}
