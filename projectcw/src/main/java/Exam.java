public class Exam {

    //Exam object to be used to handle exams

    private String exam_title = null;
    private String exam_module = null;
    private Integer exam_year = null;
    //Attributes and getter and setter methods defined

    public Exam() {
    }

    public Exam(String exam_title, String exam_module,Integer exam_year) {
        this.exam_title = exam_title;
        this.exam_module = exam_module;
        this.exam_year = exam_year;
    }

    public String getExam_title() {
        return exam_title;
    }

    public void setExam_title(String exam_title) {
        this.exam_title = exam_title;
    }

    public String getExam_module() {
        return exam_module;
    }

    public void setExam_module(String exam_module) {
        this.exam_module = exam_module;
    }

    public Integer getExam_year() {
        return exam_year;
    }

    public void setExam_year(Integer exam_year) {
        this.exam_year = exam_year;
    }
}
