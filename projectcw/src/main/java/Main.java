import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    static PostgreSQLJDBC db;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        db = new PostgreSQLJDBC();
        db.main(null);
        Parent root = FXMLLoader.load(getClass().getResource("openingPageView.fxml"));
        primaryStage.setTitle("Exam Paper Generating System");
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.show();
        //Sets window information and displays opening page

    }
    public static void addBackButtonImage(Button b,String image){
        File file = new File("src/main/resources/images/"+image);
        Image imagea = new Image(file.toURI().toString(),24,24,true,true);
        b.setGraphic(new ImageView(imagea));
        //Method to set icon/image to button
    }




}
