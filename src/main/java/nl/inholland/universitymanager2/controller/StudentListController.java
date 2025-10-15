package nl.inholland.universitymanager2.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.inholland.universitymanager2.Application;
import nl.inholland.universitymanager2.data.Database;
import nl.inholland.universitymanager2.model.Grade;
import nl.inholland.universitymanager2.model.Student;

import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentListController implements Initializable {

    @FXML
    TableView studentTableView;
    @FXML
    TableView gradeTableView;
    @FXML
    TableColumn<Student, String> coachColumn;

    private Database database;
    private ObservableList<Student> students;
    private Student selectedStudent;

    public StudentListController(Database database) {
        this.database = database;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        students = FXCollections.observableArrayList(database.getStudents());

        studentTableView.setItems(students);
        coachColumn.setCellValueFactory(tv -> Bindings.createStringBinding(() -> (tv.getValue().getGroup().getCoach().getFirstName()) + " " + (tv.getValue().getGroup().getCoach().getLastName())));

        studentTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null){
                selectedStudent = (Student) studentTableView.getSelectionModel().getSelectedItem();

                gradeTableView.setItems(FXCollections.observableArrayList(selectedStudent.getGrades())  );

            }
        });
    }

    public void onAddStudentClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("student-dialog-view.fxml"));
            StudentDialogController studentDialogController = new StudentDialogController(database);
            fxmlLoader.setController(studentDialogController);
            Scene scene = new Scene(fxmlLoader.load());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(scene);
            dialog.setTitle("Add student");
            dialog.showAndWait();

            if (studentDialogController.getStudent() != null) {
                students.add(studentDialogController.getStudent());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onAddGradeClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("grade-dialog-view.fxml"));
            GradeDialogController gradeDialogController = new GradeDialogController();
            fxmlLoader.setController(gradeDialogController);
            Scene scene = new Scene(fxmlLoader.load());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(scene);
            dialog.setTitle("Add grade");
            dialog.showAndWait();

            if (gradeDialogController.getGrade() != null){
                int index = students.indexOf(selectedStudent);
                students.get(index).addGrade(gradeDialogController.getGrade());
                gradeTableView.setItems(FXCollections.observableArrayList(students.get(index).getGrades())  );
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
