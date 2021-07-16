import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.geometry.Pos;


public class ConfirmBox {

    public static boolean answer;
    //Attribute that will be used to inform the system what option was chosen

    public static boolean display(String title, String message){    //Allows ConfirmBox to be used in a variety of situations
        Stage aStage = new Stage();
        aStage.initModality(Modality.APPLICATION_MODAL);
        aStage.setTitle(title);
        aStage.setMinWidth(300);
        Label messageLabel = new Label(message);
        //Sets stage dimensions and status of the ConfirmBox

        Button trueButton = new Button("Yes");
        Button falseButton = new Button("No");
        //Defines buttons to be used in ConfirmBox
        trueButton.setOnAction(e->{
            answer=true;
            System.out.println("true");
            aStage.close();
        });
        falseButton.setOnAction(e->{
            answer=false;
            System.out.println("false");
            aStage.close();
        });
        //Sets what will occur when either button is clicked
        VBox aVBox = new VBox(15);
        aVBox.getChildren().addAll(messageLabel,trueButton,falseButton);
        aVBox.setAlignment(Pos.CENTER);
        //Sets UI elemets within ConfirmBox
        aStage.setScene(new Scene(aVBox));
        aStage.showAndWait();
        return answer;
        //Displays ConfirmBox and returns what boolean value was chosen by the user
    }
}
