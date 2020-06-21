package sample;

import Controllers.Home;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.server.UnicastRemoteObject;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/home.fxml"));
        primaryStage.setTitle("Server Control Panel");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        closeConnections();
        System.exit(0);
    }

    public static void closeConnections() throws Exception{
        if(Home.con != null) Home.con.close();
        if(Home.reg != null) {
            Home.reg.unbind("rmi://" + Home.rmiIP + "/inventoryDatabase");
            UnicastRemoteObject.unexportObject(Home.reg, true);
        }
    }
}
