package ru.stankin.doletovlab1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Lab1 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private File getMatrixFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Матрица смежности", "*.txt"));
        File file = fileChooser.showOpenDialog(stage);

        if (file == null)
            throw new IllegalStateException("No file chosen");

        return file;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        File matrixFile;
        try {
            matrixFile = getMatrixFile(primaryStage);
        } catch (IllegalStateException e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка открытия файла!").show();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent parent = loader.load();
        MainController controller = loader.getController();
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Долетов Фёдор, ЛР1");
        primaryStage.setMaximized(true);
        Alert loading = new Alert(Alert.AlertType.INFORMATION, "Загрузка...");
        loading.show();
        controller.initData(matrixFile);
        loading.close();
        primaryStage.show();
    }

}
