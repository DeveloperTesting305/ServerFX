package Controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SelectCreateDatabases implements Initializable {


    @FXML private JFXComboBox<String> CBoldFile;
    @FXML private JFXTextField TFnewFile;
    @FXML private Button BTcancel;
    @FXML private Button BTnext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            getAllDatabaseName();
        }catch (Exception ee){
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
            ee.printStackTrace();
        }
    }

    /////***** GET DATABASES NAMES
    private void getAllDatabaseName()throws Exception{
        java.sql.Connection conInit = DriverManager.getConnection(Home.rootConnectionPath, Home.userName, Home.password);
        PreparedStatement stmt = conInit.prepareStatement("SHOW DATABASES");
        ResultSet set = stmt.executeQuery();
        //////***** CREATE DATABASES NAME ARRAY
        ArrayList<String> databasesList = new ArrayList<>();
        while(set.next()) databasesList.add(set.getString(1));
        CBoldFile.setItems(FXCollections.observableArrayList(databasesList));

        if(set != null) set.close();
        if(stmt != null) stmt.close();
        if(conInit != null) conInit.close();
    }



    /////***** NEXT BUTTON
    @FXML private void nextAction(){
        if( CBoldFile.getSelectionModel() == null | TFnewFile.getText().equals("") ){
            JOptionPane.showMessageDialog(null, "Empty Fields!...");
        }
        else closing();
    }

    /////***** CANCEL BUTTON
    @FXML private void cancelAction(){
        close();
    }

    /////***** CLOSING WORK
    private void closing(){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/closingWork.fxml"));
            Parent root = loader.load();
            ClosingWork closingWork = loader.getController();
            //**                            OLD DATABASE NAME                       NEW DATABASE NAME   **//
            closingWork.setName(CBoldFile.getSelectionModel().getSelectedItem(), TFnewFile.getText().trim());
            Stage stage = new Stage();
            stage.setTitle("File Closing");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        }catch (Exception ee){
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
            ee.printStackTrace();
        }
    }

    /////***** CLOSE
    private void close(){
        ((Stage)(TFnewFile.getScene().getWindow())).close();
    }

}
