import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Locale;

public class CreatePaperBox {
    static boolean answer;
    static String examName;
    static int examYear;
    static String examModule;
    static String examTitle;
    static SortedList<Question> data;
    static XWPFDocument doc;
    static XWPFDocument answersDoc;
    private static int moduleID;

    public static boolean display(SortedList<Question> sortedData){
        data = sortedData;
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Create Paper");
        window.setMinWidth(200);
        TextField examNameField = new TextField();
        TextField examYearField = new TextField();
        TextField examModuleField = new TextField();
        TextField examTitleField = new TextField();

        examNameField.promptTextProperty().set("Exam Name");
        examYearField.promptTextProperty().set("Exam Year");
        examModuleField.promptTextProperty().set("Exam Module");
        examTitleField.promptTextProperty().set("Exam Title");
        //Defines and sets all UI elements relating to the CreatePaperBox
        Button trueButton = new Button("Create Paper");

        trueButton.setOnAction(e->{
            examName = examNameField.getText();
            examModule = examModuleField.getText();
            try {
                if(!moduleCheck()){
                    AlertBox.display("Invalid Module","Make sure you input a valid module name");
                    return;
                }
                //Ensures module chosen by user is an existing module
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                examYear = Integer.parseInt(examYearField.getText().trim());
            }
            catch (NumberFormatException exception){
                AlertBox.display("Error","Make sure you have entered a compatible year");
                return;
            }
            //Ensures year chosen is a valid year

            examTitle = examTitleField.getText();
            if (examName.trim().isEmpty()){
                AlertBox.display("Error","Make sure exam name selected");
                return;
            }
            //Ensures exam name given
            if(ConfirmBox.display("Create Paper", "Confirm?")){
                try {
                    createPaper();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (InvalidFormatException invalidFormatException) {
                    invalidFormatException.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            else {
                System.out.println("False");
            }
            //If user chooses to create an exam, the appropriate method is run
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(examNameField,examModuleField,examTitleField,examYearField,trueButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        return answer;
        //UI elements set in box
    }

    private static Boolean moduleCheck() throws SQLException {
        Statement modulesStatement = Main.db.a.createStatement();
        String modulesQuery = "SELECT moduleID,module_name FROM public.module;";
        ResultSet modulesR = modulesStatement.executeQuery(modulesQuery);
        //Selects all existing modules
        while (modulesR.next()){
            if(examModule.toLowerCase(Locale.ROOT).equals(modulesR.getString("module_name").toLowerCase(Locale.ROOT))){
                moduleID = modulesR.getInt("moduleID");
                return true;
            }
            //Checks if module exists and returns appropriate boolean value
        }
        return false;
    }

    public static void createPaper() throws IOException, InvalidFormatException, SQLException {
        doc = new XWPFDocument();
        answersDoc = new XWPFDocument();
        //Creates files for exam and mark scheme

        XWPFParagraph title = doc.createParagraph();
        XWPFParagraph answersTitle = answersDoc.createParagraph();

        XWPFRun run = title.createRun();
        run.setText(examName);
        run.setBold(true);
        run.setFontSize(24);
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFParagraph subTitle = doc.createParagraph();

        XWPFRun answersRun = answersTitle.createRun();
        answersRun.setText(examName+" Answers");
        answersRun.setBold(true);
        answersRun.setFontSize(24);
        answersTitle.setAlignment(ParagraphAlignment.CENTER);
        XWPFParagraph answersSubTitle = answersDoc.createParagraph();

        XWPFRun subTitleRun = subTitle.createRun();
        subTitleRun.setText(examModule);
        subTitleRun.addBreak();
        subTitleRun.setText(String.valueOf(examYear));
        subTitleRun.addBreak();
        subTitleRun.addBreak();
        subTitleRun.setText("Made by "+PostgreSQLJDBC.user);
        subTitleRun.setItalic(true);
        subTitleRun.setFontSize(14);
        subTitle.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun answersSubTitleRun = answersSubTitle.createRun();
        answersSubTitleRun.setText(examModule);
        answersSubTitleRun.addBreak();
        answersSubTitleRun.setText(String.valueOf(examYear));
        answersSubTitleRun.addBreak();
        answersSubTitleRun.addBreak();
        answersSubTitleRun.setText("Made by "+PostgreSQLJDBC.user);
        answersSubTitleRun.setItalic(true);
        answersSubTitleRun.setFontSize(14);
        answersSubTitle.setAlignment(ParagraphAlignment.CENTER);



        XWPFParagraph questions = doc.createParagraph();
        XWPFRun questionsRun = questions.createRun();
        questions.setPageBreak(true);

        XWPFParagraph answersMain= answersDoc.createParagraph();
        XWPFRun answersMainRun = answersMain.createRun();
        answersMain.setPageBreak(true);

        //Creates Title page for both documents

        int marks = 0;
        int intensity = 0;
        for(Question q:data) {
            if (q.checkedProperty().getValue()) {

                questionsRun.setText(String.valueOf(q.getSubquestion()));
                questionsRun.addBreak();
                questionsRun.setText(q.getQuestion_number()+": ");
                questionsRun.setText("   "+q.getQuestion_header()+"\n("+q.getQuestion_mark()+" marks)");
                questionsRun.addCarriageReturn();
                //Adds question to exam paper
                if(!StringUtils.isEmpty(q.getQuestion_image())){
                    addImage(q);
                }
                //Adds image relating to question
                questionsRun.addCarriageReturn();
                questionsRun.addCarriageReturn();
                questionsRun.addCarriageReturn();

                answersMainRun.setText(String.valueOf(q.getSubquestion()));
                answersMainRun.addBreak();
                answersMainRun.setText(q.getQuestion_number()+": ");
                answersMainRun.setText("   "+q.getQuestion_answer()+"\n("+q.getQuestion_mark()+" marks)");
                System.out.println(q.getQuestion_answer());
                answersMainRun.addCarriageReturn();
                //Adds answers on mark scheme
                marks = marks+q.getQuestion_mark();
                intensity = intensity+q.getQuestion_difficulty();

                //Accumulates marks and intensity score for questions
            }
        }
        questionsRun.addBreak();
        questionsRun.setText("Total Marks: "+marks);
        questionsRun.addBreak();
        questionsRun.setText("Average difficulty of paper: "+intensity);

        answersMainRun.addBreak();
        answersMainRun.setText("Total Marks: "+marks);
        answersMainRun.addBreak();
        answersMainRun.setText("Average difficulty of paper: "+intensity);
        //Sets total marks and average intensity of paper in both the exam and the mark scheme

        FileOutputStream fos = new FileOutputStream(examName+".docx");
        doc.write(fos);
        fos.close();
        doc.close();

        FileOutputStream ansfos = new FileOutputStream(examName+"Answers.docx");
        answersDoc.write(ansfos);
        ansfos.close();
        answersDoc.close();
        //Creates and closes paper and mark scheme
        confirmPaperAddition();
        //Method run questioning whether or not user wants to add paper to the system
    }

    public static void confirmPaperAddition() throws SQLException {
        if(ConfirmBox.display("Add Paper", "Do you want to add this paper to the database?")){
            addPaper();
            //If the user chooses to, the add paper method is run
        }
    }

    public static void addPaper() throws SQLException {
        Statement state = Main.db.a.createStatement();
        String insertPaper = "INSERT INTO public.exam VALUES (nextval('exam_sequence'),"+examYear+","+moduleID+",'"+examName+"'," +PostgreSQLJDBC.checkerid+","+PostgreSQLJDBC.setterid+");";
        state.executeUpdate(insertPaper);
        //The exam is added
        ResultSet lastValue = state.executeQuery("SELECT last_value FROM exam_sequence;");
        int currentExamID = 0;
        while (lastValue.next()) {
            currentExamID = lastValue.getInt("last_value");
        }
        for (Question q: data){
            if (q.checkedProperty().getValue()){
                state.executeUpdate("INSERT INTO public.exam_questions VALUES (nextval('exam_questions_sequence'),"+currentExamID+","+q.getQuestionID()+");"); }
        }
        //Every question in exam is added to the system, linked to the exam
    }

    public static Dimension getImageDimension(File imgFile) throws IOException {
        int pos = imgFile.getName().lastIndexOf(".");

        String suffix = imgFile.getName().substring(pos + 1);
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        //Image file selected
        while(iter.hasNext()) {
            ImageReader reader = iter.next();

            ImageInputStream stream = new FileImageInputStream(imgFile);
            reader.setInput(stream);
            int width = reader.getWidth(reader.getMinIndex());
            int height = reader.getHeight(reader.getMinIndex());
            stream.close();
            return new Dimension(width, height);

            //Dimension of image returned

            }

        throw new IOException("File not an image file: " + imgFile.getPath());
    }
    public static void addImage(Question q) throws IOException, InvalidFormatException {
        XWPFParagraph imageP = doc.createParagraph();
        XWPFRun imagepRun = imageP.createRun();
        String imgFile = q.getQuestion_image();
        System.out.println(imgFile.substring(imgFile.lastIndexOf('.')));
        FileInputStream is = new FileInputStream(imgFile);
        File n = new File(imgFile);
        System.out.println(getImageDimension(n).getHeight());
        imagepRun.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, imgFile, Units.toEMU(getImageDimension(n).getWidth()), Units.toEMU(getImageDimension(n).getHeight())); // 200x200 pixels
        //Image added to paper
        imagepRun.addBreak();
        imagepRun.setText(q.getImage_caption());
        //Caption added underneath
        is.close();

    }

}
