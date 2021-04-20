package sample;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private TextArea textArea;

    @FXML
    private Button XMLAll;

    @FXML
    private Button BDAll;

    @FXML
    private Button parseXMLtoBD;

    @FXML
    private Button parseBDtoXML;

    @FXML
    private Button btnId;

    @FXML
    private Button btnIdDb;

    @FXML
    private TextField inputId;

    @FXML
    private TextField inputIdDb;

    @FXML
    private Button addXML;

    @FXML
    private Button changeXML;

    @FXML
    private Button deleteXML;

    @FXML
    private Button addBD;

    @FXML
    private Button changeBD;

    @FXML
    private Button deleteBD;

    private String filePath;

    private HBox getHbox() {
        var label1 = new Label("Введите имя:");
        var label2 = new Label("Введите фамилию:");
        var label3 = new Label("Введите отчество:");
        var label4 = new Label("Введите школу:");
        var label5 = new Label("Введите класс:");
        var label6 = new Label("Введите возраст:");
        var textField1 = new TextField();
        var textField2 = new TextField();
        var textField3 = new TextField();
        var textField4 = new TextField();
        var textField5 = new TextField();
        var textField6 = new TextField();
        var button = new Button("Добавить");
        button.setOnAction(actionEvent -> {
            var sax = new SAXParse();
            var students = sax.readerSaxDocument(filePath);
            var newStudent = new Student(
                    students.size() + 1,
                    textField1.getText(),
                    textField2.getText(),
                    textField3.getText(),
                    textField4.getText(),
                    textField5.getText(),
                    textField6.getText()
            );
            students.add(newStudent);
            var dom = new DomParse(filePath);
            dom.setDomNodes(students);
        });
        HBox hBox = new HBox(new FlowPane(Orientation.VERTICAL, 20, 20, label1, textField1, label2, textField2, label3, textField3, label4, textField4, label5, textField5, label6, textField6, button));
        hBox.setMinWidth(400);
        hBox.setMinHeight(600);
        return hBox;
    }

    @FXML
    void initialize() {
        var prop = new PropertiesParse();
        var catalog = prop.readCatalogRoot();
        this.filePath = catalog + "\\file.xml";
        XMLAll.setOnAction(actionEvent -> {
            var sax = new SAXParse();
            var students = sax.readerSaxDocument(filePath);
            textArea.setText("");
            if (students.size() > 0) {
                for (Student student : students) {
                    textArea.setText(textArea.getText() + student.toString() + "\n");
                }
            }
        });
        BDAll.setOnAction(actionEvent -> {
            try {
                var mySqlObj = new MySqlParse();
                var result = mySqlObj.getAll();
                textArea.setText("");
                try {
                    while (result.next()) {
                        Student student = new Student(
                                result.getInt("id"),
                                result.getString("name"),
                                result.getString("surname"),
                                result.getString("patronymic"),
                                result.getString("school"),
                                result.getString("clas"),
                                result.getString("age")
                        );
                        textArea.setText(textArea.getText() + student.toString() + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        parseXMLtoBD.setOnAction(actionEvent -> {
            var sax = new SAXParse();
            var parsing = new Parsing(sax.readerSaxDocument(filePath));
            parsing.parseXMLtoDB();
        });

        parseBDtoXML.setOnAction(actionEvent -> {
            var sax = new SAXParse();
            var dom = new DomParse(filePath);
            var parsing = new Parsing(sax.readerSaxDocument(filePath), dom);
            parsing.parseDBtoXML();
        });

        btnId.setOnAction(actionEvent -> {
            var id = inputId.getText();
            if (!id.equalsIgnoreCase("0") && id.length() > 0 && !id.equalsIgnoreCase("null")) {
                var sax = new SAXParse();
                var student = sax.searchSaxDocument(filePath, id);
                textArea.setText(student != null ? student.toString() : "Такого студента нет!");
            }
        });

        btnIdDb.setOnAction(actionEvent -> {
            var id = inputIdDb.getText();
            if (!id.equalsIgnoreCase("0") && id.length() > 0 && !id.equalsIgnoreCase("null")) {
                try {
                    var mySqlObj = new MySqlParse();
                    var result = mySqlObj.searchRecord(Integer.parseInt(id));
                    while (result.next()) {
                        Student student = new Student(
                                result.getInt("id"),
                                result.getString("name"),
                                result.getString("surname"),
                                result.getString("patronymic"),
                                result.getString("school"),
                                result.getString("clas"),
                                result.getString("age")
                        );
                        textArea.setText(student != null ? student.toString() : "Такого ученика нет!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        addXML.setOnAction(actionEvent -> {
            HBox flowPane = getHbox();
            Stage stage2 = new Stage();
            Scene scene = new Scene(flowPane, 400, 400);
            stage2.setScene(scene);
            stage2.setTitle("Ученик");
            stage2.initModality(Modality.NONE);
            stage2.show();
        });


    }
}
