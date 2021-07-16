import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class ForgotPassword {
    public static boolean passwordBool;

    public static void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        window.setMinWidth(200);
        Label label = new Label();
        label.setText("Enter Details");
        TextField emailField = new TextField();
        TextField wordField = new TextField();
        emailField.promptTextProperty().set("Email");
        wordField.promptTextProperty().set("Memorable Word");
        Button enterButton = new Button("Enter");
        //Defines UI fields
        enterButton.setOnAction(e -> {
            try {
                Statement s = Main.db.a.createStatement();
                String users = "SELECT fullname, user_email, user_password, user_word FROM public.user WHERE user_word = '" + wordField.getText() + "' AND user_email = '" + emailField.getText() + "';";
                ResultSet resultSet = s.executeQuery(users);
                //Checks if user has inputted the correct details (email matches their chosen word
                if (resultSet.next()) {
                    AlertBox.display("Correct Details", "We have found your account. Submit a new password!");
                    newPassword(wordField.getText(),emailField.getText());
                    passwordBool = true;
                    return;
                    //If so, allows them to create a new password
                }
                else {
                    AlertBox.display("Incorrect Details","Account not found with corresponding details");
                    passwordBool = false;
                    return;
                    //If not, informs them so
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, emailField, wordField, enterButton);
        layout.setAlignment(Pos.CENTER);
        //Sets UI elements within Forgot Password window
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        //Displays window
    }


    private static void newPassword(String word, String email) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(200);
        Label label = new Label();
        label.setText("Enter new Password");
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        passwordField.promptTextProperty().set("Password");
        confirmPasswordField.promptTextProperty().set("Confirm Password");
        Button enterButton = new Button("Enter");

        //Defines UI elements within new password window

        enterButton.setOnAction(e -> {
            if(passwordField.getText().equals(confirmPasswordField.getText())&& passwordField.getText().length()>=8){
                //Ensures inputted passwords match and has 8+ characters
                try {
                    Statement s = null;
                    s = Main.db.a.createStatement();
                    String modules = "UPDATE public.user SET user_password='"+passwordField.getText()+"' WHERE user_email='"+email+"' AND user_word = '"+word+"';";
                    s.executeUpdate(modules);
                    AlertBox.display("Password Changed","Your password has been successfully changed!");
                    return;
                    //Updates password and informs user of change with an Alert Box

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            else{
                AlertBox.display("New Password", "Make sure passwords match and is at least eight characters!");
            }
            //Asks user to pick new password that matches requirements
        });
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, passwordField, confirmPasswordField, enterButton);
        layout.setAlignment(Pos.CENTER);
        //Sets UI elemets within window
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        //Displays window
    }
}

