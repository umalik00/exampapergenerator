import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.UnaryOperator;

public class questionController implements Initializable {
    public ComboBox examComboBox;
    public Button submitButton;
    public CheckBox subquestionCheck;
    public ComboBox<String> subquestionComboBox;
    public TextField questionDifficultyField;
    public TextArea questionHeaderField;
    public TextField questionNumberField;
    public TextField questionMarkField;
    public TextArea questionAnswerField;
    public TableColumn<Question, Integer> viewIDColumn;
    public TableColumn<Question, Integer> viewYearColumn;
    public TableColumn<Question, String> viewModuleColumn;
    public TableColumn<Question, String> viewExamNameColumn;
    public TableColumn<Question, Integer> viewNumberColumn;
    public TableColumn<Question, String> viewQuestionColumn;
    public TableColumn<Question, Integer> viewMarksColumn;
    public TableColumn<Question, Integer> viewDifficultyColumn;
    public TableColumn<Question, Integer> viewQuestionNumberColumn;
    public TableView<Question> questionTable;
    public TextField searchQuestionField;
    public TextField viewYearTextField;
    public TextField viewNameTextField;
    public TextField viewModuleTextField;
    public TextField viewQuestionTextField;
    public TextField viewMarksTextField;
    public TextField viewDifficultyTextField;
    public TextField viewSubquestionTextField;
    public TableColumn viewSelectColumn;
    public TextArea feedbackField;
    public Button backButton;
    private String tempModule;
    private Integer tempYear;
    private String tempName;
    private Integer tempExamID;

    private Boolean subquestionBool = false;
    private Integer tempSubquestion;
    private ObservableList<Question> list = FXCollections.observableArrayList();
    private SortedList<Question> sortedData;

    public void back(ActionEvent actionEvent) throws IOException {
        Parent mainMenuParent  = FXMLLoader.load(getClass().getResource("mainMenuView.fxml"));
        Scene mainMenuScene = new Scene(mainMenuParent);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
        window.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.addBackButtonImage(backButton,"back.png");
        try {
            fillComboBox();
        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }


        subquestionCheck.selectedProperty().addListener((observable, oldValue, newValue) -> subquestionBool = subquestionCheck.isSelected());
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        };
        UnaryOperator<TextFormatter.Change> integerFilterIncludeZero = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([0-9][0-9]*)?")) {
                return change;
            }
            return null;
        };

        questionMarkField.setTextFormatter(
                new TextFormatter<>(new IntegerStringConverter(), 1, integerFilterIncludeZero));
        questionDifficultyField.setTextFormatter(
                new TextFormatter<>(new IntegerStringConverter(), 1, integerFilter));
        //Ensures numerical fields only contain integers


        viewSelectColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Question, Boolean>, ObservableValue<Boolean>>(){
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Question, Boolean> param)
                    {
                        return param.getValue().getChecked();
                    }
                });
        //Checks which checkboxes have been selected



        viewSelectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(viewSelectColumn));
        viewIDColumn.setCellValueFactory(new PropertyValueFactory<>("questionID"));
        viewYearColumn.setCellValueFactory(new PropertyValueFactory<>("exam_year"));
        viewModuleColumn.setCellValueFactory(new PropertyValueFactory<>("module_name"));
        viewExamNameColumn.setCellValueFactory(new PropertyValueFactory<>("exam_title"));
        viewNumberColumn.setCellValueFactory(new PropertyValueFactory<>("question_number"));
        viewQuestionColumn.setCellValueFactory(new PropertyValueFactory<>("question_header"));
        viewMarksColumn.setCellValueFactory(new PropertyValueFactory<>("question_mark"));
        viewDifficultyColumn.setCellValueFactory(new PropertyValueFactory<>("question_difficulty"));
        viewQuestionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("subquestion"));

        //Sets data to each column


        try {
            list = getAllRecords();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        FilteredList<Question> filteredData = new FilteredList<>(list, p -> true);

        searchQuestionField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(question -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = newValue.toLowerCase();

            return question.getQuestion_header() != null && question.getQuestion_header().toLowerCase().contains(lowerCaseFilter); // Filter matches first name.
        }));
        //returns questions with matching headers

        viewYearTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(question -> {
            if (newValue == null || newValue.isEmpty()) {
                System.out.println("emty/nu;;");
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            System.out.println( String.valueOf(question.getExam_year()));
            return String.valueOf(question.getExam_year()).contains(lowerCaseFilter);
        }));
        //returns questions with matching years

        viewNameTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(question -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            return question.getExam_title() != null && question.getExam_title().contains(lowerCaseFilter);
        }));

        //returns questions with matching exam titles

        viewModuleTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(question -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            return question.getModule_name().toLowerCase(Locale.ROOT).contains(lowerCaseFilter);
        }));
        //returns questions with matching modules


        viewQuestionTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(question -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            return String.valueOf(question.getSubquestion()).contains(lowerCaseFilter);
        }));
        //returns questions with matching questions numbers


        viewSubquestionTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(question -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            return question.getQuestion_number() != null && question.getQuestion_number().contains(lowerCaseFilter);
        }));

        viewDifficultyTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(question -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            return String.valueOf(question.getQuestion_difficulty()).contains(lowerCaseFilter);
        }));
        //returns questions with matching difficulties


        viewMarksTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(question -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            return String.valueOf(question.getQuestion_mark()).contains(lowerCaseFilter);
        }));
        //returns questions with matching question marks


        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(questionTable.comparatorProperty());
        //Sorted list allows for the data to be dynamically filtered

        questionTable.setItems(sortedData);
        //Adds data to table

    }

    private void fillComboBox() throws SQLException {
        Statement examStatement = Main.db.a.createStatement();
        String examQuery = "SELECT exam_year, module_name, exam_title FROM public.exam INNER JOIN public.module ON module.moduleID=exam.moduleID;";
        ResultSet examR = examStatement.executeQuery(examQuery);
        //Gathers exam data
        while(examR.next()){
            String name = examR.getString("exam_title");
            if (name ==null){
                name = "No name";
            }
            String temp = name+" ("+examR.getString("module_name")+" "+examR.getString("exam_year")+")";
            examComboBox.getItems().add(temp);
            }
        //Sets exams in combobox

        examComboBox.getSelectionModel().selectedItemProperty().addListener( (v,oldValue,newValue) -> {
            try {
                subquestionLoad((String) newValue);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        //Allows questions combobox data to change depending on exam combobox selection
    }

    public void submitQuestion(ActionEvent actionEvent) throws SQLException {
        String insertQuestion;
        try {
            if(questionNumberField.getText().trim().isEmpty()&&subquestionComboBox.getSelectionModel().getSelectedItem().isEmpty()){
            }

        } catch (NullPointerException e) {
            AlertBox.display("Error", "Make sure question has some form of question number");
            return;
            //Ensures question number given
        }
        if (subquestionBool) {
            String questionIDQuery = "SELECT questionID FROM public.question WHERE examID = '" + tempExamID + "' AND question_number = '" + subquestionComboBox.getSelectionModel().getSelectedItem() + "';";
            System.out.println(questionIDQuery);
            Statement questionIDStatement = Main.db.a.createStatement();
            ResultSet questionIDR = questionIDStatement.executeQuery(questionIDQuery);

            while(questionIDR.next()){
                tempSubquestion = questionIDR.getInt("questionID");
            }
            //QuestionID set for parent question of subquestion to be submitted
            questionIDR.close();
            questionIDStatement.close();

            if(questionAnswerField.getText().trim().isEmpty()||questionHeaderField.getText().trim().isEmpty()||questionNumberField.getText().trim().isEmpty()||questionDifficultyField.getText().trim().isEmpty()||questionMarkField.getText().trim().isEmpty()|| questionDifficultyField.getText().trim().equals("-") || questionMarkField.getText().trim().equals("-")){
                AlertBox.display("Error","Make sure you fill out every field with valid values");
                return;
            }
            if(subquestionComboBox.getSelectionModel().isEmpty()){
                AlertBox.display("Error","Make sure you have selected a parent question");
                return;
            }
            //Ensures every field is filled out

            insertQuestion = "INSERT INTO public.question(questionid, examid, question_number, question_header, question_answer, question_mark, subquestion, question_difficulty) VALUES (nextval('question_sequence'),"+tempExamID+",'"+questionNumberField.getText() +"','"+questionHeaderField.getText() +"','"+ questionAnswerField.getText()+"',"+ Integer.parseInt(questionMarkField.getText())+" ,"+tempSubquestion+" ,"+Integer.parseInt(questionDifficultyField.getText())+");";
        }
        else{
            insertQuestion = "INSERT INTO public.question(questionid, examid, question_number,question_header, question_answer, question_mark, question_difficulty) VALUES (nextval('question_sequence'),"+tempExamID+",'"+questionNumberField.getText() +"','"+questionHeaderField.getText() +"','"+ questionAnswerField.getText()+"',"+ Integer.parseInt(questionMarkField.getText())+","+Integer.parseInt(questionDifficultyField.getText())+");";
        }
        Statement state = null;

        try {
            state = Main.db.a.createStatement();
        } catch (SQLException throwables) {
            AlertBox.display("Error!", throwables.getMessage());
        }
        try {
            assert state != null;
            state.executeUpdate(insertQuestion);
            //inserts question into system
            AlertBox.display("Submit!", "Question submitted successfully!");
            state = Main.db.a.createStatement();
            ResultSet lastValue = state.executeQuery("SELECT last_value FROM question_sequence;");
            int currentQuestionID = 0;
            while (lastValue.next()) {
                currentQuestionID = lastValue.getInt("last_value");
            }
            if(!feedbackField.getText().isEmpty()){
                state = Main.db.a.createStatement();
                state.executeUpdate("INSERT INTO public.feedback VALUES (nextval('feedback_sequence'),"+currentQuestionID+",'"+feedbackField.getText()+"');");
            }
            //Inserts question feedback into system

            state = Main.db.a.createStatement();
            state.executeUpdate("INSERT INTO public.exam_questions VALUES (nextval('exam_questions_sequence'),"+tempExamID+","+currentQuestionID+");");

            //Inserts question into master list where questions are matched to exams

            Parent parent  = FXMLLoader.load(getClass().getResource("questionView.fxml"));
            Scene scene = new Scene(parent);

            Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();

        } catch (IOException throwables) {
            AlertBox.display("Error!", throwables.getMessage());
        }
        try {
            state.close();
        } catch (SQLException throwables) {
            AlertBox.display("Error!", throwables.getMessage());
        }
    }


    public void subquestionLoad(String value) throws SQLException {
        int temp = value.indexOf("(");
        tempName = value.substring(0,temp-1);
        tempYear = Integer.valueOf(value.substring(value.length()-5,value.length()-1));
        tempModule = value.substring(temp+1,value.lastIndexOf(' '));

        String examIDQuery;
        if(tempName.equals("No name")){
            examIDQuery = "SELECT examID FROM public.exam INNER JOIN public.module ON module.moduleID=exam.moduleID WHERE module_name = '"+tempModule+"' AND exam_year = "+tempYear+" AND exam_title IS NULL;";
        }
        else {
            examIDQuery = "SELECT examID FROM public.exam INNER JOIN public.module ON module.moduleID=exam.moduleID WHERE module_name = '" + tempModule + "' AND exam_year = " + tempYear + " AND exam_title = '" + tempName + "';";
        }

        Statement examIDStatement = Main.db.a.createStatement();
        ResultSet examIDR = examIDStatement.executeQuery(examIDQuery);
        tempExamID = 0;
        while(examIDR.next()){
            tempExamID = examIDR.getInt("examID");
        }
        examIDR.close();
        examIDStatement.close();

        String questionsQuery = "SELECT question.question_number FROM public.exam_questions INNER JOIN public.question ON exam_questions.questionid = question.questionid INNER JOIN public.exam ON exam_questions.examid = exam.examid WHERE exam.examID = "+tempExamID+";";
        Statement questionsStatement = Main.db.a.createStatement();
        ResultSet questionsR = questionsStatement.executeQuery(questionsQuery);
        subquestionComboBox.getItems().clear();
        //Question number given for questions relating to subquestions
        while(questionsR.next()){
            String questionNumber = questionsR.getString("question_number");
            subquestionComboBox.getItems().add(questionNumber);

        }
        questionsR.close();
        questionsStatement.close();
    }

    public ObservableList<Question> getAllRecords() throws SQLException {
        Statement s = Main.db.a.createStatement();
        String modules = "SELECT question.questionID,exam.exam_year,module.module_name,exam.exam_title,question.subquestion,question.question_number,question.question_header,question.question_answer,question.question_mark,question.question_difficulty,image,caption  FROM public.exam_questions INNER JOIN public.question ON exam_questions.questionid = question.questionid INNER JOIN public.exam ON exam_questions.examid = exam.examid INNER JOIN public.module ON exam.moduleID = module.moduleid LEFT JOIN public.image ON question.questionID = image.questionID\n" +
                "ORDER BY exam_questionsid ASC ";
        ResultSet resultSet = s.executeQuery(modules);
        //Gets all questions in every exam
        return getObjects(resultSet);
        }


    private ObservableList<Question> getObjects(ResultSet resultSet) {
        try {
            ObservableList<Question> aList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                //Traverses through list of question data
                Question question = new Question();
                question.setChecked(false);
                question.setQuestionID(resultSet.getInt("questionID"));
                question.setExam_year(resultSet.getInt("exam_year"));
                question.setModule_name(resultSet.getString("module_name"));
                question.setExam_title(resultSet.getString("exam_title"));
                question.setQuestion_number(resultSet.getString("question_number"));
                question.setQuestion_header(resultSet.getString("question_header"));
                question.setQuestion_answer(resultSet.getString("question_answer"));
                question.setQuestion_mark(resultSet.getInt("question_mark"));
                question.setQuestion_difficulty(resultSet.getInt("question_difficulty"));
                question.setSubquestion(resultSet.getInt("subquestion"));
                question.setQuestion_image(resultSet.getString("image"));
                question.setImage_caption(resultSet.getString("caption"));
                System.out.println(question.getExam_year());
                System.out.println(question.getModule_name());
                aList.add(question);
                //Sets data to question object and adds it to list of questions
            }
            return aList;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null; }

    public void selectQuestions(ActionEvent actionEvent) {
        if(ConfirmBox.display("Create Exam","Do you want to create an exam?") == true){
            CreatePaperBox.display(sortedData);
        }
    }
    //Allows user to create exam
}


