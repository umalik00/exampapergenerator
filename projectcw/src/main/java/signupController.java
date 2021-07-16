import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class signupController implements Initializable {
    public TextField signupFullnameField;
    public TextField signupEmailField;
    public PasswordField signupPasswordField;
    public PasswordField signupConfirmPasswordField;
    public TextField signupMemorableWordField;
    public Button signupBackButton;
    public Button signupSignupButton;


    public void signUp(ActionEvent actionEvent) throws SQLException, IOException {
        Boolean auth = signupPasswordField.getText().equals(signupConfirmPasswordField.getText());
        if (auth == false){
            AlertBox.display("Error","Passwords do not match!");
            return;
        }
        //Ensures passwords inputted match

        Statement s = Main.db.a.createStatement();
        String emailCheck = "SELECT user_email FROM public.user where user_email = '"+signupEmailField.getText()+"'";
        ResultSet r = s.executeQuery(emailCheck);
        if(r.next())
        {
            AlertBox.display("Error","User already exists with the same email address!");
            return;
        }
        //Ensure duplicate accounts are not created

        if(signupPasswordField.getText().length()<8){
            AlertBox.display("Error","Password required to be 8 characters or more");
            return;
        }
        //Ensures password is long enough
        if (!signupEmailField.getText().contains("@")||!signupEmailField.getText().contains(".")) {
            AlertBox.display("Error","Ensure email address is valid");
            return;
        }
        //Ensures email is of valid format
        if(signupPasswordField.getText().isEmpty()||signupMemorableWordField.getText().isEmpty()||signupFullnameField.getText().isEmpty()||signupEmailField.getText().isEmpty()){
            AlertBox.display("Error","Make sure every field is has been filled! ");
            return;
        }
        //Ensures every field filled

        String submit= "INSERT INTO public.user (userid,fullname,user_email,user_password,user_word)"+" VALUES (nextval('user_sequence'),'"+signupFullnameField.getText()+"', '"+signupEmailField.getText()+"','"+signupPasswordField.getText()+"','"+signupMemorableWordField.getText()+"')";


        Statement u = Main.db.a.createStatement();
        u.executeUpdate(submit);
        //User created
        ResultSet lastValue = u.executeQuery("SELECT last_value FROM user_sequence;");
        int currentUserID = 0;
        while (lastValue.next()) {
            currentUserID = lastValue.getInt("last_value");
            System.out.println(currentUserID);
        }
        ResultSet setterValue = u.executeQuery("SELECT nextval('setter_sequence');");
        int temp = 0;
        while (setterValue.next()) {
            temp = setterValue.getInt("nextval");
            temp=temp+1;
        }
        System.out.println(temp);
        String checker = "INSERT INTO public.checker (checkerid,userid)"+" VALUES (nextval('checker_sequence'),"+currentUserID+")";
        String setter = "INSERT INTO public.setter (setterid,userid)"+" VALUES ("+ temp+","+currentUserID+")";

        //Checker and setter data assigned to new user

        u.executeUpdate(checker);
        u.executeUpdate(setter);
        u.close();
        AlertBox.display("Welcome!","Sign Up successful!");
        PostgreSQLJDBC.user = signupFullnameField.getText();
        Parent mainMenuParent  = FXMLLoader.load(getClass().getResource("mainMenuView.fxml"));
        Scene mainMenuScene = new Scene(mainMenuParent);

        //If user successfully creates an account, they are informed so and the system goes to the main menu

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
        window.show();
    }
    public void back(ActionEvent actionEvent) throws IOException {
        Parent loginViewParent  = FXMLLoader.load(getClass().getResource("openingPageView.fxml"));
        Scene loginViewScene = new Scene(loginViewParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginViewScene);
        window.show();
        //Allows user to go back to opening page
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.addBackButtonImage(signupBackButton,"back.png");
        Main.addBackButtonImage(signupSignupButton,"signup.png");
        //Sets images to buttons

    }
}
