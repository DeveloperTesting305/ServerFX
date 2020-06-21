package Controllers;

import Customs.Values;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javax.swing.*;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

public class NewFile implements Initializable {

    @FXML private JFXTextField TFfileName;
    @FXML private Button BTcreateNew;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML private void createNewAction(){
        String newName = TFfileName.getText().trim();
        try {
            /////***** CREATE QUERY ACCORDING TO DB NAME *****/////
            String[] queries = Values.getDatabaseSturctureQueries(newName);

            ////**** CREATE CONNECTION
            java.sql.Connection rootCon = DriverManager.getConnection(Home.rootConnectionPath, Home.userName, Home.password);

            /////***** CREATE DATABASE STRUCTURE
            for (String query : queries) {
                PreparedStatement stmt = rootCon.prepareStatement(query);
                Integer row = stmt.executeUpdate();
                if (stmt != null) stmt.close();
            }

            ////**** CLOSE CONNECTION
            if (rootCon != null) rootCon.close();

            ////**** TURN OFF
            System.exit(0);

            /////***** CREATE QUERY ACCORDING TO DB NAME *****/////

        }catch (Exception ee){
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!" + ee.getMessage());
            ee.printStackTrace();
        }
    }
}
