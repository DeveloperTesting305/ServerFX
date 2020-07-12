package sample;

import Controllers.Home;
import javafx.collections.FXCollections;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Custom {

    public static ArrayList getAllDatabaseName()throws Exception{
        java.sql.Connection conInit = DriverManager.getConnection(Home.rootConnectionPath, Home.userName, Home.password);
        PreparedStatement stmt = conInit.prepareStatement("SHOW DATABASES");
        ResultSet set = stmt.executeQuery();
        //////***** CREATE DATABASES NAME ARRAY
        ArrayList<String> databasesList = new ArrayList<>();
        while(set.next()) databasesList.add(set.getString(1));

        if(set != null) set.close();
        if(stmt != null) stmt.close();
        if(conInit != null) conInit.close();

        return databasesList;
    }

}
