package Controllers;

import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sample.Custom;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ExportJSONData implements Initializable {

    @FXML private JFXComboBox<String> CBselectFile;
    @FXML private Button BTcreateBackup;
    @FXML private ProgressBar progressBar;
    @FXML private Label LBpath;
    @FXML private Button BTshowInFolder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            CBselectFile.setItems(FXCollections.observableArrayList(Custom.getAllDatabaseName()));
        }catch (Exception ee){
            ee.printStackTrace();
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
        }
    }

    @FXML private void createBackup(){
        String dbName = CBselectFile.getSelectionModel().getSelectedItem();
        if(dbName == null | dbName.equals("")) return;

        Stage stage = ((Stage)(CBselectFile.getScene().getWindow()));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("untitle");
        File file = fileChooser.showSaveDialog(stage);

        if(file == null){ JOptionPane.showMessageDialog(null, "Select Proper Path"); return; }

        Runnable runnable = () -> {
            try{
                progressBar.setProgress(0.1);


                Home.con = DriverManager.getConnection(Home.rootConnectionPath+dbName, Home.userName, Home.password);;
                DatabaseMetaData metadata = Home.con.getMetaData();
                ResultSet tableSet = metadata.getTables(null, null, null, new String[]{"TABLE"});

                JSONObject jsonObj = new JSONObject();

                progressBar.setProgress(0.3);

                ///*** READING TABLE NAMES
                while(tableSet.next()){
                    ////*** JSON ARRAY
                    JSONArray array = new JSONArray();

                    String tableName = tableSet.getString("TABLE_NAME");
                    Statement stmt = Home.con.createStatement();
                    ResultSet rowSet = stmt.executeQuery("select * from "+tableName);

                    ResultSetMetaData rowSetMetaData = rowSet.getMetaData();
                    int noOfColumn = rowSetMetaData.getColumnCount();

                    ///*** MOVE TABLE ROW
                    while(rowSet.next()){
                        ////**** MAP
                        Map map = new LinkedHashMap<>(noOfColumn);

                        for(byte columnNo = 1; columnNo <= noOfColumn; columnNo++){
                            String columnName = rowSetMetaData.getColumnName(columnNo);
                            Object data = rowSet.getObject(columnName);
                            ////**** PUT MAP DATA
                            map.put(columnName, data);
                        }
                        ////**** ADD JSON ARRAY
                        array.add(map);
                    }
                    ////**** ADD TABLE JSON OBJECT
                    jsonObj.put(tableName, array);
                }

                progressBar.setProgress(0.5);

                //////////********* SAVE IN FILE
                String dateStr = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss aa").format(new Date());
                File pathFile = new File(file.getParent()+"/"+dbName+"_"+dateStr+".json");
                DataOutputStream stream = new DataOutputStream(new FileOutputStream(pathFile));
                stream.write(jsonObj.toJSONString().getBytes());
                stream.flush();
                stream.close();

                progressBar.setProgress(1.0);

                ////**** UPDATE NEW FILE PATH
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        LBpath.setText(pathFile.getAbsolutePath());
                    }
                });

            }catch (Exception ee){
                ee.printStackTrace();
                JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
            }

        };
        new Thread(runnable).start();

    }

}
