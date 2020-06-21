package Controllers;

import BeanClass.ProductTransectionBean;
import BeanClass.UsersBean;
import Customs.Decode;
import Customs.Encode;
import Customs.Values;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import jdk.nashorn.internal.scripts.JO;
import sample.DatabaseImpt;

import javax.swing.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AdminPanel implements Initializable {


    @FXML private JFXTextField TFuid;
    @FXML private Button BTsetIp;
    @FXML private JFXTextField TFuserName;
    @FXML private JFXTextField TFpassword;
    @FXML private JFXComboBox<String> CBprivileges;
    @FXML private Button BTadd;
    @FXML private Button BTupdate;
    @FXML private Button BTdelete;
    @FXML private Button BTclear;
    @FXML private TableView<UsersBean> userTable;
    @FXML private TableColumn<UsersBean, Integer> userTableUid;
    @FXML private TableColumn<UsersBean, String> userTableName;
    @FXML private TableColumn<UsersBean, String> userTablePrivileges;

    private DatabaseImpt db;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            userTableUid.setCellValueFactory(new PropertyValueFactory<UsersBean, Integer>("userId"));
            userTableName.setCellValueFactory(new PropertyValueFactory<UsersBean, String>("userName"));
            userTablePrivileges.setCellValueFactory(new PropertyValueFactory<UsersBean, String>("privilege"));

            userTablePrivileges.setCellFactory(column -> new TableCell<UsersBean, String>(){
                @Override
                protected void updateItem(String item, boolean empty) {
                    if(!empty) setText(Decode.usersPrivileges(item));
                    else setText("");
                }
            });

            ////*** PRIVILEGES
            CBprivileges.setItems(FXCollections.observableArrayList(Arrays.asList(
                    Values.User.decodePrivilgeAdmin,
                    Values.User.decodePrivilgeTechnicalUser,
                    Values.User.decodePrivilgeComputerOperator,
                    Values.User.decodePrivilgeOtherUser
            )));

            BTupdate.setDisable(true);
            BTdelete.setDisable(true);
            db = new DatabaseImpt();
            getUsers();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    ////*** GET ALL USERS
    private void getUsers()throws  Exception{
        ArrayList<UsersBean> list = db.getUsers();
        userTable.setItems(FXCollections.observableArrayList(list));
    }

    ////*** ADD USERS
    @FXML private void add(){
        try {
            if(TFuserName.getText().trim().equals("")) JOptionPane.showMessageDialog(null, "Enter User Name");
            else if(TFpassword.getText().trim().equals("")) JOptionPane.showMessageDialog(null, "Enter password Name");
            else if(TFpassword.getText().trim().equals(TFuserName.getText().trim()))
                JOptionPane.showMessageDialog(null, "Invalid User Name != Password");
            else if(CBprivileges.getSelectionModel().getSelectedItem().equals("") | CBprivileges.getSelectionModel().getSelectedItem()==null)
                JOptionPane.showMessageDialog(null, "Select Privileges");
            else {

                UsersBean bean = new UsersBean(
                        TFuserName.getText().trim(),
                        TFpassword.getText().trim(),
                        Encode.usersPrivileges(CBprivileges.getSelectionModel().getSelectedItem())
                );
                db.insertUsers(bean);
                JOptionPane.showMessageDialog(null, "Inserted");
                getUsers();
                clear();
            }
        }catch (Exception ee){
            ee.printStackTrace();
            JOptionPane.showMessageDialog(null, ee.getMessage());
        }
    }

    ////*** UPDATE USERS
    @FXML private void update(){
        try {
            if(TFuserName.getText().trim().equals("")) JOptionPane.showMessageDialog(null, "Enter User Name");
            else if(TFpassword.getText().trim().equals("")) JOptionPane.showMessageDialog(null, "Enter password Name");
            else if(TFpassword.getText().trim().equals(TFuserName.getText().trim()))
                JOptionPane.showMessageDialog(null, "Invalid User Name != Password");
            else if(CBprivileges.getSelectionModel().getSelectedItem().equals("") | CBprivileges.getSelectionModel().getSelectedItem()==null)
                JOptionPane.showMessageDialog(null, "Select Privileges");
            else {
                UsersBean bean = new UsersBean(
                        Integer.parseInt(TFuid.getText().trim()),
                        TFuserName.getText().trim(),
                        TFpassword.getText().trim(),
                        Encode.usersPrivileges(CBprivileges.getSelectionModel().getSelectedItem())
                );
                db.updateUsers(bean);
                JOptionPane.showMessageDialog(null, "Updated");
                getUsers();
                clear();
            }
        }catch (Exception ee){
            ee.printStackTrace();
            JOptionPane.showMessageDialog(null, ee.getMessage());
        }
    }

    ////*** DELETE USERS
    @FXML private void delete(){
        try {
            db.deleteUsers(Integer.parseInt(TFuid.getText().trim()));
            JOptionPane.showMessageDialog(null, "Deleted");
            getUsers();
            clear();
        }catch (Exception ee){
            ee.printStackTrace();
            JOptionPane.showMessageDialog(null, ee.getMessage());
        }
    }

    ////*** USERS TABLE LISTENER
    @FXML private void tableListener(){
        try {
            UsersBean bean = userTable.getSelectionModel().getSelectedItem();
            if (bean == null) return;
                BTadd.setDisable(true);
                BTupdate.setDisable(false);
                BTdelete.setDisable(false);

                TFuid.setText(""+bean.getUserId());
                TFuserName.setText(bean.getUserName());
                TFpassword.setText(bean.getPassword());
                CBprivileges.getSelectionModel().select(Decode.usersPrivileges(bean.getPrivilege()));
        }catch (Exception ee){
            ee.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error "+ee.getMessage());
        }
    }

    ////*** CLEAR
    @FXML private void clear(){
        userTable.getSelectionModel().clearSelection();
        TFuid.setText("");
        TFuserName.setText("");
        TFpassword.setText("");
    }
}
