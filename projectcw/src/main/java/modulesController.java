import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class modulesController implements Initializable {
    @FXML
    public Button moduleSubmitButton;
    @FXML
    public Button moduleResetButton;
    @FXML
    public TextField moduleCodeField;
    @FXML
    public TextField moduleNameField;
    @FXML
    public TableView<Modules> moduleTable = new TableView<Modules>();
    @FXML
    public TableColumn<Modules, String> codeColumn = new TableColumn<Modules,String>("module_code");
    @FXML
    public TableColumn<Modules, String> moduleColumn = new TableColumn<Modules,String>("module_name");
    @FXML
    public Button backButton;


    public void submitModule(ActionEvent actionEvent) {
        if(moduleNameField.getText().trim().isEmpty()||moduleCodeField.getText().trim().isEmpty()){
            AlertBox.display("Error","Need to fill both fields");
            return;
            //Informs user both fields needs to be filled
        }
        String submit = "INSERT INTO public.module (moduleid,module_code,module_name)" + " VALUES (nextval('module_sequence'),'" + moduleCodeField.getText() + "', '" + moduleNameField.getText() + "');";
        Statement state = null;
        try {
            state = Main.db.a.createStatement();
        } catch (SQLException throwables) {
            AlertBox.display("Error!", throwables.getMessage());
        }
        try {
            state.executeUpdate(submit);
            AlertBox.display("Submit!", "Module submitted successfully!");
            //Submits new module
            ObservableList<Modules> list = getAllRecords();
            moduleTable.setItems(list);
            //Informs user of successfully submitting a module, then updates table with new module
        } catch (SQLException throwables) {
            AlertBox.display("Error!", throwables.getMessage());
        }
    }


    public static ObservableList<Modules> getAllRecords() throws SQLException {
        Statement s = Main.db.a.createStatement();
        String modules = "SELECT module_code, module_name  FROM public.module;";
        ResultSet resultSet = s.executeQuery(modules);
        ObservableList<Modules> list = getObjects(resultSet);
        //Selects all modules from database and returns data in a list
        return list;
    }

    private static ObservableList<Modules> getObjects(ResultSet resultSet) {
        try {
            ObservableList<Modules> list = FXCollections.observableArrayList();
            while (resultSet.next()) {
                Modules module = new Modules();
                module.setModule_code(resultSet.getString("module_code"));
                module.setModule_name(resultSet.getString("module_name"));
                System.out.println(module.getModule_name());
                list.add(module);
                //Sets data to module object and then adds it to list of module objects
            }
            return list;
            //returns list of module objects
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return null;
    }

    public void clearModule(ActionEvent actionEvent) throws SQLException {
        moduleCodeField.clear();
        moduleNameField.clear();
        //Clears fields
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.addBackButtonImage(backButton,"back.png");
        try {
            codeColumn.setCellValueFactory(new PropertyValueFactory<Modules, String>("module_code"));
            moduleColumn.setCellValueFactory(new PropertyValueFactory<Modules, String>("module_name"));
            ObservableList<Modules> list = getAllRecords();
            moduleTable.setItems(list);
            //Gets all module data and sets it in table

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void back(ActionEvent actionEvent) throws IOException {
        Parent mainMenuParent  = FXMLLoader.load(getClass().getResource("mainMenuView.fxml"));
        Scene mainMenuScene = new Scene(mainMenuParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
        window.show();
        //Returns user to main menu

    }
}
