package sample;

import BeanClass.*;
import Controllers.Home;
import Customs.Values;
import Remote.DatabaseInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;

public class DatabaseImpt extends UnicastRemoteObject implements DatabaseInterface {

    public DatabaseImpt() throws RemoteException {
    }

    public DatabaseImpt(Connection con) throws RemoteException {
        Home.con = con;
    }



    /////////////////************************* GENERAL METHODS ***********************************//////////////////
    private int executeUpdateQuery(String query) throws  SQLException, RemoteException{
        Statement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.createStatement();
            row = stmt.executeUpdate(query);

            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    /////////////////************************* GENERAL METHODS ***********************************//////////////////


    //////****** GET ROOT DIRECTORY PATH
    public String getRootDirPath()throws SQLException, RemoteException {
        return Home.imageRootDir;
    }


    ////////////////********************* IMAGES ********************************//////////////////
    //////****** Image Convert to Buffered Image
    private static BufferedImage convertToBufferedImage(Image image){
        BufferedImage newImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_BGR);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    /////////////////************************* INSERT/SAVE IMAGES IN DATABASE
    public void insertImage(String parentFolder, String id, String fileName, ImageIcon imageIcon)throws Exception{

        System.out.println("Image Path : "+id+"\\"+fileName+" LOAD!");

        //////****** CREATE IMAGE ROOT FOLDER
        File imageRootFolder = new File(Home.imageRootDir);
        if(!imageRootFolder.exists()) imageRootFolder.mkdir();

        //////****** CREATE ROOT PARENT FOLDER FOR ID
        File rootParentFolder = new File(imageRootFolder.getAbsolutePath()+"\\"+parentFolder);
        if(!rootParentFolder.exists()) rootParentFolder.mkdir();

        //////****** CREATE ID FOLDER FOR IMAGE
        File idFolder = new File(rootParentFolder.getAbsolutePath()+"\\"+id);
        if(!idFolder.exists()) idFolder.mkdir();

        //////****** CREATE IMAGE PATH
        File imageFile = new File(idFolder, fileName+".jpg");

        if(imageIcon == null){
            System.out.println("Image is NULL");
            return;
        }

        //////****** SAVE IMAGE
        BufferedImage bufferedImage = convertToBufferedImage(imageIcon.getImage());
        ImageIO.write(bufferedImage, "jpg", imageFile);

        System.out.println("Image Path : "+id+"\\"+fileName+"  SAVED!");
    }

    /////////////////************************* GET IMAGES FROM DATABASE
    public ArrayList getImage(String parentFolder, String id)throws Exception{

        System.out.println("LOAD Images at ID : "+id);
        System.out.println("Root Path : "+Home.imageRootDir);
        ArrayList<ImageIcon> byteArrayList = null;

        File file = new File(Home.imageRootDir+"\\"+parentFolder+"\\"+id);
        String[] addresses = file.list();
        if(addresses == null){
            System.out.println("This ID : "+id+" has no Images");
            return null;
        }

        byteArrayList = new ArrayList<>();

        for(byte fileNo = 0; fileNo < addresses.length; fileNo++){

            //////****** CREATE IMAGEICON FROM IMAGE FILE
            ImageIcon imageIcon = new ImageIcon(ImageIO.read(new FileInputStream(file.getAbsolutePath()+"\\"+addresses[fileNo])));
            //////****** ADD IMAGEICON IN ARRAYLIST
            byteArrayList.add(imageIcon);

            System.out.println("	Image path : "+addresses[fileNo]+" LOADED");
        }
        return byteArrayList;
    }
    ////////////////********************* IMAGES ********************************//////////////////


    /***
     *
     *      * USER
     *      * GODOWN
     *      * CITY
     *      * UNIT
     *      * CUSTOMER
     *      * CONTACT
     *      * PRODUCT CATEGORY
     *      * PRODUCTS
     *      * TRANSECTION
     *      * PRODUCT TRANSECTION
     *      * WEIGTH DETERMINATION
     *
     ***/





    /////////////////********************* USERS TABLE ********************************//////////////////

    /////////////////********************* GET USERS GENERAL METHOD
    private ArrayList getUsers_GeneralMethod(PreparedStatement stmt) throws  SQLException, RemoteException{
        ResultSet set = null;
        ArrayList<UsersBean> list = null;

        try{
            set = stmt.executeQuery();
            if(set != null){
                list = new ArrayList<>();

                while(set.next()){
                    UsersBean beans = new UsersBean();
                    beans.setUserId(set.getInt("user_id"));
                    beans.setUserName(set.getString("user_name"));
                    beans.setPassword(set.getString("password"));
                    beans.setPrivilege(set.getString("privilege"));
                    list.add(beans);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    /////////////////********************* GET USERS GENERAL METHOD

    /////////////////********************* GET USERS BEAN GENERAL METHOD
    private UsersBean getUsersBean_GeneralMethod(PreparedStatement stmt) throws  SQLException, RemoteException{
        ResultSet set = null;
        UsersBean beans = null;

        try{
            set = stmt.executeQuery();
            if(set != null){
                beans = new UsersBean();
                if(set.next()){
                    beans.setUserId(set.getInt("user_id"));
                    beans.setUserName(set.getString("user_name"));
                    beans.setPassword(set.getString("password"));
                    beans.setPrivilege(set.getString("privilege"));
                }
            }
            return beans;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    /////////////////********************* GET USERS BEAN GENERAL METHOD

    ////////////////********************* INSERT
    public int insertUsers(UsersBean bean)throws SQLException, RemoteException{
        String query = "insert into users(user_name, password, privilege) values(?, ?, ?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, bean.getUserName());
            stmt.setString(2, bean.getPassword());
            stmt.setString(3, bean.getPrivilege());
            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT

    ////////////////********************* UPDATE
    public int updateUsers(UsersBean bean)throws SQLException, RemoteException{
        String query = "update users set user_name = ?, password = ?, privilege = ? where user_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, bean.getUserName());
            stmt.setString(2, bean.getPassword());
            stmt.setString(3, bean.getPrivilege());
            stmt.setInt(4, bean.getUserId());
            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE

    ////////////////********************* DELETE
    public int deleteUsers(Integer userId)throws SQLException, RemoteException{
        String query = "delete from users where user_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, userId);
            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* DELETE

    ////////////////********************* GET ALL
    public ArrayList getUsers()throws SQLException, RemoteException{
        String query = "select * from users";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        return getUsers_GeneralMethod(stmt);
    }
    ////////////////********************* GET ALL

    ////////////////********************* BEAN
    ////////////////********************* GET BEAN BY ID
    public UsersBean getUsersBean(int userId)throws SQLException, RemoteException{
        String query = "select * from users where user_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, userId);
        return getUsersBean_GeneralMethod(stmt);
    }
    ////////////////********************* GET BEAN BY ID



    ////////////////********************* SINGLE VALUE
    ////////////////********************* GET USER NAME BY ID
    public String getUserNameById(Integer userId)throws SQLException, RemoteException{
        String query = "select user_name from users where user_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        String name = "";
        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, userId);
            set = stmt.executeQuery();
            if (set.next()) name = set.getString("user_name");
            return name;
        }finally {
            if(stmt != null) stmt.close();
            if(set != null) set.close();
        }
    }
    ////////////////********************* GET USER NAME BY ID

    ////////////////********************* GET USER ID
    public int getUserId(String userName, String password)throws SQLException, RemoteException{
        String query = "select user_id from users where user_name = BINARY ? and password = BINARY ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;
        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, userName);
            stmt.setString(2, password);
            set = stmt.executeQuery();
            if (set.next()) id = set.getInt("user_id");
            return id;
        }finally {
            if(stmt != null) stmt.close();
            if(set != null) set.close();
        }
    }
    ////////////////********************* GET USER ID

    ////////////////********************* IS USER VALID
    public boolean isUserValid(String userName, String password)throws SQLException, RemoteException{
        String query = "select * from users where user_name = BINARY ? and password = BINARY ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setString(1, userName);
        stmt.setString(2, password);
        if(stmt.executeQuery().next()) return true;
        return false;
    }
    ////////////////********************* IS USER VALID

    ////////////////********************* IS ADMIN USER
    public boolean isAdminUser(Integer userId)throws SQLException, RemoteException{
        String query = "select * from users where user_id =  ? and privilege = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, userId);
        stmt.setString(2, Values.User.privilgeAdmin);
        if(stmt.executeQuery().next()) return true;
        return false;
    }
    ////////////////********************* IS ADMIN USER

    /////////////////********************* USERS TABLE ********************************//////////////////





    /////////////////********************* GODOWN TABLE ********************************//////////////////

    /////////////////********************* GET GODOWN GENERAL METHOD
    private ArrayList getGodown_GeneralMethod(String query) throws  SQLException, RemoteException{
        Statement stmt = null;
        ResultSet set = null;
        ArrayList<GodownBean> list = null;

        try{
            stmt = Home.con.createStatement();
            set = stmt.executeQuery(query);

            if(set != null){
                list = new ArrayList<>();

                while(set.next()){
                    GodownBean beans = new GodownBean();
                    beans.setGodownId(set.getInt("godown_id"));
                    beans.setGodownName(set.getString("godown_name"));
                    list.add(beans);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    /////////////////********************* GET GODOWN GENERAL METHOD

    ////////////////********************* INSERT
    public int insertGodown(String godownName)throws SQLException, RemoteException{

        String query = "insert into godown(godown_name) values(?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, godownName);
            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT

    ////////////////********************* UPDATE
    public int updateGodown(GodownBean bean)throws SQLException, RemoteException{

        String query = "update godown set godown_name = ? where godown_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, bean.getGodownName());
            stmt.setInt(2, bean.getGodownId());
            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE

    ////////////////********************* GET ALL
    public ArrayList getGodown()throws SQLException, RemoteException{
        String query = "SELECT * FROM `godown`";
        System.out.println(query);
        return getGodown_GeneralMethod(query);
    }
    ////////////////********************* GET ALL

    ////////////////********************* GET BY GODOWN ID
    public ArrayList getGodownByGodownId(Integer godownId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `godown` where godown_id = "+godownId;
        System.out.println(query);
        return getGodown_GeneralMethod(query);
    }
    ////////////////********************* GET BY GODOWN ID

    ////////////////********************* GET BY GODOWN NAME
    public ArrayList getGodownByGodownName(String godownName)throws SQLException, RemoteException{
        String query = "SELECT * FROM `godown` where godown_name like '"+godownName+"%'";
        System.out.println(query);
        return getGodown_GeneralMethod(query);
    }
    ////////////////********************* GET BY GODOWN NAME

    /////////////////********************* GET GODOWN BEAN GENERAL METHOD
    private GodownBean getGodownBean_GeneralMethod(String query) throws  SQLException, RemoteException{
        Statement stmt = null;
        ResultSet set = null;
        GodownBean bean = null;

        try{
            stmt = Home.con.createStatement();
            set = stmt.executeQuery(query);

            if(set != null){
                bean = new GodownBean();
                if(set.next()){
                    bean.setGodownId(set.getInt("godown_id"));
                    bean.setGodownName(set.getString("godown_name"));
                }
            }
            return bean;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    /////////////////********************* GET GODOWN BEAN GENERAL METHOD

    ////////////////********************* GET BEAN BY GODOWN ID
    public GodownBean getGodownBeanByGodownId(Integer godownId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `godown` where godown_id = "+godownId;
        System.out.println(query);
        return getGodownBean_GeneralMethod(query);
    }
    ////////////////********************* GET BEAN BY GODOWN ID

    /////////////////********************* GODOWN TABLE ********************************//////////////////







    /////////////////********************* CITY TABLE ********************************//////////////////

    /////////////////********************* GET CITY GENERAL METHOD
    private ArrayList getCity_GeneralMethod(String query) throws  SQLException, RemoteException{
        Statement stmt = null;
        ResultSet set = null;
        ArrayList<CityBean> list = null;

        try{
            stmt = Home.con.createStatement();
            set = stmt.executeQuery(query);

            if(set != null){
                list = new ArrayList<>();

                while(set.next()){
                    CityBean beans = new CityBean();
                    beans.setCityId(set.getInt("city_id"));
                    beans.setCityName(set.getString("city_name"));
                    list.add(beans);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }

    /////////////////********************* GET CITY GENERAL METHOD

    /////////////////********************* GET CITY BEAN GENERAL METHOD
    private CityBean getCityBean(String query) throws SQLException, RemoteException{
        Statement stmt = null;
        ResultSet set = null;
        CityBean bean = null;

        try{
            stmt = Home.con.createStatement();
            set = stmt.executeQuery(query);

            if(set != null){
                if(set.next()){
                    bean = new CityBean();
                    bean.setCityId(set.getInt("city_id"));
                    bean.setCityName(set.getString("city_name"));
                }
            }
            return bean;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    /////////////////********************* GET CITY BEAN GENERAL METHOD

    /////////////////********************* INSERT
    public int insertCity(String cityName)throws SQLException, RemoteException{
        String query = "INSERT INTO CITY(city_name) VALUES('"+cityName+"')";
        System.out.println(query);
        return executeUpdateQuery(query);
    }
    /////////////////********************* INSERT

    /////////////////********************* INSERT GET GENARATED ID
    public int insertCityGetId(String cityName)throws SQLException, RemoteException{
        String query = "INSERT INTO CITY(city_name) VALUES(?)";
        PreparedStatement stmt =  null;
        ResultSet set = null;
        Integer id = 0;
        try{
            stmt = Home.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, cityName);

            stmt.executeUpdate();

            set = stmt.getGeneratedKeys();
            if(set != null)
                if(set.next())
                    id = set.getInt(1);

        }finally {
            stmt.close();
            set.close();
        }
        return id;
    }
    /////////////////********************* INSERT GET GENARATED ID

    /////////////////********************* UPDATE
    public  int updateCity(int cityId, String cityName)throws  SQLException, RemoteException{
        String query = "UPDATE CITY SET city_name = '"+cityName+"' WHERE city_id = "+cityId;
        System.out.println(query);
        return executeUpdateQuery(query);
    }
    /////////////////********************* UPDATE

    /////////////////********************* DELETE
    public  int deleteCity(int cityId)throws  SQLException, RemoteException{
        ////////********* RESTRICTION ONLY ADMIN CAN DELETE
        String query = "DELETE FROM CITY WHERE city_id = "+cityId;
        System.out.println(query);
        return executeUpdateQuery(query);
    }
    /////////////////********************* DELETE

    /////////////////********************* GET
    public ArrayList getCity()throws  SQLException, RemoteException{
        String query = "SELECT * FROM CITY ORDER BY city_name";
        System.out.println(query);
        return getCity_GeneralMethod(query);
    }
    /////////////////********************* GET

    /////////////////********************* OVERIDE GET BEAN BY ID
    public CityBean getCity(int cityId)throws  SQLException, RemoteException{
        String query = "SELECT * FROM CITY WHERE city_id = "+cityId;
        System.out.println(query);
        return getCityBean(query);
    }
    /////////////////********************* OVERIDE GET BEAN BY ID

    /////////////////********************* OVERIDE GET BEAN BY CITY NAME
    public CityBean getCity(String cityName)throws  SQLException, RemoteException{
        String query = "SELECT * FROM CITY WHERE city_name = '"+cityName+"'";
        System.out.println(query);
        return getCityBean(query);
    }
    /////////////////********************* OVERIDE GET BY CITY NAME

    /////////////////********************* SINGLE VALUE
    /////////////////********************* GET CITY NAME BY ID
    public Integer getCityIdByCityName(String cityName)throws  SQLException, RemoteException{
        String query = "SELECT city_id FROM CITY WHERE city_name = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, cityName);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) id = set.getInt("city_id");
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
        return id;
    }
    /////////////////********************* GET CITY NAME BY ID

    /////////////////********************* CITY TABLE ********************************//////////////////








    /////////////////********************* UNIT TABLE ********************************//////////////////

    /////////////////********************* GET UNIT GENERAL METHOD
    private ArrayList getUnit_GeneralMethod(PreparedStatement stmt) throws  SQLException, RemoteException{
        ResultSet set = null;
        ArrayList<UnitBean> list = null;

        try{
            set = stmt.executeQuery();
            if(set != null){
                list = new ArrayList<>();

                while(set.next()){
                    UnitBean bean = new UnitBean();
                    bean.setUnitId(set.getInt("unit_id"));
                    bean.setUnitName(set.getString("unit_name"));
                    list.add(bean);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    /////////////////********************* GET UNIT GENERAL METHOD

    /////////////////********************* GET UNIT BEAN GENERAL METHOD
    private UnitBean getUnitBean_GeneralMethod(PreparedStatement stmt) throws  SQLException, RemoteException{
        ResultSet set = null;
        UnitBean bean = null;

        try{
            set = stmt.executeQuery();
            if(set != null){
                bean = new UnitBean();

                if(set.next()){
                    bean.setUnitId(set.getInt("unit_id"));
                    bean.setUnitName(set.getString("unit_name"));
                }
            }
            return bean;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    /////////////////********************* GET UNIT BEAN GENERAL METHOD

    ////////////////********************* INSERT
    public int insertUnit(String unitName)throws SQLException, RemoteException{
        String query = "insert into unit(unit_name) values(?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, unitName);
            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT

    ////////////////********************* UPDATE
    public int updateUnit(UnitBean bean)throws SQLException, RemoteException{
        String query = "update unit set unit_name = ? where unit_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, bean.getUnitName());
            stmt.setInt(2, bean.getUnitId());
            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE

    ////////////////********************* GET ALL
    public ArrayList getUnit()throws SQLException, RemoteException{
        String query = "SELECT * FROM `unit`";
        System.out.println(query);
        PreparedStatement stmt = Home.con.prepareStatement(query);
        return getUnit_GeneralMethod(stmt);
    }
    ////////////////********************* GET ALL

    ////////////////********************* BEAN
    ////////////////********************* GET BY UNIT NAME
    public UnitBean getUnitBeanByName(String unitName)throws SQLException, RemoteException{
        String query = "SELECT * FROM `unit` where unit_name = ?";
        System.out.println(query);
        PreparedStatement stmt = Home.con.prepareStatement(query);
        stmt.setString(1, unitName);
        return getUnitBean_GeneralMethod(stmt);
    }
    ////////////////********************* GET BY UNIT ID

    ////////////////********************* GET BY UNIT NAME
    public UnitBean getUnitBeanById(int unitId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `unit` where unit_id = ?";
        System.out.println(query);
        PreparedStatement stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, unitId);
        return getUnitBean_GeneralMethod(stmt);
    }
    ////////////////********************* GET BY UNIT ID



    ////////////////********************* SINGLE VALUE
    /////////////////********************* GET UNIT ID BY NAME
    public Integer getUnitIdByName(String unitName) throws  SQLException, RemoteException{
        String query = "SELECT unit_id FROM `unit` WHERE `unit_name` = ?";
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;

        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, unitName);
            set = stmt.executeQuery();

            if(set != null)
                if(set.next()) id = set.getInt("unit_id");

        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
        return id;
    }
    /////////////////********************* GET PRODUCT CATEGORY ID BY CAT NAME

    ////////////////********************* GET UNIT AS STRING
    public ArrayList<String> getUnitAsString() throws  SQLException, RemoteException{
        String query = "SELECT * FROM `unit`";
        PreparedStatement stmt = null;
        ResultSet set = null;
        ArrayList<String> list = null;

        try{
            stmt = Home.con.prepareStatement(query);
            set = stmt.executeQuery();

            if(set != null){
                list = new ArrayList<String>();
                while (set.next()) list.add(set.getString("unit_name"));
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    /////////////////********************* GET UNIT AS STRING

    /////////////////********************* UNIT TABLE ********************************//////////////////








    ////////////////********************* CUSTOMERS METHODS ********************************//////////////////
    ////////////////********************* DEPRECATED SOON.......
    ////////////////********************* GET CUSTOMER GENERAL METHOD
    private ArrayList getCustomer_GeneralMethod_Filter(String query)throws SQLException, RemoteException{
        PreparedStatement stmt = null;
        ResultSet set = null;
        ArrayList<CustomersBean> list = null;

        try{
            stmt = Home.con.prepareStatement(query);
            set = stmt.executeQuery(query);

            if(set != null){
                list = new ArrayList<>();
                while(set.next()){
                    CustomersBean bean = new CustomersBean();
                    bean.setCustomerId(set.getInt("customer_id"));
                    bean.setCustName(set.getString("cust_name"));
                    bean.setFirmName(set.getString("firm_name"));
                    bean.setAccountTitle(set.getString("account_title"));
                    bean.setAccountNumber(set.getString("account_number"));
                    bean.setCustomerType(set.getString("customer_type"));
                    bean.setCityId(set.getInt("city_id"));
                    bean.setAddress(set.getString("address"));
                    bean.setOpeningBalance(set.getFloat("opening_balance"));
                    list.add(bean);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET CUSTOMER GENERAL METHOD

    ////////////////********************* PREPARED STATEMENT
    ////////////////********************* GET CUSTOMER GENERAL METHOD
    private ArrayList getCustomer_GeneralMethod(PreparedStatement stmt)throws SQLException, RemoteException{
        ResultSet set = null;
        ArrayList<CustomersBean> list = null;

        try{
            set = stmt.executeQuery();

            if(set != null){
                list = new ArrayList<>();
                while(set.next()){
                    CustomersBean bean = new CustomersBean();
                    bean.setCustomerId(set.getInt("customer_id"));
                    bean.setCustName(set.getString("cust_name"));
                    bean.setFirmName(set.getString("firm_name"));
                    bean.setAccountTitle(set.getString("account_title"));
                    bean.setAccountNumber(set.getString("account_number"));
                    bean.setCustomerType(set.getString("customer_type"));
                    bean.setCityId(set.getInt("city_id"));
                    bean.setAddress(set.getString("address"));
                    bean.setOpeningBalance(set.getFloat("opening_balance"));
                    list.add(bean);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET CUSTOMER GENERAL METHOD


    ////////////////********************* GET CUSTOMER BEAN GENERAL METHOD
    private CustomersBean getCustomerBean_GeneralMethod(PreparedStatement stmt)throws SQLException, RemoteException{
        ResultSet set = null;
        CustomersBean bean = null;

        try{
            set = stmt.executeQuery();

            if(set != null){
                bean = new CustomersBean();
                if(set.next()){
                    bean.setCustomerId(set.getInt("customer_id"));
                    bean.setCustName(set.getString("cust_name"));
                    bean.setFirmName(set.getString("firm_name"));
                    bean.setAccountTitle(set.getString("account_title"));
                    bean.setAccountNumber(set.getString("account_number"));
                    bean.setCustomerType(set.getString("customer_type"));
                    bean.setCityId(set.getInt("city_id"));
                    bean.setAddress(set.getString("address"));
                    bean.setOpeningBalance(set.getFloat("opening_balance"));
                }
            }
            return bean;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET CUSTOMER BEAN GENERAL METHOD

    ////////////////********************* INSERT
    public int insertCustomer(CustomersBean bean)throws SQLException, RemoteException{

        String query = "insert into customers(cust_name, firm_name, account_title, account_number, customer_type, city_id, address) values(?,?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, bean.getCustName());
            stmt.setString(2, bean.getFirmName());
            stmt.setString(3, bean.getAccountTitle());
            stmt.setString(4, bean.getAccountNumber());
            stmt.setString(5, bean.getCustomerType());
            stmt.setInt(6, bean.getCityId());
            stmt.setString(7, bean.getAddress());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT

    ////////////////********************* INSERT GET GENERATED ID
    public int insertCustomerGetId(CustomersBean bean)throws SQLException, RemoteException{
        String query = "insert into customers(cust_name, firm_name, account_title, account_number, customer_type, city_id, address) values(?,?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;
        try{
            stmt = Home.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, bean.getCustName());
            stmt.setString(2, bean.getFirmName());
            stmt.setString(3, bean.getAccountTitle());
            stmt.setString(4, bean.getAccountNumber());
            stmt.setString(5, bean.getCustomerType());
            stmt.setInt(6, bean.getCityId());
            stmt.setString(7, bean.getAddress());

            stmt.executeUpdate();
            set = stmt.getGeneratedKeys();
            if(set != null)
                if(set.next())
                    id = set.getInt(1);
        }
        finally{
            if(stmt != null) stmt.close();
            if(set != null) set.close();
        }
        return id;
    }
    ////////////////********************* INSERT GET GENERATED ID


    ////////////////********************* UPDATE
    public int updateCustomer(CustomersBean bean)throws SQLException, RemoteException{
        String query = "update customers set cust_name = ?, firm_name = ?, account_title = ?, account_number = ?, customer_type = ?, city_id = ?, address = ? where customer_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, bean.getCustName());
            stmt.setString(2, bean.getFirmName());
            stmt.setString(3, bean.getAccountTitle());
            stmt.setString(4, bean.getAccountNumber());
            stmt.setString(5, bean.getCustomerType());
            stmt.setInt(6, bean.getCityId());
            stmt.setString(7, bean.getAddress());
            stmt.setInt(8, bean.getCustomerId());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE

    ////////////////********************* UPDATE OPENING BALANCE
    public int updateCustomerOpeningBalance(Integer customerId, Float openingBalance)throws SQLException, RemoteException{
        String query = "update customers set opening_balance = ? where customer_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setFloat(1, openingBalance);
            stmt.setInt(2, customerId);

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE OPENING BALANCE

    ////////////////********************* GET ALL
    public ArrayList getCustomer()throws SQLException, RemoteException{
        String query = "SELECT * FROM `customers`";
        System.out.println(query);
        PreparedStatement stmt = Home.con.prepareStatement(query);
        return getCustomer_GeneralMethod(stmt);
    }
    ////////////////********************* GET ALL

    ////////////////********************* GET BY CUSTOMER TYPE
    public ArrayList getCustomerByCustomerType(String customerType)throws SQLException, RemoteException{
        String query = "SELECT * FROM `customers` WHERE `customer_type`= ?";
        System.out.println(query);
        PreparedStatement stmt = Home.con.prepareStatement(query);
        stmt.setString(1, customerType);
        return getCustomer_GeneralMethod(stmt);
    }
    ////////////////********************* GET BY CUSTOMER TYPE

    ////////////////********************* GET BY CUSTOMER ID
    public ArrayList getCustomerByCustomerId(Integer customerId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `customers` WHERE `customer_id`= ?";
        System.out.println(query);
        PreparedStatement stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, customerId);
        return getCustomer_GeneralMethod(stmt);
    }
    ////////////////********************* GET BY CUSTOMER ID

    ////////////////********************* GET BY CUSTOMER NAME & TYPE
    public ArrayList getCustomerByFirmNameAndType(String firmName, String customerType)throws SQLException, RemoteException{
        String query = "SELECT * FROM `customers` WHERE `firm_name` like '"+firmName+"%' and customer_type = '"+customerType+"'";
        System.out.println(query);
        return getCustomer_GeneralMethod_Filter(query);
    }
    ////////////////********************* GET BY CUSTOMER NAME & TYPE

    ////////////////********************* GET BY CITY NAME
    public ArrayList getCustomerByCityName(String cityName)throws SQLException, RemoteException{
        String query = "SELECT * FROM `customers` WHERE " +
                "`city_id` LIKE (SELECT `city_id` FROM `city` WHERE `city_name` LIKE '"+cityName+"%')";
        System.out.println(query);
        return getCustomer_GeneralMethod_Filter(query);
    }
    ////////////////********************* GET BY CITY NAME

    ////////////////********************* GET BY CITY NAME & TYPE
    public ArrayList getCustomerByCityNameAndType(String cityName , String customerType)throws SQLException, RemoteException{
        String query = "SELECT * FROM `customers` WHERE `customer_type` = '"+customerType+"' AND " +
                "`city_id` LIKE (SELECT `city_id` FROM `city` WHERE `city_name` LIKE '"+cityName+"%')";
        System.out.println(query);
        return getCustomer_GeneralMethod_Filter(query);
    }
    ////////////////********************* GET BY CITY NAME & TYPE

    ////////////////********************* FILTER SEARCH
    public ArrayList getCustomerByFilterSearch(CustomersBean bean)throws SQLException, RemoteException{
        String query = "SELECT * FROM `customers` WHERE ";
        Integer querySize = query.length();

        ///*** CUSTOMER ID
        if(bean.getCustomerId() > 0) {
            query += "customer_id = "+bean.getCustomerId()+" ";
        }

        ///*** CUSTOMER NAME
        if(!bean.getCustName().equals("")){
            if(query.length() != querySize){ query += "AND ";  querySize = query.length();}
            query += "cust_name LIKE '"+bean.getCustName()+"%' ";
        }

        ///*** FIRM NAME
        if(!bean.getFirmName().equals("")){
            if(query.length() != querySize){ query += "AND ";  querySize = query.length();}
            query += "firm_name LIKE '"+bean.getFirmName()+"%' ";
        }

        ///*** ACCOUNT TITLE
        if(!bean.getAccountTitle().equals("")){
            if(query.length() != querySize){ query += "AND ";  querySize = query.length();}
            query += "account_title LIKE '"+bean.getAccountTitle()+"' ";
        }

        ///*** ACCOUNT NUMBER
        if(!bean.getAccountNumber().equals("")){
            if(query.length() != querySize){ query += "AND ";  querySize = query.length();}
            query += "account_number LIKE '"+bean.getAccountNumber()+"' ";
        }

        ///*** CITY ID
        if(bean.getCityId() > 0){
            if(query.length() != querySize){ query += "AND ";  querySize = query.length();}
            query += "city_id = "+bean.getCityId()+" ";
        }

        ///*** CUSTOMER TYPE
        if(!bean.getCustomerType().equals("")) {
            if (query.length() != querySize) { query += "AND "; querySize = query.length(); }
            query += "customer_type = '" + bean.getCustomerType() + "'";
        }
        System.out.println(query);
        return getCustomer_GeneralMethod_Filter(query);
    }
    ////////////////********************* FILTER SEARCH

    ////////////////********************* BEAN
    ////////////////********************* GET CUSTOMER BEAN BY CUSTOMER ID
    public CustomersBean getCustomerBeanByCustomerId(Integer customerId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `customers` WHERE `customer_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, customerId);
        return getCustomerBean_GeneralMethod(stmt);
    }
    ////////////////********************* GET CUSTOMER BEAN BY CUSTOMER ID

    ////////////////********************* SINGLE VALUE
    ////////////////********************* GET CUSTOMER TYPE BY TRANSECTION ID
    public String getCustomerTypeByTransectionId(Integer transectionId) throws SQLException, RemoteException{
        String query = "SELECT customer_type FROM `customers` WHERE `customer_id` LIKE (SELECT `customer_id` FROM `transection` WHERE `transection_id` =  ?) ";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, transectionId);

        try {
            set = stmt.executeQuery();
            if(set != null) if(set.next()) return set.getString("customer_type");
            return null;
        }finally {
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET CUSTOMER TYPE BY TRANSECTION ID

    ////////////////********************* JOINING
    ////////////////********************* GET CUSTOMER/SUPPLIER BY CUSTOMER TYPE & PRODUCT ID
    public ArrayList getCustomerJoiningByCustomerTypeAndProductId(String customerType, Integer productId)throws SQLException, RemoteException{
        String query = "SELECT DISTINCT `customers`.* FROM `customers`, `transection`, `product_transection`, `products` " +
                "WHERE `customers`.`customer_id` = `transection`.`customer_id` AND " +
                "`transection`.`transection_id` = `product_transection`.`transection_id` AND " +
                "`products`.`product_id` = `product_transection`.`product_id` AND " +
                "`customers`.`customer_type` = ? AND " +
                "`products`.`product_id` = ? ";

        System.out.println(query);
        PreparedStatement stmt = Home.con.prepareStatement(query);
        stmt.setString(1, customerType);
        stmt.setInt(2, productId);
        return getCustomer_GeneralMethod(stmt);
    }

    ////////////////********************* CUSTOMERS METHODS ********************************//////////////////






    ////////////////********************* CONTACT METHODS ********************************//////////////////

    ////////////////********************* GET CONTACT GENERAL METHOD
    private ArrayList getContact_GeneralMethod(String query)throws SQLException, RemoteException{
        PreparedStatement stmt = null;
        ResultSet set = null;
        ArrayList<ContactBean> list = null;

        try{
            stmt = Home.con.prepareStatement(query);
            set = stmt.executeQuery(query);

            if(set != null){
                list = new ArrayList<>();
                while(set.next()){
                    ContactBean bean = new ContactBean();
                    bean.setContactId(set.getInt("contact_id"));
                    bean.setCustomerId(set.getInt("customer_id"));
                    bean.setContactName(set.getString("contact_name"));
                    bean.setPhoneNo(set.getString("phone_no"));
                    bean.setEmailAddress(set.getString("email_address"));
                    list.add(bean);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET CONTACT GENERAL METHOD

    ////////////////********************* INSERT
    public int insertContact(ContactBean bean)throws SQLException, RemoteException{
        String query = "insert into contact(customer_id, contact_name, phone_no, email_address) values(?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, bean.getCustomerId());
            stmt.setString(2, bean.getContactName());
            stmt.setString(3, bean.getPhoneNo());
            stmt.setString(4, bean.getEmailAddress());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT

    ////////////////********************* UPDATE
    public int updateContact(ContactBean bean)throws SQLException, RemoteException{
        String query = "update contact set customer_id = ?, contact_name = ?, phone_no = ?, email_address = ? where contact_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, bean.getCustomerId());
            stmt.setString(2, bean.getContactName());
            stmt.setString(3, bean.getPhoneNo());
            stmt.setString(4, bean.getEmailAddress());
            stmt.setInt(5, bean.getContactId());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE

    ////////////////********************* GET BY CONTACT TYPE
    public ArrayList getContactByCustomerId(int customerId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `contact` WHERE `customer_id`="+customerId;
        System.out.println(query);
        return getContact_GeneralMethod(query);
    }
    ////////////////********************* GET BY CONTACT TYPE

    ////////////////********************* CONTACT METHODS ********************************//////////////////






    ////////////////********************* PRODUCT CATEGORY METHODS ********************************//////////////////

    ////////////////********************* GET PRODUCT CATEGORY GENERAL METHOD
    private ArrayList getProductCategory_GeneralMethod(String query)throws SQLException, RemoteException{
        PreparedStatement stmt = null;
        ResultSet set = null;
        ArrayList<ProductCategoryBean> list = null;

        try{
            stmt = Home.con.prepareStatement(query);
            set = stmt.executeQuery(query);

            if(set != null){
                list = new ArrayList<>();
                while(set.next()){
                    ProductCategoryBean bean = new ProductCategoryBean();
                    bean.setProdCatId(set.getInt("prod_cat_id"));
                    bean.setProdCatName(set.getString("prod_cat_name"));
                    bean.setRemarks(set.getString("remarks"));
                    list.add(bean);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT CATEGORY GENERAL METHOD

    ////////////////********************* INSERT
    public int insertProductCategory(ProductCategoryBean bean)throws SQLException, RemoteException{
        String query = "insert into product_category(prod_cat_name, remarks) values(?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, bean.getProdCatName());
            stmt.setString(2, bean.getRemarks());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT

    ////////////////********************* UPDATE
    public int updateProductCategory(ProductCategoryBean bean)throws SQLException, RemoteException{
        String query = "update product_category set prod_cat_name = ?, remarks = ? where prod_cat_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, bean.getProdCatName());
            stmt.setString(2, bean.getRemarks());
            stmt.setInt(3, bean.getProdCatId());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE

    ////////////////********************* GET ALL
    public ArrayList getProductCategory()throws SQLException, RemoteException{
        String query = "SELECT * FROM `product_category`";
        System.out.println(query);
        return getProductCategory_GeneralMethod(query);
    }
    ////////////////********************* GET ALL

    ////////////////********************* GET BY PROD CAT ID
    public ArrayList getProductCategoryByProdCatId(Integer prodCatId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `product_category` WHERE prod_cat_id = "+prodCatId;
        System.out.println(query);
        return getProductCategory_GeneralMethod(query);
    }
    ////////////////********************* GET BY PROD CAT ID

    ////////////////********************* GET BY PROD CAT NAME
    public ArrayList getProductCategoryByProdCatName(String prodCatName)throws SQLException, RemoteException{
        String query = "SELECT * FROM `product_category` WHERE prod_cat_name LIKE '"+prodCatName+"%'";
        System.out.println(query);
        return getProductCategory_GeneralMethod(query);
    }
    ////////////////********************* GET BY PROD CAT NAME


    /////////////////********************* GET PRODUCT CATEGORY AS STRING
    public ArrayList<String> getProductCategoryAsString() throws  SQLException, RemoteException{
        String query = "SELECT * FROM `product_category`";
        PreparedStatement stmt = null;
        ResultSet set = null;
        ArrayList<String> list = null;

        try{
            stmt = Home.con.prepareStatement(query);
            set = stmt.executeQuery();

            if(set != null){
                list = new ArrayList<String>();
                while (set.next()) list.add(set.getString("prod_cat_name"));
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    /////////////////********************* GET PRODUCT CATEGORY AS STRING



    ////////////////********************* BEAN
    ////////////////********************* GET PRODUCT CATEGORY BEAN GENERAL METHOD
    private ProductCategoryBean getProductCategoryBean_GeneralMethod(String query)throws SQLException, RemoteException{
        PreparedStatement stmt = null;
        ResultSet set = null;
        ProductCategoryBean bean = null;

        try{
            stmt = Home.con.prepareStatement(query);
            set = stmt.executeQuery(query);

            if(set != null){
                bean = new ProductCategoryBean();
                if(set.next()){
                    bean.setProdCatId(set.getInt("prod_cat_id"));
                    bean.setProdCatName(set.getString("prod_cat_name"));
                    bean.setRemarks(set.getString("remarks"));
                }
            }
            return bean;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT CATEGORY BEAN GENERAL METHOD

    ////////////////********************* GET BEAN BY PRODUCT CATEGORY ID
    public ProductCategoryBean getProductCategoryBeanByProdCatId(Integer prodCatId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `product_category` WHERE prod_cat_id = "+prodCatId;
        System.out.println(query);
        return getProductCategoryBean_GeneralMethod(query);
    }
    ////////////////********************* GET BEAN BY PRODUCT CATEGORY ID

    ////////////////********************* GET BEAN BY PRODUCT CATEGORY NAME
    public ProductCategoryBean getProductCategoryBeanByProdCatName(String prodCatName)throws SQLException, RemoteException{
        String query = "SELECT * FROM `product_category` WHERE prod_cat_name = '"+prodCatName+"'";
        System.out.println(query);
        return getProductCategoryBean_GeneralMethod(query);
    }
    ////////////////********************* GET BEAN BY PRODUCT CATEGORY NAME



    ////////////////********************* SINGLE VALUE
    /////////////////********************* GET PRODUCT CATEGORY ID BY CAT NAME
    public Integer getProductCategoryIdByName(String prodCatName) throws  SQLException, RemoteException{
        String query = "SELECT prod_cat_id FROM `product_category` WHERE prod_cat_name = ?";
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;

        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, prodCatName);
            set = stmt.executeQuery();

            if(set != null)
                if(set.next()) id = set.getInt("prod_cat_id");

        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
        return id;
    }
    /////////////////********************* GET PRODUCT CATEGORY ID BY CAT NAME

    ////////////////********************* PRODUCT CATEGORY METHODS ********************************//////////////////







    ////////////////********************* PRODUCTS METHODS ********************************//////////////////

    ////////////////********************* GET PRODUCTS GENERAL METHOD
    private ArrayList getProducts_GeneralMethod(String query)throws SQLException, RemoteException{
        PreparedStatement stmt = null;
        ResultSet set = null;
        ArrayList<ProductsBean> list = null;

        try{
            stmt = Home.con.prepareStatement(query);
            set = stmt.executeQuery(query);

            if(set != null){
                list = new ArrayList<>();
                while(set.next()){
                    ProductsBean bean = new ProductsBean();
                    bean.setProductId(set.getInt("product_id"));
                    bean.setProdCatId(set.getInt("prod_cat_id"));
                    bean.setProdName(set.getString("prod_name"));
                    bean.setProdBrandName(set.getString("prod_brand_name"));
                    bean.setOpeningRate(set.getFloat("opening_rate"));
                    bean.setBuyingRate(set.getFloat("buying_rate"));
                    bean.setSaleRate(set.getFloat("sale_rate"));
                    bean.setOpeningOnhandQty(set.getFloat("opening_onhand_qty"));
                    bean.setProdCreateDate(set.getTimestamp("prod_create_date"));
                    bean.setLastProdUpdate(set.getTimestamp("last_prod_update"));
                    bean.setUpdateBy(set.getString("update_by"));
                    bean.setUnitId(set.getInt("unit_id"));
                    bean.setRemarks(set.getString("remarks"));
                    list.add(bean);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCTS GENERAL METHOD


    ////////////////********************* GET PRODUCTS GENERAL METHOD
    private ArrayList getProducts_GeneralMethod(PreparedStatement stmt)throws SQLException, RemoteException{
        ResultSet set = null;
        ArrayList<ProductsBean> list = null;

        try{
            set = stmt.executeQuery();

            if(set != null){
                list = new ArrayList<>();
                while(set.next()){
                    ProductsBean bean = new ProductsBean();
                    bean.setProductId(set.getInt("product_id"));
                    bean.setProdCatId(set.getInt("prod_cat_id"));
                    bean.setProdName(set.getString("prod_name"));
                    bean.setProdBrandName(set.getString("prod_brand_name"));
                    bean.setOpeningRate(set.getFloat("opening_rate"));
                    bean.setBuyingRate(set.getFloat("buying_rate"));
                    bean.setSaleRate(set.getFloat("sale_rate"));
                    bean.setOpeningOnhandQty(set.getFloat("opening_onhand_qty"));
                    bean.setProdCreateDate(set.getTimestamp("prod_create_date"));
                    bean.setLastProdUpdate(set.getTimestamp("last_prod_update"));
                    bean.setUpdateBy(set.getString("update_by"));
                    bean.setUnitId(set.getInt("unit_id"));
                    bean.setRemarks(set.getString("remarks"));
                    list.add(bean);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCTS GENERAL METHOD


    ////////////////********************* GET PRODUCT BEAN GENERAL METHOD
    private ProductsBean getProductBean_GeneralMethod(String query)throws SQLException, RemoteException{
        PreparedStatement stmt = null;
        ResultSet set = null;
        ProductsBean bean = null;

        try{
            stmt = Home.con.prepareStatement(query);
            set = stmt.executeQuery(query);

            if(set != null){
                bean = new ProductsBean();
                if(set.next()){
                    bean.setProductId(set.getInt("product_id"));
                    bean.setProdCatId(set.getInt("prod_cat_id"));
                    bean.setProdName(set.getString("prod_name"));
                    bean.setProdBrandName(set.getString("prod_brand_name"));
                    bean.setOpeningRate(set.getFloat("opening_rate"));
                    bean.setBuyingRate(set.getFloat("buying_rate"));
                    bean.setSaleRate(set.getFloat("sale_rate"));
                    bean.setOpeningOnhandQty(set.getFloat("opening_onhand_qty"));
                    bean.setProdCreateDate(set.getTimestamp("prod_create_date"));
                    bean.setLastProdUpdate(set.getTimestamp("last_prod_update"));
                    bean.setUpdateBy(set.getString("update_by"));
                    bean.setUnitId(set.getInt("unit_id"));
                    bean.setRemarks(set.getString("remarks"));
                }
            }
            return bean;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCTS GENERAL METHOD

    ////////////////********************* INSERT BY PRODUCT ID
    public int insertProductById(ProductsBean bean)throws SQLException, RemoteException{
        String query = "INSERT INTO `products`(`product_id`, `prod_cat_id`, `prod_name`, `prod_brand_name`, `opening_rate`, `prod_create_date`, `unit_id`, `remarks`) VALUES(?,?,?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query, Statement.NO_GENERATED_KEYS);
            stmt.setInt(1, bean.getProductId());
            stmt.setInt(2, bean.getProdCatId());
            stmt.setString(3, bean.getProdName());
            stmt.setString(4, bean.getProdBrandName());
            stmt.setFloat(5, bean.getOpeningRate());
            stmt.setTimestamp(6, bean.getProdCreateDate());
            stmt.setInt(7, bean.getUnitId());
            stmt.setString(8, bean.getRemarks());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT BY PRODUCT ID

    ////////////////********************* INSERT
    public int insertProduct(ProductsBean bean)throws SQLException, RemoteException{
        String query = "insert into products(prod_cat_id, prod_name, prod_brand_name, opening_rate, prod_create_date, unit_id, remarks) values(?,?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, bean.getProdCatId());
            stmt.setString(2, bean.getProdName());
            stmt.setString(3, bean.getProdBrandName());
            stmt.setFloat(4, bean.getOpeningRate());
            stmt.setTimestamp(5, bean.getProdCreateDate());
            stmt.setInt(6, bean.getUnitId());
            stmt.setString(7, bean.getRemarks());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT


    ////////////////********************* INSERT GET GENERATED ID
    public int insertProductGetId(ProductsBean bean)throws SQLException, RemoteException{
        String query = "insert into products(prod_cat_id, prod_name, prod_brand_name, opening_rate, prod_create_date, unit_id, remarks) values(?,?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;

        int id = 0;
        try{
            stmt = Home.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, bean.getProdCatId());
            stmt.setString(2, bean.getProdName());
            stmt.setString(3, bean.getProdBrandName());
            stmt.setFloat(4, bean.getOpeningRate());
            stmt.setTimestamp(5, bean.getProdCreateDate());
            stmt.setInt(6, bean.getUnitId());
            stmt.setString(7, bean.getRemarks());

            stmt.executeUpdate();

            set = stmt.getGeneratedKeys();
            if(set != null)
                if(set.next())
                    id = set.getInt(1);

            return id;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT GET GENERATED ID


    ////////////////********************* UPDATE
    public int updateProduct(ProductsBean bean)throws SQLException, RemoteException{
        String query = "update products set prod_name = ?, prod_brand_name = ?, remarks = ? where product_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, bean.getProdName());
            stmt.setString(2, bean.getProdBrandName());
            stmt.setString(3, bean.getRemarks());
            stmt.setInt(4, bean.getProductId());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE

    ////////////////********************* UPDATE OPENING ON HAND QTY
    public int updateProductOpeningOnHandQty(Integer productId, Float openingOnHandQty)throws SQLException, RemoteException{
        String query = "UPDATE `products` SET `opening_onhand_qty` = ? WHERE `product_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setFloat(1, openingOnHandQty);
            stmt.setInt(2, productId);
            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE OPENING ON HAND QTY

    ////////////////********************* UPDATE CODDING FORM
    public int updateProductCoddingForm(ProductsBean bean)throws SQLException, RemoteException{
        String query = "update products set buying_rate = ?, sale_rate = ?, update_by = ?, last_prod_update = ? where product_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setFloat(1, bean.getBuyingRate());
            stmt.setFloat(2, bean.getSaleRate());
            stmt.setString(3, bean.getUpdateBy());
            stmt.setTimestamp(4, bean.getLastProdUpdate());
            stmt.setInt(5, bean.getProductId());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE CODDING FORM

    ////////////////********************* UPDATE FAST CODDING FORM      update sale_rate, last_prod_update, update_by
    public int updateProductCoddingFastCoding(ProductsBean bean)throws SQLException, RemoteException{
        String query = "UPDATE `products` SET `sale_rate` = ?, `last_prod_update` = ?, `update_by` = ? WHERE `product_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setFloat(1, bean.getOpeningRate());    ///*** set opening_rate -> sale_rate
            stmt.setTimestamp(2, bean.getLastProdUpdate());
            stmt.setString(3, bean.getUpdateBy());
            stmt.setInt(4, bean.getProductId());

            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE FAST CODDING FORM

    ////////////////********************* GET ALL
    public ArrayList getProducts()throws SQLException, RemoteException{
        String query = "SELECT * FROM `products`";
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET ALL

    ////////////////********************* GET PRODUCT ID
    public ArrayList getProductsByProductId(Integer productId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `products` WHERE product_id = "+productId;
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET PRODUCT ID

    ////////////////********************* GET BY PRODUCT CATEGORY ID
    public ArrayList getProductsByProdCatId(Integer prodCatId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `products` WHERE prod_cat_id = "+prodCatId;
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET BY PRODUCT CATEGORY ID

    ////////////////********************* GET BY PRODUCT CATEGORY NAME
    public ArrayList getProductsByProdCatName(String prodCatName)throws SQLException, RemoteException{
        String query = "SELECT * FROM `products` WHERE prod_cat_id LIKE " +
                "(SELECT prod_cat_id FROM product_category WHERE prod_cat_name LIKE '"+prodCatName+"%')";
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET BY PRODUCT CATEGORY NAME

    ////////////////********************* GET BY PRODUCT NAME
    public ArrayList getProductsByProductName(String prodName)throws SQLException, RemoteException{
        String query = "SELECT * FROM `products` WHERE prod_name LIKE '"+prodName+"%'";
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET BY PRODUCT NAME

    ////////////////********************* GET BY PRODUCT BRAND NAME
    public ArrayList getProductsByBrandName(String brandNmae)throws SQLException, RemoteException{
        String query = "SELECT * FROM `products` WHERE prod_brand_name LIKE '"+brandNmae+"%'";
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET BY PRODUCT BRAND NAME

    ////////////////********************* GET BY PRODUCT NAME AND CATEGORY ID
    public ArrayList getProductsByProductNameAndProdCatId(String prodName, Integer prodCatId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `products` WHERE prod_name LIKE '"+prodName+"%' AND prod_cat_id = "+prodCatId;
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET BY PRODUCT NAME AND CATEGORY ID

    ////////////////********************* GET BY PRODUCT BRAND NAME AND CATEGORY ID
    public ArrayList getProductsByBrandNameAndProdCatId(String brandName, Integer prodCatId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `products` WHERE prod_brand_name LIKE '"+brandName+"%' AND prod_cat_id = "+prodCatId;
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET BY PRODUCT BRAND NAME AND CATEGORY ID


    ////////////////********************* GET PRODUCT SEARCH
    public ArrayList getProductSearch(ProductsBean bean) throws SQLException, RemoteException{
        String query = "SELECT * FROM `products` WHERE `prod_cat_id` = ? ";

        Byte counter = 0;

        /////***** PRODUCT ID
        if(bean.getProductId() != 0) query += "AND `product_id` =  ? ";
        /////***** PRODUCT NAME
        if(!bean.getProdName().equals("")) query += "AND `prod_name` Like ? ";
        /////***** BRAND NAME
        if(!bean.getProdBrandName().equals("")) query += "AND `prod_brand_name` Like  ? ";
        /////***** PRICE
        if(bean.getSaleRate() != 0) query += "AND `sale_rate` =  ? ";

        /////***** ORDER BY
        query += "ORDER BY `prod_name`";

        PreparedStatement stmt = Home.con.prepareStatement(query);
        System.out.println(query);

        /////***** PRODUCT CAT ID
        stmt.setInt(++counter, bean.getProdCatId());
        /////***** PRODUCT ID
        if(bean.getProductId() != 0) stmt.setInt(++counter, bean.getProductId());
        /////***** PRODUCT NAME
        if(!bean.getProdName().equals("")) stmt.setString(++counter, "%"+bean.getProdName()+"%");
        /////***** BRAND NAME
        if(!bean.getProdBrandName().equals("")) stmt.setString(++counter, "%"+bean.getProdBrandName()+"%");
        /////***** PRICE
        if(bean.getSaleRate() != 0) stmt.setFloat(++counter, bean.getSaleRate());

        return getProducts_GeneralMethod(stmt);
    }
    ////////////////********************* GET PRODUCT SEARCH



    ////////////////********************* BEAN
    ////////////////********************* GET PRODUCT BEAN BY ID
    public ProductsBean getProductBeanByProductId(Integer productId)throws SQLException, RemoteException{
        String query = "SELECT * FROM `products` WHERE product_id = "+productId;
        System.out.println(query);
        return getProductBean_GeneralMethod(query);
    }
    ////////////////********************* GET PRODUCT BEAN BY ID



    ////////////////********************* JOINING VALUES
    ////////////////********************* GET PRODUCT IN GODOWN WITH CATEGORY
    /***
     *  product IN (all product in godown = ? and prod_cat_id = ?) and prod_cat_id = ?
     *  -> Show all Product that exit in selected godown with selected product category
     * ***/
    public ArrayList getProductsINGodownWithCatId(Integer godownId, Integer prodCatId)throws SQLException, RemoteException{
        String query = "SELECT products.* FROM products " +
                "WHERE product_id IN " +
                "( " +
                    "SELECT DISTINCT products.product_id " +
                    "FROM products, product_transection, godown " +
                    "WHERE products.product_id = product_transection.product_id AND " +
                    "godown.godown_id = product_transection.godown_id " +
                    "AND godown.godown_id = " +godownId+
                ") " +
                "AND products.`prod_cat_id` = "+prodCatId;

        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET PRODUCT IN GODOWN WITH CATEGORY

    ////////////////********************* GET PRODUCT NOT IN GODOWN WITH CATEGORY
    /***
     *  product NOT IN (all product in godown = ? and prod_cat_id = ?) and prod_cat_id = ?
     *  -> Show all Product that NOT exit in selected godown with selected product category
     * ***/
    public ArrayList getProductsNOTInGodownWithCatId(Integer godownId, Integer prodCatId)throws SQLException, RemoteException{
        String query = "SELECT products.* FROM products " +
                "WHERE product_id NOT IN " +
                "( " +
                    "SELECT DISTINCT products.product_id " +
                    "FROM products, product_transection, godown " +
                    "WHERE products.product_id = product_transection.product_id AND " +
                    "godown.godown_id = product_transection.godown_id " +
                    "AND godown.godown_id = " +godownId+
                ") " +
                "AND products.`prod_cat_id` = "+prodCatId;


        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET PRODUCT NOT IN GODOWN WITH CATEGORY

    ////////////////********************* GET PRODUCT SEARCH
    public ArrayList getProductSearch(ProductsBean bean, Integer godownId) throws SQLException, RemoteException{
        String query = "SELECT DISTINCT `products`.* FROM `products`, `product_transection`, `godown` " +
                "WHERE `products`.`product_id` = `product_transection`.`product_id` AND " +
                "`product_transection`.`godown_id` = `godown`.`godown_id` AND " +
                "`godown`.`godown_id` = ? AND " +
                "`products`.`prod_cat_id` = ? ";

        Byte counter = 0;

        /////***** PRODUCT ID
        if(bean.getProductId() != 0) query += "AND `products`.`product_id` =  ? ";
        /////***** PRODUCT NAME
        if(!bean.getProdName().equals("")) query += "AND `products`.`prod_name` Like ? ";
        /////***** BRAND NAME
        if(!bean.getProdBrandName().equals("")) query += "AND `products`.`prod_brand_name` Like  ? ";
        /////***** PRICE
        if(bean.getSaleRate() != 0) query += "AND `products`.`sale_rate` =  ? ";

        /////***** ORDER BY
        query += "ORDER BY `products`.`prod_name`";

        PreparedStatement stmt = Home.con.prepareStatement(query);
        System.out.println(query);

        /////***** GODOWN ID
        stmt.setInt(++counter, godownId);
        /////***** PRODUCT CAT ID
        stmt.setInt(++counter, bean.getProdCatId());
        /////***** PRODUCT ID
        if(bean.getProductId() != 0) stmt.setInt(++counter, bean.getProductId());
        /////***** PRODUCT NAME
        if(!bean.getProdName().equals("")) stmt.setString(++counter, "%"+bean.getProdName()+"%");
        /////***** BRAND NAME
        if(!bean.getProdBrandName().equals("")) stmt.setString(++counter, "%"+bean.getProdBrandName()+"%");
        /////***** PRICE
        if(bean.getSaleRate() != 0) stmt.setFloat(++counter, bean.getSaleRate());

        return getProducts_GeneralMethod(stmt);
    }
    ////////////////********************* GET PRODUCT SEARCH

    ////////////////********************* GET BY GODOWN ID
    public ArrayList getProductsByGodownId(Integer godownId)throws SQLException, RemoteException{
        String query = "SELECT DISTINCT `products`.* FROM `products`, `product_transection`, `godown` " +
                "WHERE `products`.`product_id` = `product_transection`.`product_id` AND " +
                "`product_transection`.`godown_id` = `godown`.`godown_id` AND " +
                "`godown`.`godown_id` = "+godownId;
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET BY GODOWN ID AND PRODUCT ID

    ////////////////********************* GET BY GODOWN ID AND PRODUCT ID
    public ArrayList getProductsByGodownIdAndProductId(Integer godownId, Integer productId)throws SQLException, RemoteException{
        String query = "SELECT DISTINCT `products`.* FROM `products`, `product_transection`, `godown` " +
                "WHERE `products`.`product_id` = `product_transection`.`product_id` AND " +
                "`product_transection`.`godown_id` = `godown`.`godown_id` AND " +
                "`godown`.`godown_id` = "+godownId+" AND " +
                "`products`.`product_id` = "+productId;
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET BY GODOWN ID AND PRODUCT ID

    ////////////////********************* GET BY GODOWN ID AND PRODUCT NAME
    public ArrayList getProductsByGodownIdAndProductName(Integer godownId, String productName)throws SQLException, RemoteException{
        String query = "SELECT DISTINCT `products`.* FROM `products`, `product_transection`, `godown` " +
                "WHERE `products`.`product_id` = `product_transection`.`product_id` AND " +
                "`product_transection`.`godown_id` = `godown`.`godown_id` AND " +
                "`godown`.`godown_id` = "+godownId+" AND " +
                "`products`.`prod_name` LIKE '"+productName+"%'";
        System.out.println(query);
        return getProducts_GeneralMethod(query);
    }
    ////////////////********************* GET BY GODOWN ID AND PRODUCT NAME



    ////////////////********************* SINGLE VALUE
    ////////////////********************* GET PRODUCT NAME BY PRODUCT ID
    public String getProductNameByProductId(Integer productId) throws SQLException, RemoteException{
        String query = "select prod_name from products where product_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, productId);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) return set.getString("prod_name");
            return null;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT NAME BY PRODUCT ID

    ////////////////********************* GET PRODUCT ON HAND OPENING QTY BY PRODUCT ID
    public int getProductOpeningQtyByProductId(Integer productId) throws SQLException, RemoteException{
        String query = "select opening_onhand_qty from products where product_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, productId);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) return set.getInt("opening_onhand_qty");
            return 0;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT ON HAND OPENING QTY BY PRODUCT ID

    ////////////////********************* GET PRODUCT UNIT NAME BY PRODUCT ID
    public String getProductUnitNameByProductId(Integer productId) throws SQLException, RemoteException{
        String query = "select unit_name AS unit from unit where unit_id = (select unit_id from products where product_id = ? )";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, productId);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) return set.getString("unit");
            return null;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT UNIT NAME BY PRODUCT ID

    ////////////////********************* GET PRODUCT BUYING RATE BY PRODUCT ID
    public Float getProductBuyingRateByProductId(Integer productId) throws SQLException, RemoteException{
        String query = "SELECT `buying_rate` FROM `products` WHERE `product_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, productId);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) return set.getFloat("buying_rate");
            return null;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT BUYING RATE BY PRODUCT ID

    ////////////////********************* GET PRODUCT ALL QUANTITY BY CUSTOMER TYPE & PRODUCT ID
    public Float getProductAllQuantityByCustomerTypeAndProductId(String customerType, Integer productId) throws SQLException, RemoteException{
        String query = "SELECT SUM(`product_transection`.`quantity`) AS quantity " +
                "FROM `customers`, `transection`, `products`, `product_transection` " +
                "WHERE " +
                "`customers`.`customer_id` = `transection`.`customer_id` AND " +
                "`transection`.`transection_id` = `product_transection`.`transection_id` AND " +
                "`products`.`product_id` = `product_transection`.`product_id` AND " +
                "`customers`.`customer_type` = ? AND " +
                "`products`.`product_id` = ? ";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, customerType);
            stmt.setInt(2, productId);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) return set.getFloat("quantity");
            return null;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT ALL QUANTITY BY CUSTOMER TYPE & PRODUCT ID

    ////////////////********************* GET PRODUCT ALL QUANTITY BY VOUCHAR TYPE & PRODUCT ID
    public Float getProductAllQuantityByVoucharTypeAndProductId(String voucharType, Integer productId) throws SQLException, RemoteException{
        String query = "SELECT SUM(`product_transection`.`quantity`) AS quantity " +
                "FROM `transection`, `products`, `product_transection` " +
                "WHERE " +
                "`transection`.`transection_id` = `product_transection`.`transection_id` AND " +
                "`products`.`product_id` = `product_transection`.`product_id` AND " +
                "`transection`.`voucher_type` = ? AND " +
                "`products`.`product_id` = ? AND " +
                "`transection`.`status` = ?";
        System.out.println(query);
        Float quantity = null;
        PreparedStatement stmt = null;
        ResultSet set = null;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, voucharType);
            stmt.setInt(2, productId);
            stmt.setString(3, Values.Transection.statusClear);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) quantity = set.getFloat("quantity");

            if(quantity != null) return quantity;
            return Float.valueOf(0);
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT ALL QUANTITY BY VOUCHAR TYPE & PRODUCT ID

    ////////////////********************* GET PRODUCT ALL QUANTITY BY CUSTOMER TYPE & VOUCHAR TYPE & PRODUCT ID
    public Float getProductAllQuantityByCustomerTypeAndVoucharTypeAndProductId(String customerType, String voucharType, Integer productId) throws SQLException, RemoteException{
        String query = "SELECT SUM(`product_transection`.`quantity`) AS quantity " +
                "FROM `customers`, `transection`, `products`, `product_transection` " +
                "WHERE " +
                "`customers`.`customer_id` = `transection`.`customer_id` AND " +
                "`transection`.`transection_id` = `product_transection`.`transection_id` AND " +
                "`products`.`product_id` = `product_transection`.`product_id` AND " +
                "`transection`.`voucher_type` = ? AND " +
                "`customers`.`customer_type` = ? AND " +
                "`products`.`product_id` = ? AND " +
                "`transection`.`status` = ?";
        System.out.println(query);
        Float quantity = null;
        PreparedStatement stmt = null;
        ResultSet set = null;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, voucharType);
            stmt.setString(2, customerType);
            stmt.setInt(3, productId);
            stmt.setString(4, Values.Transection.statusClear);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) quantity = set.getFloat("quantity");

            if(quantity != null) return quantity;
            return Float.valueOf(0);
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT ALL QUANTITY BY CUSTOMER TYPE & VOUCHAR TYPE & PRODUCT ID

    ////////////////********************* GET PRODUCT AVAILABLE QUANTITY BY PRODUCT ID
    public Float getProductAllAvailableQuantityByProductId(Integer productId) throws SQLException, RemoteException{
        Float allAvailableQty =
        ( getProductOpeningQtyByProductId(productId) +                                                                                                       // OPENING QUANTITY +
        getProductAllQuantityByCustomerTypeAndVoucharTypeAndProductId(Values.Customer.SupplierType, Values.Transection.voucherTypeRegular, productId) +      // PURCHASE +
        getProductAllQuantityByCustomerTypeAndVoucharTypeAndProductId(Values.Customer.CustomerType, Values.Transection.voucherTypeReturn, productId) +       // RETURN SALE +
        getProductAllQuantityByVoucharTypeAndProductId(Values.Transection.voucherTypeGodownIn, productId) ) -                                                // GODOWN IN +
        getProductAllQuantityByCustomerTypeAndVoucharTypeAndProductId(Values.Customer.CustomerType, Values.Transection.voucherTypeRegular, productId) -      // SALE -
        getProductAllQuantityByCustomerTypeAndVoucharTypeAndProductId(Values.Customer.CustomerType, Values.Transection.voucherTypeSale, productId) -         // CASH SALE -
        getProductAllQuantityByCustomerTypeAndVoucharTypeAndProductId(Values.Customer.SupplierType, Values.Transection.voucherTypeReturn, productId) -       // RETURN PURCHASE -
        getProductAllQuantityByVoucharTypeAndProductId(Values.Transection.voucherTypeDeadStock, productId) -                                                 // DEAD STOCK -
        getProductAllQuantityByVoucharTypeAndProductId(Values.Transection.voucherTypeGodownToGodown, productId);                                             // GODOWN TO GODOWN -
        return allAvailableQty;
    }
    ////////////////********************* GET PRODUCT AVAILABLE QUANTITY BY PRODUCT ID


    ////////////////********************* WITH GODOWN ID
    ////////////////********************* GET PRODUCT ALL QUANTITY BY CUSTOMER TYPE & GODOWN ID & PRODUCT ID
    public Float getProductQuantityByCustomerTypeAndGodownIdAndProductId(String customerType, Integer godownId, Integer productId) throws SQLException, RemoteException{
        String query = "SELECT SUM(`product_transection`.`quantity`) AS quantity " +
                "FROM `customers`, transection, `products`, `product_transection`, `godown` " +
                "WHERE `customers`.`customer_id` = `transection`.`customer_id` AND  " +
                "`transection`.`transection_id` = `product_transection`.`transection_id` AND " +
                "`products`.`product_id` = `product_transection`.`product_id` AND " +
                "`product_transection`.`godown_id` = `godown`.`godown_id` AND " +
                "`customers`.`customer_type` = ? AND  " +
                "`godown`.`godown_id` = ? AND " +
                "`products`.`product_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, customerType);
            stmt.setInt(2, godownId);
            stmt.setInt(3, productId);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) return set.getFloat("quantity");
            return null;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT ALL QUANTITY BY CUSTOMER TYPE & GODOWN ID & PRODUCT ID

    ////////////////********************* GET PRODUCT QUANTITY BY VOUCHAR TYPE & GODOWN ID & PRODUCT ID
    public Float getProductQuantityByVoucharTypeAndGodownIdAndProductId(String voucharType, Integer godownId, Integer productId) throws SQLException, RemoteException{
        String query = "SELECT SUM(`product_transection`.`quantity`) AS quantity " +
                "FROM `transection`, `products`, `product_transection`, `godown` " +
                "WHERE `transection`.`transection_id` = `product_transection`.`transection_id` AND " +
                "`products`.`product_id` = `product_transection`.`product_id` AND " +
                "`godown`.`godown_id` = `product_transection`.`godown_id` AND " +
                "`transection`.`voucher_type` = ? AND " +
                "`godown`.`godown_id` = ? AND " +
                "`products`.`product_id` = ? AND " +
                "`transection`.`status` = ?";
        System.out.println(query);
        Float quantity = null;
        PreparedStatement stmt = null;
        ResultSet set = null;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, voucharType);
            stmt.setInt(2, godownId);
            stmt.setInt(3, productId);
            stmt.setString(4, Values.Transection.statusClear);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) quantity = set.getFloat("quantity");

                System.out.println("Type : "+voucharType+" GID : "+godownId+" PID : "+productId);
                System.out.println("QTY : "+quantity);
            if (quantity != null) return quantity;
            return Float.valueOf(0);
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT QUANTITY BY VOUCHAR TYPE & GODOWN ID & PRODUCT ID

    ////////////////********************* GET PRODUCT QUANTITY BY CUSTOMER TYPE & VOUCHAR TYPE & GODOWN ID & PRODUCT ID
    public Float getProductQuantityByCustomerTypeAndVoucharTypeAndGodownIdAndProductId(String customerType, String voucharType, Integer godownId, Integer productId) throws SQLException, RemoteException{
        String query = "SELECT SUM(`product_transection`.`quantity`) AS quantity " +
                "FROM `customers`, `transection`, `products`, `product_transection`, `godown` " +
                "WHERE `customers`.`customer_id` = `transection`.`customer_id` AND " +
                "`transection`.`transection_id` = `product_transection`.`transection_id` AND " +
                "`products`.`product_id` = `product_transection`.`product_id` AND " +
                "`godown`.`godown_id` = `product_transection`.`godown_id` AND " +
                "`customers`.`customer_type` = ? AND " +
                "`transection`.`voucher_type` = ? AND " +
                "`godown`.`godown_id` = ? AND " +
                "`products`.`product_id` = ? AND " +
                "`transection`.`status` = ?";
        System.out.println(query);
        Float quantity = null;
        PreparedStatement stmt = null;
        ResultSet set = null;

        try {
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, customerType);
            stmt.setString(2, voucharType);
            stmt.setInt(3, godownId);
            stmt.setInt(4, productId);
            stmt.setString(5, Values.Transection.statusClear);
            set = stmt.executeQuery();
            if(set != null)
                if(set.next()) quantity = set.getFloat("quantity");

            //System.out.println(customerType+" "+voucharType+" "+quantity);
            if(quantity != null) return quantity;
            return Float.valueOf(0);
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT QUANTITY BY CUSTOMER TYPE & VOUCHAR TYPE & GODOWN ID & PRODUCT ID

    ////////////////********************* GET PRODUCT AVAILABLE QUANTITY BY GODOWN ID & PRODUCT ID
    public Float getProductAvailableQuantityByGodownIdAndProductId(Integer godownId, Integer productId) throws SQLException, RemoteException{
        Float availableQty =
        ( getProductOpeningQtyByProductId(productId) +                                                                                                                       // OPENING QUANTITY +
        getProductQuantityByCustomerTypeAndVoucharTypeAndGodownIdAndProductId(Values.Customer.SupplierType, Values.Transection.voucherTypeRegular, godownId, productId) +    // PURCHASE +
        getProductQuantityByCustomerTypeAndVoucharTypeAndGodownIdAndProductId(Values.Customer.CustomerType, Values.Transection.voucherTypeReturn, godownId, productId)  +    // RETURN SALE +
        getProductQuantityByVoucharTypeAndGodownIdAndProductId(Values.Transection.voucherTypeGodownIn, godownId, productId) ) -                                              // GODOWN IN +
        getProductQuantityByCustomerTypeAndVoucharTypeAndGodownIdAndProductId(Values.Customer.CustomerType, Values.Transection.voucherTypeRegular, godownId, productId) -    // SALE -
        getProductQuantityByCustomerTypeAndVoucharTypeAndGodownIdAndProductId(Values.Customer.CustomerType, Values.Transection.voucherTypeSale, godownId, productId) -       // CASH SALE -
        getProductQuantityByCustomerTypeAndVoucharTypeAndGodownIdAndProductId(Values.Customer.SupplierType, Values.Transection.voucherTypeReturn, godownId, productId) -     // RETURN PURCHASE -
        getProductQuantityByVoucharTypeAndGodownIdAndProductId(Values.Transection.voucherTypeDeadStock, godownId, productId) -                                               // DEAD STOCK -
        getProductQuantityByVoucharTypeAndGodownIdAndProductId(Values.Transection.voucherTypeGodownToGodown, godownId, productId);                                           // GODOWN TO GODOWN -

        return availableQty;
    }
    ////////////////********************* GET PRODUCT AVAILABLE QUANTITY BY GODOWN ID & PRODUCT ID

    ////////////////********************* PRODUCTS METHODS ********************************//////////////////








    ////////////////********************* TRANSECTION METHODS ********************************//////////////////

    ////////////////********************* GET TRANSECTION GENERAL METHOD
    private ArrayList getTransection_GeneralMethod(PreparedStatement stmt)throws SQLException, RemoteException{
        ResultSet set = null;
        ArrayList<TransectionBean> list = null;

        try{
            set = stmt.executeQuery();

            if(set != null){
                list = new ArrayList<>();
                while(set.next()){
                    TransectionBean bean = new TransectionBean();
                    bean.setTransectionId(set.getInt("transection_id"));
                    bean.setTransDate(set.getTimestamp("trans_date"));
                    bean.setCustomerId(set.getInt("customer_id"));
                    bean.setUserId(set.getInt("user_id"));
                    bean.setName(set.getString("name"));
                    bean.setFromGodownId(set.getInt("from_godown_id"));
                    bean.setToGodownId(set.getInt("to_godown_id"));
                    bean.setVoucherType(set.getString("voucher_type"));
                    bean.setPaymentType(set.getString("payment_type"));
                    bean.setPackingExpence(set.getFloat("packing_expence"));
                    bean.setOtherExpence(set.getFloat("other_expence"));
                    bean.setDiscount(set.getFloat("discount"));
                    bean.setTotalAmount(set.getFloat("total_amount"));
                    bean.setReceivableAmount(set.getFloat("receivable_amount"));
                    bean.setPendingAmount(set.getFloat("pending_amount"));
                    bean.setHasBillInHand(set.getBoolean("has_bill_in_hand"));
                    bean.setStatus(set.getString("status"));
                    bean.setReferenceBillNo(set.getString("reference_bill_no"));
                    bean.setTransportName(set.getString("transport_name"));
                    bean.setRemarks(set.getString("remarks"));
                    bean.setConfirmedBy(set.getString("confirmed_by"));
                    list.add(bean);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET TRANSECTION GENERAL METHOD

    ////////////////********************* GET TRANSECTION BEAN GENERAL METHOD
    private TransectionBean getTransectionBean_GeneralMethod(PreparedStatement stmt)throws SQLException, RemoteException{
        ResultSet set = null;
        TransectionBean bean = null;

        try{
            set = stmt.executeQuery();

            if(set != null){
                bean = new TransectionBean();
                if(set.next()){
                    bean.setTransectionId(set.getInt("transection_id"));
                    bean.setTransDate(set.getTimestamp("trans_date"));
                    bean.setCustomerId(set.getInt("customer_id"));
                    bean.setUserId(set.getInt("user_id"));
                    bean.setName(set.getString("name"));
                    bean.setFromGodownId(set.getInt("from_godown_id"));
                    bean.setToGodownId(set.getInt("to_godown_id"));
                    bean.setVoucherType(set.getString("voucher_type"));
                    bean.setPaymentType(set.getString("payment_type"));
                    bean.setPackingExpence(set.getFloat("packing_expence"));
                    bean.setOtherExpence(set.getFloat("other_expence"));
                    bean.setDiscount(set.getFloat("discount"));
                    bean.setTotalAmount(set.getFloat("total_amount"));
                    bean.setReceivableAmount(set.getFloat("receivable_amount"));
                    bean.setPendingAmount(set.getFloat("pending_amount"));
                    bean.setHasBillInHand(set.getBoolean("has_bill_in_hand"));
                    bean.setStatus(set.getString("status"));
                    bean.setReferenceBillNo(set.getString("reference_bill_no"));
                    bean.setTransportName(set.getString("transport_name"));
                    bean.setRemarks(set.getString("remarks"));
                    bean.setConfirmedBy(set.getString("confirmed_by"));
                }
            }
            return bean;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET TRANSECTION BEAN GENERAL METHOD


    ////////////////********************* INSERT BILL AND GET GENERATED ID
    ///*** SALE FORM, ADD STOCK, RETURN SALE FORM, RETURN STOCK FORM
    public Integer insertTransectionBillGetId(TransectionBean bean)throws SQLException, RemoteException{
        String query = "insert into transection(trans_date, customer_id, user_id, name, voucher_type, payment_type, packing_expence, " +
                "other_expence, discount, total_amount, receivable_amount, pending_amount, has_bill_in_hand, status, reference_bill_no, " +
                "transport_name, remarks, confirmed_by) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;

        try{
            stmt = Home.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setTimestamp(1, bean.getTransDate());
            stmt.setInt(2, bean.getCustomerId());
            stmt.setInt(3, bean.getUserId());
            stmt.setString(4, bean.getName());
            stmt.setString(5, bean.getVoucherType());
            stmt.setString(6, bean.getPaymentType());
            stmt.setFloat(7, bean.getPackingExpence());
            stmt.setFloat(8, bean.getOtherExpence());
            stmt.setFloat(9, bean.getDiscount());
            stmt.setFloat(10, bean.getTotalAmount());
            stmt.setFloat(11, bean.getReceivableAmount());
            stmt.setFloat(12, bean.getPendingAmount());
            stmt.setBoolean(13, bean.getHasBillInHand());
            stmt.setString(14, bean.getStatus());
            stmt.setString(15, bean.getReferenceBillNo());
            stmt.setString(16, bean.getTransportName());
            stmt.setString(17, bean.getRemarks());
            stmt.setString(18, bean.getConfirmedBy());
            stmt.executeUpdate();

            set = stmt.getGeneratedKeys();
            if(set != null)
                if(set.next()) id = set.getInt(1);
            return id;
        }
        finally{
            if(stmt != null) stmt.close();
            if(set != null) set.close();
        }
    }
    ////////////////********************* INSERT BILL AND GET GENERATED ID


    ////////////////********************* INSERT RECEIPT AND GET GENERATED ID
    public Integer insertTransectionReceiptGetId(TransectionBean bean)throws SQLException, RemoteException{
        String query = "insert into transection(trans_date, customer_id, user_id, voucher_type, payment_type, status, total_amount, remarks, confirmed_by) values(?,?,?,?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;

        try{
            stmt = Home.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setTimestamp(1, bean.getTransDate());
            stmt.setInt(2, bean.getCustomerId());
            stmt.setInt(3, bean.getUserId());
            stmt.setString(4, bean.getVoucherType());
            stmt.setString(5, bean.getPaymentType());
            stmt.setString(6, bean.getStatus());
            stmt.setFloat(7, bean.getTotalAmount());
            stmt.setString(8, bean.getRemarks());
            stmt.setString(9, bean.getConfirmedBy());
            stmt.executeUpdate();

            set = stmt.getGeneratedKeys();
            if(set != null)
                if(set.next()) id = set.getInt(1);
            return id;
        }
        finally{
            if(stmt != null) stmt.close();
            if(set != null) set.close();
        }
    }
    ////////////////********************* INSERT RECEIPT AND GET GENERATED ID

    ////////////////********************* INSERT DEAD STOCK AND GET GENERATED ID
    public Integer insertTransectionDeadStockGetId(TransectionBean bean)throws SQLException, RemoteException{
        String query = "insert into transection(trans_date, voucher_type, user_id, status, total_amount, remarks, confirmed_by) values(?,?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;

        try{
            stmt = Home.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setTimestamp(1, bean.getTransDate());
            stmt.setString(2, bean.getVoucherType());
            stmt.setInt(3, bean.getUserId());
            stmt.setString(4, bean.getStatus());
            stmt.setFloat(5, bean.getTotalAmount());
            stmt.setString(6, bean.getRemarks());
            stmt.setString(7, bean.getConfirmedBy());
            stmt.executeUpdate();

            set = stmt.getGeneratedKeys();
            if(set != null)
                if(set.next())
                    id = set.getInt(1);

            return id;
        }
        finally{
            if(stmt != null) stmt.close();
            if(set != null) set.close();
        }
    }
    ////////////////********************* INSERT DEAD STOCK AND GET GENERATED ID

    ////////////////********************* INSERT GODOWN TO GODOWN AND GET GENERATED ID
    public Integer insertTransectionGodownToGodownAndGetId(TransectionBean bean)throws SQLException, RemoteException{
        String query = "insert into transection(trans_date, from_godown_id, to_godown_id, voucher_type, user_id, status, transport_name, total_amount, remarks, confirmed_by) values(?,?,?,?,?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;

        try{
            stmt = Home.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setTimestamp(1, bean.getTransDate());
            stmt.setInt(2, bean.getFromGodownId());
            stmt.setInt(3, bean.getToGodownId());
            stmt.setString(4, bean.getVoucherType());
            stmt.setInt(5, bean.getUserId());
            stmt.setString(6, bean.getStatus());
            stmt.setString(7, bean.getTransportName());
            stmt.setFloat(8, bean.getTotalAmount());
            stmt.setString(9, bean.getRemarks());
            stmt.setString(10, bean.getConfirmedBy());
            stmt.executeUpdate();

            set = stmt.getGeneratedKeys();
            if(set != null) if(set.next()) id = set.getInt(1);

            return id;
        }
        finally{
            if(stmt != null) stmt.close();
            if(set != null) set.close();
        }
    }
    ////////////////********************* INSERT GODOWN TO GODOWN AND GET GENERATED ID

    ////////////////********************* INSERT GODOWN IN AND GET GENERATED ID
    public Integer insertTransectionGodownInAndGetId(TransectionBean bean)throws SQLException, RemoteException{
        String query = "insert into transection(trans_date, from_godown_id, voucher_type, user_id, status, reference_bill_no, total_amount) values(?,?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;

        try{
            stmt = Home.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setTimestamp(1, bean.getTransDate());
            stmt.setInt(2, bean.getFromGodownId());
            stmt.setString(3, bean.getVoucherType());
            stmt.setInt(4, bean.getUserId());
            stmt.setString(5, bean.getStatus());
            stmt.setString(6, bean.getReferenceBillNo());
            stmt.setFloat(7, bean.getTotalAmount());
            stmt.executeUpdate();

            set = stmt.getGeneratedKeys();
            if(set != null)
                if(set.next()) id = set.getInt(1);
            return id;
        }
        finally{
            if(stmt != null) stmt.close();
            if(set != null) set.close();
        }
    }
    ////////////////********************* INSERT GODOWN IN AND GET GENERATED ID


    ////////////////********************* UPDATE RECEIPT
    public Integer updateTransectionReceipt(Integer transectionId, String status, String remarks)throws SQLException, RemoteException{
        String query = "update transection set status = ?, remarks = ? where transection_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        ResultSet set = null;
        Integer id = 0;

        try{
            stmt = Home.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, status);
            stmt.setString(2, remarks);
            stmt.setInt(3, transectionId);
            return stmt.executeUpdate();
        }
        finally{
            if(stmt != null) stmt.close();
            if(set != null) set.close();
        }
    }
    ////////////////********************* UPDATE RECEIPT

    ////////////////********************* UPDATE TRANSECTION STOCK
    public Integer updateTransectionStock(Integer transectionId, Boolean hasBill)throws SQLException, RemoteException{
        String query = "update transection set has_bill_in_hand = ? where transection_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;

        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setBoolean(1, hasBill);
            stmt.setInt(2, transectionId);
            return stmt.executeUpdate();
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE TRANSECTION STOCK

    ////////////////********************* UPDATE TRANSECTION SALE
    public Integer updateTransectionSale(Integer transectionId, String name, Float packingExpence, Float otherExpence, Float dicount, Float totalAmount, Float receivableAmount, Float pendingAmount, String status, String remarks)throws SQLException, RemoteException{
        String query = "update transection set name = ?, packing_expence = ?, other_expence = ?, discount = ?, total_amount = ?, receivable_amount = ?, pending_amount = ?, status = ?, remarks = ? where transection_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;

        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setFloat(2, packingExpence);
            stmt.setFloat(3, otherExpence);
            stmt.setFloat(4, dicount);
            stmt.setFloat(5, totalAmount);
            stmt.setFloat(6, receivableAmount);
            stmt.setFloat(7, pendingAmount);
            stmt.setString(8, status);
            stmt.setString(9, remarks);
            stmt.setInt(10, transectionId);
            return stmt.executeUpdate();
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE TRANSECTION SALE

    ////////////////********************* UPDATE TRANSECTION GODOWN IN
    public Integer updateTransectionGodownIn(Integer transectionId, String status, String transportName, String confirmedBy, String remarks)throws SQLException, RemoteException{
        String query = "update transection set status = ?, transport_name = ?, confirmed_by = ?, remarks = ? where transection_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;

        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, transportName);
            stmt.setString(3, confirmedBy);
            stmt.setString(4, remarks);
            stmt.setInt(5, transectionId);
            return stmt.executeUpdate();
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* UPDATE TRANSECTION GODOWN IN

    ////////////////********************* DELETE TRANSECTION
    public Integer deleteTransection(Integer transectionId)throws SQLException, RemoteException{
        deleteDetermination(transectionId);
        deleteProductTransection(transectionId);
        String query = "DELETE FROM `transection` WHERE `transection_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;

        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, transectionId);
            return stmt.executeUpdate();
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* DELETE TRANSECTION

    ////////////////********************* GET ALL
    public ArrayList getTransection() throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection`";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET ALL

    ////////////////********************* GET BY CUSTOMER ID
    public ArrayList getTransectionByCustomerId(Integer customerId) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `customer_id` = ? AND voucher_type != ? ORDER BY trans_date";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, customerId);
        stmt.setString(2, Values.Transection.voucherTypeSale);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET BY CUSTOMER ID

    ////////////////********************* GET BY CUSTOMER ID AND FROM DATE , TO DATE
    public ArrayList getTransectionByCustomerIdAndFromDateToDate(Integer customerId, Timestamp fromDate, Timestamp toDate) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `customer_id` = ? AND trans_date >= ? AND `trans_date` <= ? AND voucher_type != ? ORDER BY `trans_date`";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, customerId);
        stmt.setTimestamp(2, fromDate);
        stmt.setTimestamp(3, toDate);
        stmt.setString(4, Values.Transection.voucherTypeSale);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET BY CUSTOMER ID AND FROM DATE , TO DATE

    ////////////////********************* GET BY CUSTOMER ID AND DATE
    public ArrayList getTransectionByCustomerIdAndDate(Integer customerId, Timestamp date) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `customer_id` = ? AND `trans_date` < ? AND voucher_type != ? ORDER BY `trans_date`";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, customerId);
        stmt.setTimestamp(2, date);
        stmt.setString(3, Values.Transection.voucherTypeSale);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET BY CUSTOMER ID  DATE

    ////////////////********************* GET TRANSECTION RECEIPT
    public ArrayList getTransectionReceipt() throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `voucher_type` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setString(1, Values.Transection.voucherTypeReceipt);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION RECEIPT

    ////////////////********************* GET TRANSECTION RECEIPT BY STATUS
    public ArrayList getTransectionReceiptByStatus(String status) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `voucher_type` = ? AND status = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setString(1, Values.Transection.voucherTypeReceipt);
        stmt.setString(2, status);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION RECEIPT BY STATUS

    ////////////////********************* GET TRANSECTION RECEIPT BY PAYMENT TYPE
    public ArrayList getTransectionReceiptByPaymentType(String paymentType) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `voucher_type` = ? AND payment_type = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setString(1, Values.Transection.voucherTypeReceipt);
        stmt.setString(2, paymentType);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION RECEIPT BY PAYMENT TYPE

    ////////////////********************* GET TRANSECTION RECEIPT BY STATUS & PAYMENT TYPE
    public ArrayList getTransectionReceiptByStatusAndPaymentType(String status, String paymentType) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `voucher_type` = ? AND status = ? AND payment_type = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setString(1, Values.Transection.voucherTypeReceipt);
        stmt.setString(2, status);
        stmt.setString(3, paymentType);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION RECEIPT BY STATUS & PAYMENT TYPE

    ////////////////********************* GET TRANSECTION RECEIPT & CUSTOMER ID
    public ArrayList getTransectionReceiptByCustomerId(Integer customerId) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `voucher_type` = ? AND `customer_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setString(1, Values.Transection.voucherTypeReceipt);
        stmt.setInt(2, customerId);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION RECEIPT & CUSTOMER ID

    ////////////////********************* GET TRANSECTION RECEIPT BY STATUS & CUSTOMER ID
    public ArrayList getTransectionReceiptByStatusAndCustomerId(String status, Integer customerId) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `voucher_type` = ? AND status = ? AND `customer_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setString(1, Values.Transection.voucherTypeReceipt);
        stmt.setString(2, status);
        stmt.setInt(3, customerId);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION RECEIPT BY STATUS & CUSTOMER ID

    ////////////////********************* GET TRANSECTION RECEIPT BY PAYMENT TYPE & CUSTOMER ID
    public ArrayList getTransectionReceiptByPaymentTypeAndCustomerId(String paymentType, Integer customerId) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `voucher_type` = ? AND payment_type = ? AND `customer_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setString(1, Values.Transection.voucherTypeReceipt);
        stmt.setString(2, paymentType);
        stmt.setInt(3, customerId);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION RECEIPT BY PAYMENT TYPE & CUSTOMER ID

    ////////////////********************* GET TRANSECTION RECEIPT BY STATUS & PAYMENT TYPE & CUSTOMER ID
    public ArrayList getTransectionReceiptByStatusAndPaymentTypeAndCustomerId(String status, String paymentType, Integer customerId) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `voucher_type` = ? AND status = ? AND payment_type = ? AND `customer_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = Home.con.prepareStatement(query);
        stmt.setString(1, Values.Transection.voucherTypeReceipt);
        stmt.setString(2, status);
        stmt.setString(3, paymentType);
        stmt.setInt(4, customerId);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION RECEIPT BY STATUS & PAYMENT TYPE & CUSTOMER ID

    ////////////////********************* GET FULL TRANSECTION INQUIRY
    public ArrayList getFullTransectionInquiry(TransectionBean bean) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE ";

        Integer querySize = query.length();
        Byte counter = 0;

        /////***** TRANSECTION ID
        if(bean.getTransectionId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection_id` = ? ";
        }

        /////***** TRANS DATE
        if(bean.getTransDate() != null){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`trans_date` >= ? AND `trans_date` <= ? ";     //** TRANS DATE >= '2020-03-10 00:00:00' AND TRANS DATE <= '2020-03-10 23:59:59'
        }

        /////***** CUSTOMER ID
        if(bean.getCustomerId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`customer_id` = ? ";
        }

        /////***** USER ID
        if(bean.getUserId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`user_id` = ? ";
        }

        /////***** NAME
        if(!bean.getName().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`name` = ? ";
        }

        /////***** FROM GODOWN ID
        if(bean.getFromGodownId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`from_godown_id` = ? ";
        }

        /////***** TO GODOWN ID
        if(bean.getToGodownId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`to_godown_id` = ? ";
        }

        /////***** VOUCHER TYPE
        if(!bean.getVoucherType().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
           query += "`voucher_type` LIKE ? ";
        }

        /////***** PAYMENT TYPE
        if(!bean.getPaymentType().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`payment_type` LIKE ? ";
        }

        /////***** NO BOOLEAN HAS BILL IN HAND (has_bill_in_hand)
        /////***** NO FLOAT AMOUNTS (packing_expence, other_expence, discount) EXCEPT (total_amount)

        /////***** TOTAL AMOUNT
        if(bean.getTotalAmount() > 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`total_amount` = ? ";
        }

        /////***** STATUS
        if(!bean.getStatus().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`status` LIKE ? ";
        }

        /////***** REFERENCE BILL NO
        if(!bean.getReferenceBillNo().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`reference_bill_no` LIKE ? ";
        }

        /////***** TRANSPORT NAME
        if(!bean.getTransportName().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transport_name` LIKE ? ";
        }

        /////***** REMARKS
        if(!bean.getRemarks().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`remarks` LIKE ? ";
        }

        /////***** CONFIRMED BY
        if(!bean.getConfirmedBy().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`confirmed_by` LIKE ? ";
        }

        PreparedStatement stmt = Home.con.prepareStatement(query);
        System.out.println(query);

        /////***** TRASECTION ID
        if(bean.getTransectionId() != 0) stmt.setInt(++counter, bean.getTransectionId());
        /////***** TRANS DATE
        if(bean.getTransDate() != null){
            stmt.setTimestamp(++counter, bean.getTransDate());
            stmt.setTimestamp(++counter, Timestamp.valueOf(bean.getTransDate().toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
            System.out.println(bean.getTransDate());
            System.out.println(Timestamp.valueOf(bean.getTransDate().toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
        }
        /////***** CUSTOMER ID
        if(bean.getCustomerId() != 0) stmt.setInt(++counter, bean.getCustomerId());

        /////***** USER ID
        if(bean.getUserId() != 0) stmt.setInt(++counter, bean.getUserId());

        /////***** NAME
        if(!bean.getName().equals("")) stmt.setString(++counter, "%"+bean.getName()+"%");

        /////***** FROM GODOWN ID
        if(bean.getFromGodownId() != 0) stmt.setInt(++counter, bean.getFromGodownId());

        /////***** TO GODOWN ID
        if(bean.getToGodownId() != 0) stmt.setInt(++counter, bean.getToGodownId());

        /////***** VOUCHER TYPE
        if(!bean.getVoucherType().equals("")) stmt.setString(++counter, "%"+bean.getVoucherType()+"%");

        /////***** PAYMENT TYPE
        if(!bean.getPaymentType().equals("")) stmt.setString(++counter, "%"+bean.getPaymentType()+"%");

        /////***** NO BOOLEAN HAS BILL IN HAND (has_bill_in_hand)
        /////***** NO FLOAT AMOUNTS (packing_expence, other_expence, discount) EXCEPT (total_amount)

        /////***** TOTAL AMOUNT
        if(bean.getTotalAmount() != 0) stmt.setFloat(++counter, bean.getTotalAmount());

        /////***** STATUS
        if(!bean.getStatus().equals("")) stmt.setString(++counter, "%"+bean.getStatus()+"%");

        /////***** REFERENCE BILL NO
        if(!bean.getReferenceBillNo().equals("")) stmt.setString(++counter, "%"+bean.getReferenceBillNo()+"%");

        /////***** TRANSPORT NAME
        if(!bean.getTransportName().equals("")) stmt.setString(++counter, "%"+bean.getTransportName()+"%");

        /////***** REMARKS
        if(!bean.getRemarks().equals("")) stmt.setString(++counter, "%"+bean.getRemarks()+"%");

        /////***** CONFIRMED BY
        if(!bean.getConfirmedBy().equals("")) stmt.setString(++counter, bean.getConfirmedBy()+"%");

        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET FULL TRANSECTION INQUIRY



    ////////////////********************* GET TRANSECTION INQUIRY STOCK
    public ArrayList getTransectionInquiryStock(TransectionBean bean, Boolean hasBill) throws SQLException, RemoteException{
        String query = "SELECT `transection`.* FROM `transection`, `customers` WHERE ";

        Integer querySize = query.length();
        Byte counter = 0;

        /////***** TRANSECTION - CUSTOMER JOINING
        query += "`transection`.`customer_id` = `customers`.`customer_id` AND ";
        /////***** CUSTOMER TYPE
        query += "`customers`.`customer_type` = ? AND ";
        /////***** VOUCHER TYPE
        query += "`transection`.`voucher_type` = ? ";

        /////***** CUSTOMER ID
        if(bean.getCustomerId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`customer_id` = ? ";
        }

        /////***** HAS BILL IN HAND
        if(hasBill) {
            if (querySize != query.length()) { query += "AND "; querySize = query.length(); }
            query += "`transection`.`has_bill_in_hand` = ? ";
        }

        /////***** TRANSECTION ID
        if(bean.getTransectionId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`transection_id` = ? ";
        }

        /////***** TRANS DATE
        else if(bean.getTransDate() != null){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`trans_date` >= ? AND `transection`.`trans_date` <= ? ";     //** TRANS DATE >= '2020-03-10 00:00:00' AND TRANS DATE <= '2020-03-10 23:59:59'
        }

        /////***** REFERENCE BILL NO
        else if(!bean.getReferenceBillNo().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`reference_bill_no` LIKE ? ";
        }

        /////***** TRANSPORT NAME
        else if(!bean.getTransportName().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`transport_name` LIKE ? ";
        }

        /////***** CONFIRMED BY
        else if(!bean.getConfirmedBy().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`confirmed_by` LIKE ? ";
        }

        /////***** TOTAL AMOUNT
        else if(bean.getTotalAmount() > 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`total_amount` = ? ";
        }

        /////***** ORDER BY
        query += "ORDER BY `transection`.`trans_date`";

        PreparedStatement stmt = Home.con.prepareStatement(query);
        System.out.println(query);

        /////***** CUSTOMER TYPE
        stmt.setString(++counter, Values.Customer.SupplierType);
        /////***** VOUCHAR TYPE
        stmt.setString(++counter, Values.Transection.voucherTypeRegular);
        /////***** CUSTOMER ID
        if(bean.getCustomerId() != 0) stmt.setInt(++counter, bean.getCustomerId());
        /////***** HAS BILL IN HAND
        if(hasBill) stmt.setBoolean(++counter, bean.getHasBillInHand());
        /////***** TRANSECTION ID
        if(bean.getTransectionId() != 0) stmt.setInt(++counter, bean.getTransectionId());
        /////***** TRANS DATE
        else if(bean.getTransDate() != null){
            stmt.setTimestamp(++counter, bean.getTransDate());
            stmt.setTimestamp(++counter, Timestamp.valueOf(bean.getTransDate().toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
            System.out.println(bean.getTransDate());
            System.out.println(Timestamp.valueOf(bean.getTransDate().toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
        }
        /////***** REFERENCE BILL NO
        else if(!bean.getReferenceBillNo().equals("")) stmt.setString(++counter, bean.getReferenceBillNo()+"%");
        /////***** TRANSPORT NAME
        else if(!bean.getTransportName().equals("")) stmt.setString(++counter, bean.getTransportName()+"%");
        /////***** CONFIRMED BY
        else if(!bean.getConfirmedBy().equals("")) stmt.setString(++counter, bean.getConfirmedBy()+"%");
        /////***** TOTAL AMOUNT
        else if(bean.getTotalAmount() > 0) stmt.setFloat(++counter, bean.getTotalAmount());

        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION INQUIRY STOCK

    ////////////////********************* GET TRANSECTION INQUIRY SALE
    public ArrayList getTransectionInquirySale(TransectionBean bean) throws SQLException, RemoteException{
        String query = "SELECT `transection`.* FROM `transection`, `customers` WHERE ";

        Integer querySize = query.length();
        Byte counter = 0;

        /////***** TRANSECTION - CUSTOMER JOINING
        query += "`transection`.`customer_id` = `customers`.`customer_id` AND ";
        /////***** CUSTOMER TYPE
        query += "`customers`.`customer_type` = ? AND ";
        /////***** TRANSECTION VOUCHAR TYPE
        query += "(`transection`.`voucher_type` = ? OR `transection`.`voucher_type` = ?) ";


        /////***** CUSTOMER ID
        if(bean.getCustomerId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`customers`.`customer_id` = ? ";
        }

        /////***** TRANSECTION ID
        if(bean.getTransectionId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`transection_id` = ? ";
        }

        /////***** TRANS DATE
        else if(bean.getTransDate() != null){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`trans_date` >= ? AND `transection`.`trans_date` <= ? ";     //** TRANS DATE >= '2020-03-10 00:00:00' AND TRANS DATE <= '2020-03-10 23:59:59'
        }

        /////***** NAME
        else if(!bean.getName().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`name` LIKE ? ";
        }

        /////***** TRANSPORT NAME
        else if(!bean.getTransportName().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`transport_name` LIKE ? ";
        }

        /////***** PAYMENT TYPE
        else if(!bean.getPaymentType().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`payment_type` = ? ";
        }

        /////***** PACKING EXPENCE
        else if(bean.getPackingExpence() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`packing_expence` = ? ";
        }

        /////***** OTHER EXPENCE
        else if(bean.getOtherExpence() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`other_expence` = ? ";
        }

        /////***** DISCOUNT
        else if(bean.getDiscount() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`discount` = ? ";
        }

        /////***** TOTAL AMOUNT
        else if(bean.getTotalAmount() > 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`total_amount` = ? ";
        }

        /////***** CONFIRMED BY
        else if(!bean.getConfirmedBy().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection`.`confirmed_by` LIKE ? ";
        }

        /////***** ORDER BY
        query += "ORDER BY `transection`.`trans_date`";

        PreparedStatement stmt = Home.con.prepareStatement(query);
        System.out.println(query);

        /////***** CUSTOMER TYPE
        stmt.setString(++counter, Values.Customer.CustomerType);
        /////***** TRANSECTION VOUCHAR TYPE -> REGULAR
        stmt.setString(++counter, Values.Transection.voucherTypeRegular);
        /////***** TRANSECTION VOUCHAR TYPE -> SALE
        stmt.setString(++counter, Values.Transection.voucherTypeSale);

        /////***** CUSTOMER ID
        if(bean.getCustomerId() != 0) stmt.setInt(++counter, bean.getCustomerId());

        /////***** TRANSECTION ID
        if(bean.getTransectionId() != 0) stmt.setInt(++counter, bean.getTransectionId());
        /////***** TRANS DATE
        else if(bean.getTransDate() != null){
            stmt.setTimestamp(++counter, bean.getTransDate());
            stmt.setTimestamp(++counter, Timestamp.valueOf(bean.getTransDate().toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
            System.out.println(bean.getTransDate());
            System.out.println(Timestamp.valueOf(bean.getTransDate().toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
        }
        /////***** NAME
        else if(!bean.getName().equals("")) stmt.setString(++counter, bean.getName()+"%");
        /////***** TRANSPORT NAME
        else if(!bean.getTransportName().equals("")) stmt.setString(++counter, bean.getTransportName()+"%");
        /////***** PAYMENT TYPE
        else if(!bean.getPaymentType().equals("")) stmt.setString(++counter, bean.getPaymentType());
        /////***** PACKING EXPENCE
        else if(bean.getPackingExpence() != 0) stmt.setFloat(++counter, bean.getPackingExpence());
        /////***** OTHER EXPENCE
        else if(bean.getOtherExpence() != 0) stmt.setFloat(++counter, bean.getOtherExpence());
        /////***** DISCOUNT
        else if(bean.getDiscount() != 0) stmt.setFloat(++counter, bean.getDiscount());
        /////***** TOTAL AMOUNT
        else if(bean.getTotalAmount() > 0) stmt.setFloat(++counter, bean.getTotalAmount());
        /////***** CONFIRMED BY
        else if(!bean.getConfirmedBy().equals("")) stmt.setString(++counter, bean.getConfirmedBy()+"%");

        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION INQUIRY SALE

    ////////////////********************* GET TRANSECTION INQUIRY
    public ArrayList getTransectionInquiry(TransectionBean bean) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE ";

        Integer querySize = query.length();
        Byte counter = 0;

        /////***** CUSTOMER ID
        if(bean.getCustomerId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`customer_id` = ? ";
        }

        /////***** TRANSECTION ID
        if(bean.getTransectionId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection_id` = ? ";
        }

        /////***** TRANS DATE
        else if(bean.getTransDate() != null){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`trans_date` >= ? AND `trans_date` <= ? ";     //** TRANS DATE >= '2020-03-10 00:00:00' AND TRANS DATE <= '2020-03-10 23:59:59'
        }

        /////***** REFERENCE BILL NO
        else if(!bean.getReferenceBillNo().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`reference_bill_no` LIKE ? ";
        }

        /////***** TRANSPORT NAME
        else if(!bean.getTransportName().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transport_name` LIKE ? ";
        }

        /////***** CONFIRMED BY
        else if(!bean.getConfirmedBy().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`confirmed_by` LIKE ? ";
        }

        /////***** TOTAL AMOUNT
        else if(bean.getTotalAmount() > 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`total_amount` = ? ";
        }


        PreparedStatement stmt = Home.con.prepareStatement(query);
        System.out.println(query);

        /////***** CUSTOMER ID
        if(bean.getCustomerId() != 0) stmt.setInt(++counter, bean.getCustomerId());
        /////***** TRANSECTION ID
        if(bean.getTransectionId() != 0) stmt.setInt(++counter, bean.getTransectionId());
            /////***** TRANS DATE
        else if(bean.getTransDate() != null){
            stmt.setTimestamp(++counter, bean.getTransDate());
            stmt.setTimestamp(++counter, Timestamp.valueOf(bean.getTransDate().toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
            System.out.println(bean.getTransDate());
            System.out.println(Timestamp.valueOf(bean.getTransDate().toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
        }
        /////***** REFERENCE BILL NO
        else if(!bean.getReferenceBillNo().equals("")) stmt.setString(++counter, bean.getReferenceBillNo()+"%");
            /////***** TRANSPORT NAME
        else if(!bean.getTransportName().equals("")) stmt.setString(++counter, bean.getTransportName()+"%");
            /////***** CONFIRMED BY
        else if(!bean.getConfirmedBy().equals("")) stmt.setString(++counter, bean.getConfirmedBy()+"%");
            /////***** TOTAL AMOUNT
        else if(bean.getTotalAmount() > 0) stmt.setFloat(++counter, bean.getTotalAmount());

        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION INQUIRY

    ////////////////********************* GET TRANSECTION INQUIRY GODOWN
    public ArrayList getTransectionInquiryGodown(TransectionBean bean) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE ";

        Integer querySize = query.length();
        Byte counter = 0;

        /////***** TRANSECTION VOUCHAR TYPE
        query += "(`voucher_type` = ? OR `voucher_type` = ?) ";


        /////***** TRANSECTION ID
        if(bean.getTransectionId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transection_id` = ? ";
        }

        /////***** TRANS DATE
        else if(bean.getTransDate() != null){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`trans_date` >= ? AND `trans_date` <= ? ";     //** TRANS DATE >= '2020-03-10 00:00:00' AND TRANS DATE <= '2020-03-10 23:59:59'
        }

        /////***** FROM GODOWN ID
        else if(bean.getFromGodownId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`from_godown_id` = ? ";
        }

        /////***** TO GODOWN ID
        else if(bean.getToGodownId() != 0){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`to_godown_id` = ? ";
        }

        /////***** STATUS
        else if(!bean.getStatus().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`status` LIKE ? ";
        }

        /////***** REFERENCE BILL NO
        else if(!bean.getReferenceBillNo().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`reference_bill_no` LIKE ? ";
        }

        /////***** TRANSPORT NAME
        else if(!bean.getTransportName().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`transport_name` LIKE ? ";
        }

        /////***** CONFIRMED BY
        else if(!bean.getConfirmedBy().equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`confirmed_by` LIKE ? ";
        }

        PreparedStatement stmt = Home.con.prepareStatement(query);
        System.out.println(query);

        /////***** TRANSECTION VOUCHAR TYPE -> GODOWN TO GODOWN
        stmt.setString(++counter, Values.Transection.voucherTypeGodownToGodown);
        /////***** TRANSECTION VOUCHAR TYPE -> GODOWN IN
        stmt.setString(++counter, Values.Transection.voucherTypeGodownIn);

        /////***** TRANSECTION ID
        if(bean.getTransectionId() != 0) stmt.setInt(++counter, bean.getTransectionId());
        /////***** TRANS DATE
        else if(bean.getTransDate() != null){
            stmt.setTimestamp(++counter, bean.getTransDate());
            stmt.setTimestamp(++counter, Timestamp.valueOf(bean.getTransDate().toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
            System.out.println(bean.getTransDate());
            System.out.println(Timestamp.valueOf(bean.getTransDate().toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
        }
        /////***** FROM GODOWN ID
        else if(bean.getFromGodownId() != 0) stmt.setInt(++counter, bean.getFromGodownId());
        /////***** TO GODOWN ID
        else if(bean.getToGodownId() != 0) stmt.setInt(++counter, bean.getToGodownId());
        /////***** STATUS
        else if(!bean.getStatus().equals("")) stmt.setString(++counter, bean.getStatus()+"%");
        /////***** REFERENCE BILL NO
        else if(!bean.getReferenceBillNo().equals("")) stmt.setString(++counter, bean.getReferenceBillNo()+"%");
        /////***** TRANSPORT NAME
        else if(!bean.getTransportName().equals("")) stmt.setString(++counter, bean.getTransportName()+"%");
        /////***** CONFIRMED BY
        else if(!bean.getConfirmedBy().equals("")) stmt.setString(++counter, bean.getConfirmedBy()+"%");

        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION INQUIRY GODOWN




    ////////////////********************* BEAN
    ////////////////********************* GET TRANSECTION BEAN BY TRANSECTION ID
    public TransectionBean getTransectionBeanByTransectionId(Integer transectionId) throws SQLException, RemoteException{
        String query = "SELECT * FROM `transection` WHERE `transection_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, transectionId);
        return getTransectionBean_GeneralMethod(stmt);
    }
    ////////////////********************* GET BY CUSTOMER ID  DATE


    ////////////////********************* JOINING
    ////////////////********************* GET TRANSECTION BY CUSTOMER ID & PRODUCT ID
    public ArrayList getTransectionJoiningByCustomerIdAndProductId(Integer customerId, Integer productId) throws SQLException, RemoteException{
        String query = "SELECT `transection`.* FROM `transection`, `product_transection`, `products` " +
                "WHERE `transection`.`transection_id` = `product_transection`.`transection_id` AND " +
                "`products`.`product_id` = `product_transection`.`product_id` AND " +
                "`transection`.`customer_id` = ? AND " +
                "`products`.`product_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;
        stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, customerId);
        stmt.setInt(2, productId);
        return getTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET TRANSECTION BY CUSTOMER ID & PRODUCT ID


    ////////////////********************* SINGLE VALUE
    ////////////////********************* GET SUM TRANSECTION & STATUS TOTAL AMOUNT
    public Float getSumOfAmountByVoucherTypeAndStatus(Timestamp transDate, String voucherType, String status)throws SQLException, RemoteException{
        PreparedStatement stmt = null;
        ResultSet set = null;
        Float amount = Float.valueOf(0);
        String query = "SELECT SUM(total_amount) AS 'total_amount' FROM `transection` WHERE ";

        Integer querySize = query.length();
        Byte counter = 0;

        /////***** TRANS DATE
        if(transDate != null){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`trans_date` >= ? AND `trans_date` <= ? ";     //** TRANS DATE >= '2020-03-10 00:00:00' AND TRANS DATE <= '2020-03-10 23:59:59'
        }

        /////***** VOUCHER TYPE
        if(!voucherType.equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`voucher_type` LIKE ? ";
        }

        /////***** STATUS
        if(!status.equals("")){
            if(querySize != query.length()){ query += "AND "; querySize = query.length(); }
            query += "`status` LIKE ? ";
        }


        try{
            stmt = Home.con.prepareStatement(query);
            System.out.println(query);

            /////***** TRANS DATE
            if(transDate != null){
                stmt.setTimestamp(++counter, transDate);
                stmt.setTimestamp(++counter, Timestamp.valueOf(transDate.toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)));
            }

            /////***** VOUCHER TYPE
            if(!voucherType.equals("")) stmt.setString(++counter, voucherType);

            /////***** STATUS
            if(!status.equals("")) stmt.setString(++counter, status);

            set = stmt.executeQuery();
            if(set.next()) amount = set.getFloat("total_amount");

            return amount;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }

    ////////////////********************* TRANSECTION METHODS ********************************//////////////////










    ////////////////********************* PRODUCT TRANSECTION METHODS ********************************//////////////////

    ////////////////********************* GET PRODUCT TRANSECTION GENERAL METHOD
    private ArrayList getProductTransection_GeneralMethod(PreparedStatement stmt)throws SQLException, RemoteException{
        ResultSet set = null;
        ArrayList<ProductTransectionBean> list = null;

        try{
            set = stmt.executeQuery();

            if(set != null){
                list = new ArrayList<>();
                while(set.next()){
                    ProductTransectionBean bean = new ProductTransectionBean();
                    bean.setTransectionId(set.getInt("transection_id"));
                    bean.setProductId(set.getInt("product_id"));
                    bean.setGodownId(set.getInt("godown_id"));
                    bean.setQuantity(set.getFloat("quantity"));
                    bean.setUnitPrice(set.getFloat("unit_price"));
                    bean.setAmount(set.getFloat("amount"));
                    list.add(bean);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET PRODUCT TRANSECTION GENERAL METHOD

    ////////////////********************* INSERT
    public int insertProductTransection(ProductTransectionBean bean)throws SQLException, RemoteException{
        String query = "insert into product_transection(transection_id, product_id, godown_id, quantity, unit_price, amount) values(?,?,?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, bean.getTransectionId());
            stmt.setInt(2, bean.getProductId());
            stmt.setInt(3, bean.getGodownId());
            stmt.setFloat(4, bean.getQuantity());
            stmt.setFloat(5, bean.getUnitPrice());
            stmt.setFloat(6, bean.getAmount());
            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT

    ////////////////********************* UPDATE
    ////////////////********************* UPDATE

    ////////////////********************* DELETE PRODUCT TRANSECTION BY TRANSECTION ID
    public Integer deleteProductTransection(Integer transectionId)throws SQLException, RemoteException{
        String query = "DELETE FROM `product_transection` WHERE `transection_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;

        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, transectionId);
            return stmt.executeUpdate();
        }
        finally{ if(stmt != null) stmt.close(); }
    }
    ////////////////********************* DELETE PRODUCT TRANSECTION BY TRANSECTION ID

    ////////////////********************* GET PRODUCT TRANSECTION BY TRANSECTION ID
    public ArrayList getProductTransectionByTransectionId(Integer transectionId) throws SQLException, RemoteException{
        String query = "SELECT * FROM product_transection WHERE transection_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;

        stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, transectionId);
        return getProductTransection_GeneralMethod(stmt);
    }
    ////////////////********************* GET PRODUCT TRANSECTION BY TRANSECTION ID

    ////////////////********************* PRODUCT TRANSECTION METHODS ********************************//////////////////









    ////////////////********************* DETERMINATION METHODS ********************************//////////////////

    ////////////////********************* GET DETERMINATION GENERAL METHOD
    private ArrayList getDetermination_GeneralMethod(PreparedStatement stmt)throws SQLException, RemoteException{
        ResultSet set = null;
        ArrayList<DeterminationBean> list = null;
        try{
            set = stmt.executeQuery();

            if(set != null){
                list = new ArrayList<>();
                while(set.next()){
                    DeterminationBean bean = new DeterminationBean();
                    bean.setTransectionId(set.getInt("transection_id"));
                    bean.setProductId(set.getInt("product_id"));
                    bean.setNoOfPack(set.getFloat("no_of_pack"));
                    bean.setQuantity(set.getFloat("quantity"));
                    list.add(bean);
                }
            }
            return list;
        }
        finally{
            if(set != null) set.close();
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* GET DETERMINATION GENERAL METHOD

    ////////////////********************* INSERT
    public int insertDetermination(DeterminationBean bean)throws SQLException, RemoteException{
        String query = "insert into determination(transection_id, product_id, no_of_pack, quantity) values(?,?,?,?)";
        System.out.println(query);
        PreparedStatement stmt = null;
        int row = 0;
        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, bean.getTransectionId());
            stmt.setInt(2, bean.getProductId());
            stmt.setFloat(3, bean.getNoOfPack());
            stmt.setFloat(4, bean.getQuantity());
            row = stmt.executeUpdate();
            return row;
        }
        finally{
            if(stmt != null) stmt.close();
        }
    }
    ////////////////********************* INSERT

    ////////////////********************* UPDATE
    ////////////////********************* UPDATE

    ////////////////********************* DELETE DETERMINATION BY TRANSECTION ID
    public Integer deleteDetermination(Integer transectionId)throws SQLException, RemoteException{
        String query = "DELETE FROM `determination` WHERE `transection_id` = ?";
        System.out.println(query);
        PreparedStatement stmt = null;

        try{
            stmt = Home.con.prepareStatement(query);
            stmt.setInt(1, transectionId);
            return stmt.executeUpdate();
        }
        finally{ if(stmt != null) stmt.close(); }
    }
    ////////////////********************* DELETE DETERMINATION BY TRANSECTION ID

    ////////////////********************* GET DETERMINATION BY TRANSECTION ID
    public ArrayList getDeterminationByTransectionIdAndProductId(Integer transectionId, Integer productId) throws SQLException, RemoteException{
        String query = "SELECT * FROM determination WHERE transection_id = ? AND product_id = ?";
        System.out.println(query);
        PreparedStatement stmt = null;

        stmt = Home.con.prepareStatement(query);
        stmt.setInt(1, transectionId);
        stmt.setInt(2, productId);
        return getDetermination_GeneralMethod(stmt);
    }
    ////////////////********************* GET DETERMINATION BY TRANSECTION ID


    ////////////////********************* DETERMINATION METHODS ********************************//////////////////

}
