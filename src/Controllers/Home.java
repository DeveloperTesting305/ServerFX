package Controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.DatabaseImpt;
import sample.Main;

import javax.swing.*;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Home implements Initializable {

    @FXML private Button BTstart;
    @FXML private Button BTstop;
    @FXML private Button BTadminPanel;
    @FXML private JFXComboBox<String> CBselectFile;
    @FXML private JFXTextArea TAstatus;
    @FXML private MenuBar menuBar;

    public static Registry reg = null;
    public static String databaseIP = "127.0.0.1";
    public static String rmiIP = "";
    public static java.sql.Connection con;
    private static String databaseName = "";

    public static String userName = "root";
    public static String password = "";
    public static String sqlPort = "3306";
    public static String rootConnectionPath = "jdbc:mysql://"+databaseIP+":"+sqlPort+"/";
    public static String imageRootDir = "C:\\";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BTstop.setDisable(true);
        BTadminPanel.setDisable(true);
        try{
            Class.forName("com.mysql.jdbc.Driver");
            ////**** GET DATABASE NAMES AND SET INTO COMBO BOX
            getAllDatabaseName();
        }catch(Exception ee){
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
            TAstatus.appendText("\n"+ee.getMessage());
        }
    }

    private void getAllDatabaseName()throws Exception{
        java.sql.Connection conInit = DriverManager.getConnection(rootConnectionPath, userName, password);
        PreparedStatement stmt = conInit.prepareStatement("SHOW DATABASES");
        ResultSet set = stmt.executeQuery();
        //////***** CREATE DATABASES NAME ARRAY
        ArrayList<String> databasesList = new ArrayList<>();
        while(set.next()) databasesList.add(set.getString(1));
        CBselectFile.setItems(FXCollections.observableArrayList(databasesList));

        if(set != null) set.close();
        if(stmt != null) stmt.close();
        if(conInit != null) conInit.close();
    }



    ////**** START SERVER
    ////**** SELECT BY ENTER KEY
    @FXML private void KeyActionStart(KeyEvent event){
        if(event.getCode().equals(KeyCode.ENTER)) startAction();
    }
    @FXML private void startAction(){
        BTstart.setDisable(true);
        BTstop.setDisable(false);
        BTadminPanel.setDisable(false);
        CBselectFile.setDisable(true);
        menuBar.setDisable(true);
        try{
            databaseName = CBselectFile.getSelectionModel().getSelectedItem();
            imageRootDir += databaseName;
            if(databaseName.equals("")) {
                TAstatus.appendText("\n !.. Database Not Connected...");
                return;
            }
            //////***** CONNECTIONS
            DatabaseConnection();
            RmiConnection();

        }catch (Exception ee){
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
            TAstatus.appendText("\n"+ee.getMessage());
        }
    }



    ////**** STOP SERVER
    ////**** SELECT BY ENTER KEY
    @FXML private void KeyActionStop(KeyEvent event){
        if(event.getCode().equals(KeyCode.ENTER)) stopAction();
    }
    @FXML private void stopAction(){
        BTstop.setDisable(true);

        /////***** THREAD INTERFACE
        Runnable runnable = () -> {
            try {
                Main.closeConnections();
                TAstatus.appendText("\nDatabase Connection Closed!...");
                TAstatus.appendText("\nUnbind Object RMI Closed!...");
                TAstatus.appendText("\n\nShutdown in 2 sec...");

                Thread.sleep(2000);
                System.exit(0);
            }catch (Exception ee){
                JOptionPane.showMessageDialog(null, "Somethimg Wrong! "+ee.getMessage());
                TAstatus.appendText("\n"+ee.getMessage());
            }
        };
        //////***** STRAT THRAED
        new Thread(runnable).start();
    }


    private void DatabaseConnection()throws Exception{
        /////////////////************************* ESTABLISHED DATABASE CONNECTION
        con = DriverManager.getConnection("jdbc:mysql://"+ databaseIP +":3306/"+databaseName, "root", "");
        System.out.println("Database Connected");
        TAstatus.appendText("\nDatabase Connected...");
    }


    private void RmiConnection()throws Exception{

        ////**** JAVA SECURITY PERMISSION
        System.setProperty("java.rmi.server.hostname", rmiIP);

        ////**** BIND OBJECT IN REGISTRY
        DatabaseImpt ob = new DatabaseImpt();
        reg = LocateRegistry.createRegistry(1099);
        reg.rebind("rmi://"+ rmiIP +"/inventoryDatabase", ob);


        System.out.println("RMI Registry Connected!...");
        TAstatus.appendText("\nIP Address : "+rmiIP);
        TAstatus.appendText("\nRMI Registry Running...");
        TAstatus.appendText("\nServer is is Running...");
    }


    ///////////********** SYSTEM **********///////////

    ///////****** ADMIN PANEL
    @FXML private void adminPanel(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/adminPanel.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Admin Panel");
            stage.setScene(new Scene(root));
            stage.show();
        }catch (Exception ee){
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
            TAstatus.appendText("\n"+ee.getMessage());
        }
    }


    ///////****** IP SETTING
    @FXML private void ipSetting(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/ipSetting.fxml"));
            Stage stage = new Stage();
            stage.setTitle("IP Setting RMI");
            stage.setScene(new Scene(root));
            stage.show();
        }catch (Exception ee){
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
            TAstatus.appendText("\n"+ee.getMessage());
        }
    }

    ///////****** MOBILE DATA UPLOAD
    @FXML private void mobileDataUpload(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/mobileDataUpload.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Mobile Data Upload");
            stage.setScene(new Scene(root));
            stage.show();
        }catch (Exception ee){
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
            TAstatus.appendText("\n"+ee.getMessage());
        }
    }
    ///////////********** SYSTEM **********///////////


    ///////////********** FILE **********///////////
    ///////****** NEW FILE
    @FXML private void newFile(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/newFile.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Create New File");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        }catch (Exception ee){
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
            TAstatus.appendText("\n"+ee.getMessage());
        }
    }

    ///////****** CLOSING
    @FXML private void closing(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/selectCreateDatabases.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Select & Create File");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        }catch (Exception ee){
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
            TAstatus.appendText("\n"+ee.getMessage());
        }
    }


    ///////////********** FILE **********///////////


    /////***** CLOSE
    private void close(){
        ((Stage)(menuBar.getScene().getWindow())).close();
    }



}
