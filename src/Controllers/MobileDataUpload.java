package Controllers;

import Customs.Values;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Custom;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MobileDataUpload implements Initializable{

    @FXML private JFXComboBox<String> CBselectFile;
    @FXML private Button BTcreateFile;
    @FXML private ProgressBar progressBar;
    @FXML private Button BTuploadFile;
    @FXML private Label LBpath;
    @FXML private Button BTshowInFolder;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            getAllDatabaseName();
        }catch (Exception ee){
            ee.printStackTrace();
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!"+ee.getMessage());
        }
    }

    /////***** GET DATABASES NAMES
    private void getAllDatabaseName()throws Exception{
        CBselectFile.setItems(FXCollections.observableArrayList(Custom.getAllDatabaseName()));
    }

    /////***** CREATE FILE
    @FXML private void createFile(){
        try{
            ////**** GET PARENT PATH
            File file = selectPath();
            if(file == null){ JOptionPane.showMessageDialog(null, "Select Proper Path"); return; }
            String path = file.getParent();

            ///**** DATABASE CONNECTION
            String databaseName = CBselectFile.getSelectionModel().getSelectedItem();
            String databasePath = Home.rootConnectionPath+databaseName;
            java.sql.Connection conInit = DriverManager.getConnection(databasePath, Home.userName, Home.password);

            ////**** GET DATABASE TABLE NAMES
            ResultSet tableSet = conInit.getMetaData().getTables(null, null, null, new String[]{"TABLE"});

            ////**** CREATE BACKUP FOLDER
            String dateStr = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss aa").format(new Date());
            File backupFile = new File(path+"/"+databaseName+" "+dateStr);
            backupFile.mkdirs();

            ////**** TABLES CODE MAP
            Hashtable codeHash = Values.getTableMap();

            ////**** READ TABLE DATA ROW BY ROW
            while (tableSet.next()){
                String tableName = tableSet.getString("TABLE_NAME");
                String tableCode = codeHash.get(tableName).toString();

                ////**** CREATE TABLE FILE
                String outputFilePath = backupFile.getAbsolutePath()+"/"+tableCode+".csv";
                PrintStream printStream = new PrintStream(new FileOutputStream(outputFilePath));

                ////**** EXECUTE QUERY
                String query = "select * from "+tableName;
                PreparedStatement stmt = conInit.prepareStatement(query);
                ResultSet resultSet = stmt.executeQuery();

                ////**** NO: OF COLUMN IN TABLE
                byte noOfColumn = (byte) resultSet.getMetaData().getColumnCount();

                ////**** READ TABLE ROWS IN BUFFER
                StringBuffer buffer = new StringBuffer();
                while (resultSet.next()){
                    String separator = "";
                    for(byte col = 1; col <= noOfColumn; col++){
                        buffer.append(separator+resultSet.getObject(col));
                        separator = ",";
                    }
                    buffer.append("\n");
                }

                ////**** WRITE DATA IN BUFFER
                printStream.print(buffer.toString());

                ////**** CLOSE RESOURCES
                printStream.close();
                stmt.close();
                resultSet.close();
            }

            ////**** CLOSE RESOURCES
            tableSet.close();


            ///////////********* ZIP FILES *********///////////
            ////**** ZIP DESTINATION PATH
            String destinetionStr = path+"\\"+backupFile.getName()+".zip";

            ////**** FILE LIST
            String[] files = backupFile.list();

            ////**** RESOURCES
            FileOutputStream fos = new FileOutputStream(destinetionStr);
            ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));

            ////**** READ FILE LIST
            for(String filePath : files){
                File input = new File(backupFile.getAbsolutePath()+"/"+filePath);
                FileInputStream fis = new FileInputStream(input);
                ZipEntry ze = new ZipEntry(input.getName());
                System.out.println("Zipping the file: "+input.getName());
                zipOut.putNextEntry(ze);
                byte[] tmp = new byte[4*1024];
                int size = 0;
                while((size = fis.read(tmp)) != -1){
                    zipOut.write(tmp, 0, size);
                }
                ////**** CLOSE RESOURCES
                zipOut.flush();
                fis.close();
            }
            ////**** CLOSE RESOURCES
            zipOut.close();

            JOptionPane.showMessageDialog(null, "Done... Zipped the files...");
            ///////////********* ZIP FILES *********///////////

            ////**** DELETE ROW DATA
            deleteFolder(backupFile);

        }catch (Exception ee){
            ee.printStackTrace();
        }
    }

    ///////////********* DELETE FILES
    private void deleteFolder(File file){
        for (File subFile : file.listFiles()) {
            if(subFile.isDirectory()) {
                deleteFolder(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }



    private File selectPath(){
        Stage stage = ((Stage)(CBselectFile.getScene().getWindow()));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("untitle");
        return fileChooser.showSaveDialog(stage);
    }

    @FXML private void uploadFile(){

    }
}
