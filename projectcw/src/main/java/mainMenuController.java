import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class mainMenuController implements Initializable{
    public Text test;
    public Button mainMenuLogOffButton;
    public Button modulesButton;
    public Button questionsButton;
    public Button examsButton;
    public Button imagesButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.addBackButtonImage(mainMenuLogOffButton,"logoff.png");
        Platform.runLater(() ->{
            test.setText("Welcome "+Main.db.user+"!");
            //Gathers user name and adds it to main menu welcome page
        });
    }


    public void logoff(ActionEvent actionEvent) {
        boolean answer = ConfirmBox.display("Close","Are you sure?");
        if (answer == true){
            Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            window.close();
        }
        //Confirms user wants to log off and does so by closing the system

    }

    public void openModules(ActionEvent actionEvent) throws IOException {
        Parent moduleViewParent  = FXMLLoader.load(getClass().getResource("modulesView.fxml"));
        Scene moduleViewScene = new Scene(moduleViewParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(moduleViewScene);
        window.show();
        //Opens module page
    }

    public void openQuestions(ActionEvent actionEvent) throws IOException {
        Parent questionsViewParent  = FXMLLoader.load(getClass().getResource("questionView.fxml"));
        Scene questionsViewScene = new Scene(questionsViewParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(questionsViewScene);
        window.show();
        //Opens questions page
    }

    public void openExams(ActionEvent actionEvent) throws IOException {
        Parent examsViewParent  = FXMLLoader.load(getClass().getResource("examsView.fxml"));
        Scene examsViewScene = new Scene(examsViewParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(examsViewScene);
        window.show();
        //Opens exams page
    }

    public void openImages(ActionEvent actionEvent) throws IOException {
        Parent imagesViewParent  = FXMLLoader.load(getClass().getResource("imageView.fxml"));
        Scene imagesViewScene = new Scene(imagesViewParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(imagesViewScene);
        window.show();
        //Opens images page
    }
}

