package Controllers;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class IpSetting implements Initializable {

    @FXML private JFXTextField TFip;
    @FXML private Button BTsetIp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    ////**** SELECT BY ENTER KEY
    @FXML private void KeyActionSetIp(KeyEvent event){
        if(event.getCode().equals(KeyCode.ENTER)) setIp();
    }
    @FXML private void setIp(){
        Home.rmiIP = TFip.getText().trim();
        ((Stage)(TFip.getScene().getWindow())).close();
    }
}
