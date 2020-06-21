package Controllers;

import BeanClass.CustomersBean;
import BeanClass.GodownBean;
import BeanClass.ProductsBean;
import BeanClass.TransectionBean;
import Customs.Values;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import sample.DatabaseImpt;

import javax.swing.JOptionPane;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClosingWork implements Initializable {

    @FXML private Label LBmsg;
    @FXML private ProgressBar progressBar;
    @FXML private Button BTstartTransfer;
    @FXML private Button BTcancel;
    @FXML private Button BTdone;

    private String oldName = "";
    private String newName = "";
    private DatabaseImpt oldDatabaseImpt;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            BTdone.setDisable(true);
        }catch (Exception ee) {
            JOptionPane.showMessageDialog(null, "Somethimg Wrong!" + ee.getMessage());
            ee.printStackTrace();
        }
    }

    /////***** START TRANSFER
    @FXML private void startTransferAction() {
        new AsynTask().start();
    }

    /////***** CLOSING PROCESS
    private class AsynTask extends Thread{
        @Override
        public void run() {
            try {
                BTcancel.setDisable(true);
                BTstartTransfer.setDisable(true);

                progressBar.setProgress(0.0);

                /////***** CREATE QUERY ACCORDING TO DB NAME *****/////
                String[] queries = Values.getDatabaseSturctureQueries(newName);

                java.sql.Connection rootCon = DriverManager.getConnection(Home.rootConnectionPath, Home.userName, Home.password);
                /////***** CREATE DATABASE STRUCTURE
                for (String query : queries) {
                    PreparedStatement stmt = rootCon.prepareStatement(query);
                    Integer row = stmt.executeUpdate();
                    if (stmt != null) stmt.close();
                }
                progressBar.setProgress(0.1);
                /////***** CREATE QUERY ACCORDING TO DB NAME *****/////


                ///*** RESOURSES
                PreparedStatement stmt = null;
                String query = "";


                /////***** INSERTING DATA IN TABLES *****/////
                /////***** CITY TABLE
                query = "INSERT INTO `"+newName+"`.`city`(`city_id`, `city_name`) SELECT `"+oldName+"`.`city`.`city_id`, `"+oldName+"`.`city`.`city_name` FROM `"+oldName+"`.`city`";
                stmt = rootCon.prepareStatement(query);
                stmt.executeUpdate();
                if (stmt != null) stmt.close();
                progressBar.setProgress(0.2);

                /////***** GODOWN TABLE
                query = "INSERT INTO `"+newName+"`.`godown`(`godown_id`, `godown_name`) SELECT `"+oldName+"`.`godown`.`godown_id`, `"+oldName+"`.`godown`.`godown_name` FROM `"+oldName+"`.`godown`";
                stmt = rootCon.prepareStatement(query);
                stmt.executeUpdate();
                if (stmt != null) stmt.close();
                progressBar.setProgress(0.25);

                /////***** UNIT TABLE
                query = "INSERT INTO `"+newName+"`.`unit`(`unit_id`, `unit_name`) SELECT `"+oldName+"`.`unit`.`unit_id`, `"+oldName+"`.`unit`.`unit_name` FROM `"+oldName+"`.`unit`";
                stmt = rootCon.prepareStatement(query);
                stmt.executeUpdate();
                if (stmt != null) stmt.close();
                progressBar.setProgress(0.3);

                /////***** CUSTOMER TABLE
                query = "INSERT INTO `"+newName+"`.`customers`(`customer_id`, `cust_name`, `firm_name`, `account_title`, `account_number`, `customer_type`, `city_id`, `address`) " +
                        "SELECT `"+oldName+"`.`customers`.`customer_id`, `"+oldName+"`.`customers`.`cust_name`, `"+oldName+"`.`customers`.`firm_name`, `"+oldName+"`.`customers`.`account_title`, " +
                        "`"+oldName+"`.`customers`.`account_number`, `"+oldName+"`.`customers`.`customer_type`, `"+oldName+"`.`customers`.`city_id`, `"+oldName+"`.`customers`.`address` " +
                        "FROM `"+oldName+"`.`customers`";
                stmt = rootCon.prepareStatement(query);
                stmt.executeUpdate();
                if (stmt != null) stmt.close();
                progressBar.setProgress(0.35);

                /////***** CONTACT TABLE
                query = "INSERT INTO `"+newName+"`.`contact`(`contact_id`, `customer_id`, `contact_name`, `phone_no`, `email_address`) SELECT `"+oldName+"`.`contact`.`contact_id`, " +
                        "`"+oldName+"`.`contact`.`customer_id`, `"+oldName+"`.`contact`.`contact_name`, `"+oldName+"`.`contact`.`phone_no`, `"+oldName+"`.`contact`.`email_address` " +
                        "FROM `"+oldName+"`.`contact`";
                stmt = rootCon.prepareStatement(query);
                stmt.executeUpdate();
                if (stmt != null) stmt.close();
                progressBar.setProgress(0.4);

                /////***** PRODUCT CATEGORY TABLE
                query = "INSERT INTO `"+newName+"`.`product_category`(`prod_cat_id`, `prod_cat_name`, `remarks`) SELECT `"+oldName+"`.`product_category`.`prod_cat_id`, " +
                        "`"+oldName+"`.`product_category`.`prod_cat_name`, `"+oldName+"`.`product_category`.`remarks` FROM `"+oldName+"`.`product_category`";
                stmt = rootCon.prepareStatement(query);
                stmt.executeUpdate();
                if (stmt != null) stmt.close();
                progressBar.setProgress(0.45);

                /////***** PRODUCTS TABLE
                query = "INSERT INTO `"+newName+"`.`products`(`product_id`, `prod_cat_id`, `prod_name`, `prod_brand_name`, `opening_rate`, `buying_rate`, `sale_rate`, " +
                        "`prod_create_date`, `last_prod_update`, `update_by`, `unit_id`, `remarks`) SELECT `"+oldName+"`.`products`.`product_id`, " +
                        "`"+oldName+"`.`products`.`prod_cat_id`, `"+oldName+"`.`products`.`prod_name`, `"+oldName+"`.`products`.`prod_brand_name`, `"+oldName+"`.`products`.`opening_rate`, " +
                        "`"+oldName+"`.`products`.`buying_rate`, `"+oldName+"`.`products`.`sale_rate`, `"+oldName+"`.`products`.`prod_create_date`, `"+oldName+"`.`products`.`last_prod_update`, " +
                        "`"+oldName+"`.`products`.`update_by`, `"+oldName+"`.`products`.`unit_id`, `"+oldName+"`.`products`.`remarks` FROM `"+oldName+"`.`products`";
                stmt = rootCon.prepareStatement(query);
                stmt.executeUpdate();
                if (stmt != null) stmt.close();
                /////***** INSERTING DATA IN TABLES *****/////

                progressBar.setProgress(0.5);

                ///*** OLD DATABASE CONNECTIVITY
                oldDatabaseImpt = new DatabaseImpt(DriverManager.getConnection(Home.rootConnectionPath+""+oldName, Home.userName, Home.password));


                /////***** INSERT CUSTOMERS OPEINING BALANCE *****/////

                /////***** SELECT EACH CUSTOMER INDIVIDUALLY
                ArrayList<CustomersBean> customersBeanArrayList = oldDatabaseImpt.getCustomer();
                for(CustomersBean customersBean : customersBeanArrayList){
                    /////***** + OPENING BALANCE
                    Float balance = Float.valueOf(0);
                    balance += customersBean.getOpeningBalance();

                    /////***** SELECT EACH TRANSECTION OF SELECTED CUSTOMER
                    ArrayList<TransectionBean> transectionBeanArrayList = oldDatabaseImpt.getTransectionByCustomerId(customersBean.getCustomerId());
                    for(TransectionBean transectionBean : transectionBeanArrayList){
                        String voucherType = transectionBean.getVoucherType();
                        if (voucherType.equals(Values.Transection.voucherTypeRegular))          //// HAS CREDIT BILL
                            balance += transectionBean.getTotalAmount();
                        else if (voucherType.equals(Values.Transection.voucherTypeReturn) |
                                voucherType.equals(Values.Transection.voucherTypeReceipt))      //// HAS DEBIT PAYMENT
                            balance -= transectionBean.getTotalAmount();
                    }

                    /////***** UPDATE CUSTOMERS OPENING BALANCE
                    query = "UPDATE `"+newName+"`.`customers` SET `"+newName+"`.`customers`.`opening_balance` = ? WHERE `"+newName+"`.`customers`.`customer_id` = ?";
                    stmt = rootCon.prepareStatement(query);
                    stmt.setFloat(1, balance);
                    stmt.setInt(2, customersBean.getCustomerId());
                    stmt.executeUpdate();
                    if (stmt != null) stmt.close();
                }
                /////***** INSERT CUSTOMERS OPEINING BALANCE *****/////

                progressBar.setProgress(0.6);

                /////***** INSERT PRODUCTS AVAILABLE QUANTITY *****/////
                /////***** GET ALL PRODUCTS
                ArrayList<ProductsBean> productsBeanArrayList = oldDatabaseImpt.getProducts();
                for(ProductsBean productsBean : productsBeanArrayList){
                    /////***** GET ALL GODOWNS
                    ArrayList<GodownBean> godownBeanArrayList = oldDatabaseImpt.getGodown();
                    for (GodownBean godownBean : godownBeanArrayList){
                        Integer transectionId = 0;

                        ///**** AVAILABLE QTY
                        Float availableQty = oldDatabaseImpt.getProductAvailableQuantityByGodownIdAndProductId(godownBean.getGodownId(), productsBean.getProductId());
                        if(availableQty == 0) continue;

                        ///**** AMOUNT
                        Float amount = availableQty * productsBean.getBuyingRate();

                        /////***** INSERT TRANSECTION OF AVAILABLE PRODUCT QTY -> GIN TRANSECTION
                        query = "INSERT INTO `"+newName+"`.`transection`(`trans_date`, `voucher_type`, `status`, `total_amount`) VALUES(?,?,?,?)";
                        stmt = rootCon.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                        stmt.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
                        stmt.setString(2, Values.Transection.voucherTypeGodownIn);
                        stmt.setString(3, Values.Transection.statusPending);
                        stmt.setFloat(4, amount);
                        stmt.executeUpdate();

                        ///*** GET GENERATED TID
                        ResultSet set = stmt.getGeneratedKeys();
                        if(set != null) if(set.next()) transectionId = set.getInt(1);

                        ////**** CLOSE RESOURSES
                        if (stmt != null) stmt.close();
                        if (set != null) set.close();

                        /////***** INSERT PRODUCT TRANSECTION -> FOR AVAILABLE QTY
                        query = "INSERT INTO `"+newName+"`.`product_transection`(`transection_id`, `product_id`, `godown_id`, `quantity`, `unit_price`, `amount`) VALUES(?,?,?,?,?,?)";
                        stmt = rootCon.prepareStatement(query);
                        stmt.setInt(1, transectionId);
                        stmt.setInt(2, productsBean.getProductId());
                        stmt.setInt(3, godownBean.getGodownId());
                        stmt.setFloat(4, availableQty);
                        stmt.setFloat(5, productsBean.getBuyingRate());
                        stmt.setFloat(6, amount);
                        stmt.executeUpdate();

                        ////**** CLOSE RESOURSES
                        if (stmt != null) stmt.close();
                        if (set != null) set.close();

                        /////***** INSERT PRODUCT OPENING ON HAND QUANTITY
                        query = "UPDATE `"+newName+"`.`products` SET `"+newName+"`.`products`.`opening_onhand_qty` = ? WHERE `"+newName+"`.`products`.`product_id` = ?";
                        stmt = rootCon.prepareStatement(query);
                        stmt.setFloat(1, availableQty);
                        stmt.setInt(2, productsBean.getProductId());
                        stmt.executeUpdate();

                        ////**** CLOSE RESOURSES
                        if (stmt != null) stmt.close();
                        if (set != null) set.close();
                    }
                }
                progressBar.setProgress(0.7);
                /////***** INSERT PRODUCTS AVAILABLE QUANTITY *****/////

                ////**** CLOSE CONNECTION
                if (rootCon != null) rootCon.close();

                /////***** CREATE IMAGE FOLDER *****/////
                ///*** CREATE NEW DB IMAGE DIR
                File newDBfile = new File("C:\\"+newName);
                if(!newDBfile.exists()) newDBfile.mkdir();

                progressBar.setProgress(0.8);

                ///*** OLD DB
                File oldDBfile = new File("C:\\"+oldName);

                ///*** GET ALL PARENT FOLDER
                String[] parentName = oldDBfile.list();
                for(String parent : parentName){
                    if(parent.equals(Values.Transection.tableName)) continue;
                    File parentFile = new File(oldDBfile.getAbsolutePath()+"\\"+parent);

                    ///*** GET ALL ID FOLDER
                    String[] idName = parentFile.list();
                    for(String id : idName){
                        File idFile = new File(parentFile.getAbsolutePath()+"\\"+id);

                        ///*** GET ALL IMAGE FOLDER
                        String[] imageName = idFile.list();
                        for(String image : imageName){
                            ///*** COPY FILE
                            DataInputStream imageFile = new DataInputStream(new FileInputStream(idFile.getAbsolutePath()+"\\"+image));
                            Integer fileSize = imageFile.available();
                            byte[] dataBytes = new byte[fileSize];
                            imageFile.readFully(dataBytes, 0, dataBytes.length);
                            imageFile.close();

                            ///*** NEW DATABASE PARENT FOLDER
                            File newParentFolder = new File(newDBfile.getAbsolutePath()+"\\"+parent);
                            if(!newParentFolder.exists()) newParentFolder.mkdir();

                            ///*** NEW DATABASE ID FOLDER
                            File newIdFolder = new File(newParentFolder.getAbsolutePath()+"\\"+id);
                            if(!newIdFolder.exists()) newIdFolder.mkdir();

                            ///*** PASTE IMAGE PATH
                            DataOutputStream pasteImageFile = new DataOutputStream(new FileOutputStream(newIdFolder.getAbsolutePath()+"\\"+image));
                            pasteImageFile.write(dataBytes, 0, dataBytes.length);
                            pasteImageFile.close();

                        } //** IMAGE FOLDER LOOP END
                    } //** ID FOLDER LOOP END
                } //** IMAGE PARENT LOOP END
                /////***** CREATE IMAGE FOLDER *****/////

                progressBar.setProgress(1.0);
                BTdone.setDisable(false);
            } catch (Exception ee) {
                JOptionPane.showMessageDialog(null, "Somethimg Wrong!" + ee.getMessage());
                ee.printStackTrace();
            }
        }
    }

    /////***** CANCEL
    @FXML private void cancelAction(){
        close();
    }

    /////***** DONE
    @FXML private void doneAction(){
        System.exit(0);
    }


    /////***** PUBLIC METHOD *****/////
    public void setName(String oldName, String newName){
        this.oldName = oldName;
        this.newName = newName;

        System.out.println("Old Name : "+oldName);
        System.out.println("New Name : "+newName);
    }
    /////***** PUBLIC METHOD *****/////


    /////***** CLOSE
    private void close(){
        ((Stage)(LBmsg.getScene().getWindow())).close();
    }

}
