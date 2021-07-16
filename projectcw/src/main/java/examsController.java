import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.UnaryOperator;

public class examsController implements Initializable {
    public ComboBox examViewModuleCombo;
    public ComboBox examViewSetterCombo;
    public ComboBox examViewMarkerCombo;
    public Button submitButton;
    public ComboBox viewModuleComboBox;
    public TextField titleField;
    public TextField yearField;
    public List<String> moduleArray;
    public List<String> userArray;
    public TableColumn examYearColumn;
    public TableColumn examModuleColumn;
    public TableColumn examTitleColumn;
    public TableView examsTable;
    public Button examsViewFilterButton;
    public Button examsViewResetButton;
    public TextField yearTextField;
    public Button backButton;
    private ObservableList<Exam> list;

    public void back(ActionEvent actionEvent) throws IOException {
        Parent mainMenuParent = FXMLLoader.load(getClass().getResource("mainMenuView.fxml"));
        Scene mainMenuScene = new Scene(mainMenuParent);

        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
        window.show();
        //Allows user to return to previous page (Main Menu)

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.addBackButtonImage(backButton,"back.png");
        //Adds image to back button
        try {
            fillComboBoxes();
            examYearColumn.setCellValueFactory(new PropertyValueFactory<Exam, Integer>("exam_year"));
            examModuleColumn.setCellValueFactory(new PropertyValueFactory<Exam, String>("exam_module"));
            examTitleColumn.setCellValueFactory(new PropertyValueFactory<Exam, String>("exam_title"));
            list = getAllRecords();

            examsTable.setItems(list);
            //Gathers all exam objects and sets them in table columns


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            }
            return null;
            //Ensures integer fields only allow integers to be inputted
        };

        yearTextField.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 2021, integerFilter));
        //Uses integer filter and sets default value as 2021 (current year)

    }

    private void fillComboBoxes() throws SQLException {
        moduleArray = new ArrayList();
        userArray = new ArrayList();
        Statement modulesStatement = Main.db.a.createStatement();
        Statement usersStatement = Main.db.a.createStatement();
        String modulesQuery = "SELECT moduleID,module_name FROM public.module;";
        String usersQuery = "SELECT userID,fullname,user_email FROM public.user;";
        ResultSet modulesR = modulesStatement.executeQuery(modulesQuery);
        //Gathers all modules

        while (modulesR.next()) {
            int id = (modulesR.getInt("moduleID"));
            String name = (modulesR.getString("module_name"));
            examViewModuleCombo.getItems().add(name);
            viewModuleComboBox.getItems().add(name);
            moduleArray.add(String.valueOf(id));
            moduleArray.add(name);
        }
        //Adds modules to appropriate comboboxes and dedicated arraylist


        ResultSet usersR = usersStatement.executeQuery(usersQuery);
        while (usersR.next()) {
            String temp = usersR.getString("fullname");
            temp = temp + " (" + usersR.getString("user_email") + ")";
            examViewMarkerCombo.getItems().add(temp);
            examViewSetterCombo.getItems().add(temp);
            userArray.add(String.valueOf(usersR.getInt("userID")));
            userArray.add(temp);
        }
        //Gathers all users and adds them to appropriate comboboxes and dedicated arraylist


        usersR.close();
        modulesR.close();
        usersStatement.close();
        modulesStatement.close();
    }

    public void submitPaper(ActionEvent actionEvent) {
        String checker = "";
        String setter = "";
        String module = "";
        Integer checkerNo;
        Integer setterNo;
        Integer moduleNo;
        if (examViewMarkerCombo.getSelectionModel().getSelectedItem() != null && examViewSetterCombo.getSelectionModel().getSelectedItem() != null) {
            checker = examViewMarkerCombo.getSelectionModel().getSelectedItem().toString();
            setter = examViewSetterCombo.getSelectionModel().getSelectedItem().toString();
            //Ensures checker and setter comboboxes have been chosen and gathers choice

            checkerNo = Integer.parseInt(userArray.get(userArray.indexOf(checker) - 1));
            setterNo = Integer.parseInt(userArray.get(userArray.indexOf(setter) - 1));
            //Gathers appropriate IDs from arraylists
        } else {
            AlertBox.display("Error", "Both a Checker and a Setter need to be allocated to the exam!");
            return;
            //Produces error message in AlertBox if checker or setter not selected
        }

        if (examViewModuleCombo.getSelectionModel().getSelectedItem() != null) {
            module = examViewModuleCombo.getSelectionModel().getSelectedItem().toString();
            moduleNo = Integer.parseInt(moduleArray.get(moduleArray.indexOf(module) - 1));
            //Gathers chosen module

        } else {
            AlertBox.display("Error", "Module needs to be set!");
            return;
            //Ensures module selected
        }
        if ((yearField.getText().matches("-?\\d+") && (Integer.parseInt(yearField.getText()) > 1900 && Integer.parseInt(yearField.getText()) < 2100)) == false) {
            AlertBox.display("Error", "Make sure you input a valid year!");
            return;
            //Ensures valid year chosen
        }


        String submit = "INSERT INTO public.exam(examid, exam_year, moduleid, exam_title, checkerid, setterid) VALUES (nextval('exam_sequence')," + Integer.parseInt(yearField.getText()) + "," + moduleNo + ",'" + titleField.getText() + "'," + checkerNo + "," + setterNo + ");";

        Statement state = null;
        try {
            state = Main.db.a.createStatement();
        } catch (SQLException throwables) {
            AlertBox.display("Error!", throwables.getMessage());
        }
        try {
            state.executeUpdate(submit);
            AlertBox.display("Submit!", "Exam submitted successfully!");
            //Submits exam to system
        } catch (SQLException throwables) {
            AlertBox.display("Error!", "Unable to submit exam due to internal error");
        }
            //Informs user the exam has not been added due to an error
        try {
            state.close();
        } catch (SQLException throwables) {
            AlertBox.display("Error!", throwables.getMessage());
        }
    }


    public static ObservableList<Exam> getAllRecords() throws SQLException {
        Statement s = Main.db.a.createStatement();
        String modules = "SELECT exam_year, exam_title,module_name FROM public.exam INNER JOIN public.module ON exam.moduleID = module.moduleID;;";
        ResultSet resultSet = s.executeQuery(modules);
        //Gathers all exams and their modules
        ObservableList<Exam> aList = getObjects(resultSet);
        //Method run to set all exam and module data to appropriate objects
        return aList;
    }

    private static ObservableList<Exam> getObjects(ResultSet resultSet) {
        try {
            ObservableList<Exam> aList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                Exam exam = new Exam();
                exam.setExam_year(resultSet.getInt("exam_year"));
                exam.setExam_title(resultSet.getString("exam_title"));
                exam.setExam_module(resultSet.getString("module_name"));
                aList.add(exam);
                //Defines an exam object with gathered data
            }
            return aList;
            //Returns list of exam objects
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return null;
    }

    public void filterExams(ActionEvent actionEvent) {
        List<Exam> copyOfList = list;
        ObservableList<Exam>newList = FXCollections.observableArrayList();

        for (Exam exam:copyOfList){
            //System.out.println(viewModuleComboBox.getSelectionModel().getSelectedIndex());
            if(viewModuleComboBox.getSelectionModel().getSelectedItem() == null){
               if(exam.getExam_year() == Integer.parseInt(yearTextField.getText())){
                   System.out.println(exam.getExam_year());
                   newList.add(exam);
               }
               //Allows for user to filter throug exams by just exam year
            }
            else if (yearTextField.getText().trim().isEmpty()){
                if(exam.getExam_module().equals(viewModuleComboBox.getSelectionModel().getSelectedItem().toString())){
                    newList.add(exam);
                }
            }
            //Allows user to filter by just module
            else if(exam.getExam_year() == Integer.parseInt(yearTextField.getText())&&exam.getExam_module().equals(viewModuleComboBox.getSelectionModel().getSelectedItem().toString())){
                System.out.println();
                newList.add(exam);}}
            //Allows user to filter by both exam and module
        examsTable.setItems(newList);
        //Filter exams

    }

    public void resetFields(ActionEvent actionEvent) {
        viewModuleComboBox.getSelectionModel().clearSelection();
        yearTextField.clear();
        examsTable.setItems(list);
        //Resets fields and table
    }
}
