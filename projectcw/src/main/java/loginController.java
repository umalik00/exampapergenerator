import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class loginController implements Initializable {
    public Button loginLoginButton;
    public TextField loginUsernameField;
    public PasswordField logInPasswordField;
    public Button loginBackButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.addBackButtonImage(loginBackButton,"back.png");
        Main.addBackButtonImage(loginLoginButton,"login.png");
        //Sets image to buttons

        logInPasswordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    loginLoginButton.fire();
                }
            }
            //Allows user to press Enter to login
        });
        loginBackButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    loginBackButton.fire();
                }
            }
            //Allows user to press Enter to login
        });
        loginUsernameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    loginLoginButton.fire();
                }
            }
            //Allows user to press Enter to login
        });
        loginLoginButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    loginLoginButton.fire();
                }
            }
            //Allows user to press Enter to login
        });
    }

    public void backButtonClick(ActionEvent actionEvent) throws IOException {
        Parent loginViewParent  = FXMLLoader.load(getClass().getResource("openingPageView.fxml"));
        Scene loginViewScene = new Scene(loginViewParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginViewScene);
        window.show();
        //Returns user to opening page
    }
    public void loginButtonClick(ActionEvent actionEvent) throws IOException {
        Boolean auth = Main.db.login(loginUsernameField.getText(),logInPasswordField.getText());
        if(auth==false){
            AlertBox.display("Error","Username or password incorrect");
        }
        //Alerts user of incorrect login details
        else{
            AlertBox.display("Welcome","Welcome back!");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainMenuView.fxml"));
        //Informs user of log in success and advances system to main menu
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

            window.setScene(scene);

            window.show();

        }

    }
    public void forgotPassword(ActionEvent actionEvent) {
        ForgotPassword.display();
        //Displays forgot password window

    }
}
