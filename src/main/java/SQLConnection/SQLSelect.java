package SQLConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLSelect {

    private Connection CONNECTION;
    //Hier werden alle Select befehle geschrieben die von nöten sind
    public void SQLSelect(){
        try{
            Class.forName("org.sqlite.JDBC");
            CONNECTION = DriverManager.getConnection(String.format("jdbc:sqlite:finanzplannerDB.db",0));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ResultSet SqlSelectTable(String tableName){
        Statement statement;
        ResultSet rs = null;
        try{
            statement = CONNECTION.createStatement();
            String sqlSelect = "SELCET * FROM "+tableName+";";
            rs = statement.executeQuery(sqlSelect);
            statement.close();
            return rs;
        }catch (Exception e){
            e.printStackTrace();
        }
        return rs;
    }
}
