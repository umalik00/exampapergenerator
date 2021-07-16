import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;

public class Question {
    private String image_caption;
    private int questionID;
    private int examid;
    private String question_number;
    private String question_header;
    private String question_answer;
    private int question_mark;
    private int subquestion;
    private int imageID;
    private String module_name;
    private int moduleID;
    private int exam_year;
    private int question_difficulty;
    private String exam_title;
    private CheckBox select;
    private BooleanProperty checked;
    private String question_image;
    //Question object to be used to handle questions

    public Question(){
        checked = new SimpleBooleanProperty(false);
    }

    public BooleanProperty getChecked(){
        return checked;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getExamid() {
        return examid;
    }

    public void setExamid(int examid) {
        this.examid = examid;
    }

    public String getQuestion_number() {
        return question_number;
    }

    public void setQuestion_number(String question_number) {
        this.question_number = question_number;
    }

    public String getQuestion_header() {
        return question_header;
    }

    public void setQuestion_header(String question_header) {
        this.question_header = question_header;
    }

    public String getQuestion_answer() {
        return question_answer;
    }

    public void setQuestion_answer(String question_answer) {
        this.question_answer = question_answer;
    }

    public int getQuestion_mark() {
        return question_mark;
    }

    public void setQuestion_mark(int question_mark) {
        this.question_mark = question_mark;
    }

    public int getSubquestion() {
        return subquestion;
    }

    public void setSubquestion(int subquestion) {
        this.subquestion = subquestion;
    }

    public int getQuestion_imageID() {
        return imageID;
    }
    public void setQuestion_imageID(int imageID) {this.imageID=imageID;
    }

    public CheckBox getSelect() {
        return select;
    }

    public void setSelect(String string) {
        this.select = new CheckBox(string);
    }

    public int getExam_year() {
        return exam_year;
    }

    public void setExam_year(int exam_year) {
        this.exam_year = exam_year;
    }

    public int getModuleID() {
        return moduleID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public int getQuestion_difficulty() {
        return question_difficulty;
    }

    public void setQuestion_difficulty(int question_difficulty) {
        this.question_difficulty = question_difficulty;
    }

    public String getExam_title() {
        return exam_title;
    }

    public void setExam_title(String exam_title) {
        this.exam_title = exam_title;
    }

    public boolean isChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    public void setQuestion_image(String question_image) {
        this.question_image = question_image;
    }

    public String getQuestion_image() {
        return question_image;
    }

    public String getImage_caption() {
        return image_caption;
    }

    public void setImage_caption(String image_caption) {
        this.image_caption = image_caption;
    }

    //Attributes and getter and setter methods defined

}
