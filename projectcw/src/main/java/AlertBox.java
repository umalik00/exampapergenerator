import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {
    public static void display(String title,String message){    //Allows ConfirmBox to be used in a variety of situations
        Stage aStage = new Stage();
        aStage.initModality(Modality.APPLICATION_MODAL);
        aStage.setTitle(title);
        aStage.setMinWidth(300);
        //Sets stage dimensions and status

        Label messageLabel = new Label(message);
        Button closeButton = new Button("OK");
        //Defines UI elements of Alertbox
        closeButton.setOnAction(e->{
            aStage.close();
        } );

        closeButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    aStage.close();
                }
            }
        });
        //Sets what occurs on actions relating with UI elements
        VBox aVbox = new VBox(15);
        aVbox.getChildren().addAll(messageLabel,closeButton);
        aVbox.setAlignment(Pos.CENTER);
        //Sets UI elemets within AlertBox

        aStage.setScene(new Scene(aVbox));
        aStage.showAndWait();
        //Displays AlertBox
    }
}
