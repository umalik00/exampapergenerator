import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class openingPageController implements Initializable {

    public Button signupButton;
    public Button loginButton;

    public void logInButtonClick(ActionEvent actionEvent) throws IOException {
        Parent loginViewParent  = FXMLLoader.load(getClass().getResource("loginView.fxml"));
        Scene loginViewScene = new Scene(loginViewParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginViewScene);
        window.show();

        //Allows user to access log in page

    }
    public void signUpClick(ActionEvent actionEvent) throws IOException {
        Parent signupViewParent  = FXMLLoader.load(getClass().getResource("signUpView.fxml"));
        Scene signupViewScene = new Scene(signupViewParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(signupViewScene);
        window.show();

        //Allows user to access sign up page
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.addBackButtonImage(signupButton,"signup.png");
        Main.addBackButtonImage(loginButton,"login.png");
        //Adds images to buttons
    }
}
