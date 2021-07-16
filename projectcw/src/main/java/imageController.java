import com.sun.javaws.security.Resource;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.security.PublicKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class imageController implements Initializable {

    public ChoiceBox questionBox;
    public ChoiceBox imageOptionsChoice;
    public Button selectImageButton;
    public TextField imageNameField;
    public TextField imageCaptionField;
    public File image;
    public Button imageResetButton;
    public Button imageSubmitButton;
    public Button viewImageButton;
    public ImageView imageDisplay;
    public Button deleteImageButton;
    public Button backButton;


    public void selectImage() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(selectImageButton.getScene().getWindow());
        image = selectedFile;
        //Allows user to select image file and sets their selected file to image attribute
    }


    public void submitImage() throws IOException, SQLException {
        if (imageNameField.getText().trim().isEmpty()){
            AlertBox.display("Error","Make sure you have given the image a name");
        }
        //Ensures name provided to image file and informs user if not done so

        if (image!=null) {

            String mimetype = Files.probeContentType(image.toPath());

            if (!(mimetype != null && mimetype.split("/")[0].equals("image"))) {
                AlertBox.display("Error","This is not an image file");
                return;
            }
            //Checks if file selected is an image file. If not, informs user with AlertBox and allows them to select a new file
            String type = image.getPath();
            int i = type.lastIndexOf('.');
            if (i > 0) {
                type = type.substring(i+1);
            }
            type="."+type;

            String name ="src/main/resources/images/"+imageNameField.getText()+type;
            //Selects filepath for image
            File newImage = new File(name);
            Files.copy(image.toPath(),newImage.toPath());
            //Image stored
            Statement imageStatement = Main.db.a.createStatement();

            String temp = (String) questionBox.getSelectionModel().getSelectedItem();
            int questionID= Integer.parseInt(temp.substring(temp.indexOf('(')+1,temp.indexOf(')')));
            String caption = imageCaptionField.getText();
            //Image metadata set


            String imageQuery = "INSERT INTO public.image(imageid, questionid, image, caption) VALUES (nextval('image_sequence'),'"+questionID+"','"+"src/main/resources/images/"+imageNameField.getText()+type+"','"+caption+"');";
            imageStatement.executeUpdate(imageQuery);
            AlertBox.display("Submit!","Image submitted successfully!");
            //Image details added to system
            imageStatement.close();
            resetFields();
            fillComboBoxes();


        }
        else {
            AlertBox.display("Error","Make sure an image has been selected");
        }
        //Ensures file has been selected

    }
    public void resetFields(){
        image = null;
        imageCaptionField.clear();
        imageNameField.clear();
        questionBox.getItems().clear();
        //Resets all fields

    }




    private void fillComboBoxes() throws SQLException {
        questionBox.getItems().clear();
        imageOptionsChoice.getItems().clear();
        Statement questionStatement = Main.db.a.createStatement();
        Statement imageStatement = Main.db.a.createStatement();

        String questionQuery = "SELECT module_code,exam_year,exam_title,question_number,questionid FROM (question INNER JOIN exam ON exam.examid = question.examid) INNER JOIN module on exam.moduleid=module.moduleid;";
        String imageQuery = "SELECT module_code,exam_year,exam_title,question_number,imageid FROM ((image INNER JOIN question ON image.questionid = question.questionid) INNER JOIN exam on exam.examid=question.examid)inner join module on module.moduleid = exam.moduleid;";
        ResultSet questionR = questionStatement.executeQuery(questionQuery);
        ResultSet imageR = imageStatement.executeQuery(imageQuery);

        while(questionR.next()){
            String temp = "";
            temp = temp+(questionR.getString("module_code"))+"/";
            temp = temp+(questionR.getInt("exam_year"))+"/";
            temp = temp+(questionR.getString("exam_title"))+"/";
            temp = temp+(questionR.getString("question_number"))+" ";
            temp = temp+"("+(questionR.getString("questionid"))+")";
            questionBox.getItems().add(temp);
        }
        questionR.close();
        questionStatement.close();
        //Selects questions and adds them to question box with module and exam paper information
        while (imageR.next()){
            String temp = "";
            temp = temp+(imageR.getString("module_code"))+"/";
            temp = temp+(imageR.getInt("exam_year"))+"/";
            temp = temp+(imageR.getString("exam_title"))+"/";
            temp = temp+(imageR.getString("question_number"))+"/";
            temp = temp+(imageR.getInt("imageid"));
            imageOptionsChoice.getItems().add(temp);
        }
        imageR.close();
        imageStatement.close();
        //Gets image information and adds it to the apppropriate choicebox
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.addBackButtonImage(backButton,"back.png");
        try {
            fillComboBoxes();
            //Fills all comboboxes

            imageDisplay.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
                if (oldScene == null && newScene != null) {
                    imageDisplay.fitWidthProperty().bind(newScene.widthProperty());}});
            //Ensures when image added it fits the allocated space

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void viewImage(ActionEvent actionEvent) throws SQLException, URISyntaxException {
        String filePath = null;
        String temp = (String) imageOptionsChoice.getSelectionModel().getSelectedItem();
        int i = temp.lastIndexOf('/');
        int imageID = Integer.parseInt(temp.substring(i+1,temp.length()));
        //Gets imageID of selected image
        Statement imageStatement = Main.db.a.createStatement();
        String imageQuery = "SELECT image FROM image WHERE imageid ="+imageID+";";
        ResultSet imageR = imageStatement.executeQuery(imageQuery);

        while (imageR.next()){
            filePath = imageR.getString("image");
        }
        //Gets image path
        File file = new File(filePath);
        System.out.println(file.toURI().toString());
        Image imagea = new Image(file.toURI().toString());
        imageDisplay.setImage(imagea);
        //Displays image
    }
    public void deleteImage(ActionEvent actionEvent) throws SQLException {

        Boolean deleteBool = ConfirmBox.display("Delete Image","Are you sure you want to delete this image?");
        if (deleteBool == Boolean.TRUE){
        //Ensures user wants to delete image
            String temp = (String) imageOptionsChoice.getSelectionModel().getSelectedItem();
            int i = temp.lastIndexOf('/');
            int imageID = Integer.parseInt(temp.substring(i+1));
            Statement imageStatement = Main.db.a.createStatement();
            String imageQuery = "SELECT image FROM image WHERE imageid ="+imageID+";";
            ResultSet imageR = imageStatement.executeQuery(imageQuery);
            String filePath = null;
            while (imageR.next()){
                filePath = imageR.getString("image");
            }
            //Gets image path

            try {
                Files.delete(Paths.get(filePath));
            //Deletes image file from device
                String deleteQuery = "DELETE FROM image WHERE imageid ="+imageID+";";
                imageStatement.executeUpdate(deleteQuery);
            //Deletes Image informtation from database
            } catch (NoSuchFileException x) {
                System.err.format("no such file called", filePath);
            } catch (DirectoryNotEmptyException x) {
                System.err.format("DirectoryNotEmptyException", filePath);
            } catch (IOException x) {
                System.err.println(x);
            }


        }
        resetFields();
        fillComboBoxes();

    }

    public void back(ActionEvent actionEvent) throws IOException {
        Parent mainMenuParent  = FXMLLoader.load(getClass().getResource("mainMenuView.fxml"));
        Scene mainMenuScene = new Scene(mainMenuParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
        window.show();
        //Allows user to access previous page
    }
}
