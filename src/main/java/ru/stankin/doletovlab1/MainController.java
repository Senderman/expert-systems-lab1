package ru.stankin.doletovlab1;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class MainController {

    @FXML
    public RadioButton listDepthSearch;
    @FXML
    public RadioButton recurseDepthSearch;
    @FXML
    public RadioButton modDepthSearch;
    @FXML
    private TextField startNodeNumber;
    @FXML
    private TextField goalNodeNumber;
    @FXML
    private TextField breadthSearchResult;
    @FXML
    private TextField breadthSearchSteps;
    @FXML
    private TextField depthSearchResult;
    @FXML
    private TextField depthSearchSteps;
    @FXML
    private GridPane graphMatrix;
    @FXML
    private ImageView graphImageView;

    private Graph graph;

    @FXML
    private void initialize() {
        ToggleGroup depthSearchToggleGroup = new ToggleGroup();
        listDepthSearch.setToggleGroup(depthSearchToggleGroup);
        recurseDepthSearch.setToggleGroup(depthSearchToggleGroup);
        modDepthSearch.setToggleGroup(depthSearchToggleGroup);
    }

    public void initData(File file) {
        new Thread(() -> {
            final int[][] matrix;
            try {
                matrix = loadMatrixFromFile(file);
            } catch (IOException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Ошибка загрузки файла!").show());
                return;
            }
            this.graph = new Graph(matrix);
            Platform.runLater(() -> {
                graphImageView.setImage(SwingFXUtils.toFXImage(graph.getGraphImage(), null));
                fillUi(matrix);

            });
        }).start();

    }

    private void fillUi(int[][] matrix) {
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            // Вывести номер ряда
            var rowLabel = new TextArea(String.valueOf(rowIndex));
            rowLabel.setMaxSize(2, 2);
            rowLabel.setEditable(false);
            rowLabel.setPrefSize(5, 5);
            rowLabel.setStyle("-fx-font-weight: bold");
            graphMatrix.add(rowLabel, 0, rowIndex + 1);
            for (int colIndex = 0; colIndex < matrix.length; colIndex++) {
                // вывести номер столбца
                var columnLabel = new TextArea(String.valueOf(colIndex));
                columnLabel.setMaxSize(2, 2);
                columnLabel.setEditable(false);
                columnLabel.setStyle("-fx-font-weight: bold");
                graphMatrix.add(columnLabel, colIndex + 1, 0);
                // вывести элемент матрицы
                var elemLabel = new TextArea(String.valueOf(matrix[rowIndex][colIndex]));
                elemLabel.setMaxSize(2, 2);
                elemLabel.setEditable(false);
                graphMatrix.add(elemLabel, colIndex + 1, rowIndex + 1);
            }
        }
    }

    /**
     * Берет матрицу смежности из файла и конвертирует ее в матрицу интов
     *
     * @param file файл из которого прочитать
     * @return матрица в виде int[][]
     * @throws IOException если произошла ошибка чтения файла
     */
    private int[][] loadMatrixFromFile(File file) throws IOException {
        return Files.lines(file.toPath())
                .map(line -> line.chars().map(Character::getNumericValue).toArray())
                .toArray(int[][]::new);
    }


    @FXML
    private void onClearSearchButtonClick() {
        startNodeNumber.setText("");
        goalNodeNumber.setText("");
        breadthSearchResult.setText("");
        breadthSearchSteps.setText("");
        depthSearchSteps.setText("");
        depthSearchResult.setText("");
    }


    @FXML
    private void onWidthSearchButtonClick() {
        new Thread(() -> {
            int startVertex, goalVertex;
            try {
                startVertex = Integer.parseInt(startNodeNumber.getText());
                goalVertex = Integer.parseInt(goalNodeNumber.getText());
            } catch (NumberFormatException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Неверные данные!").show());
                return;
            }
            var results = graph.breadthSearch(startVertex, goalVertex);
            Platform.runLater(() -> {
                breadthSearchResult.setText(results.getPath());
                breadthSearchSteps.setText(String.valueOf(results.getSteps()));
            });
        }).start();
    }

    @FXML
    private void onDeepSearchButtonClick() {
        new Thread(() -> {
            int startVertex, goalVertex;
            try {
                startVertex = Integer.parseInt(startNodeNumber.getText());
                goalVertex = Integer.parseInt(goalNodeNumber.getText());
            } catch (NumberFormatException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Неверные данные!").show());
                return;
            }
            SearchResults results;
            if (listDepthSearch.isSelected())
                results = graph.listDepthSearch(startVertex, goalVertex);
            else if (recurseDepthSearch.isSelected())
                results = graph.recurseDepthSearch(startVertex, goalVertex);
            else if (modDepthSearch.isSelected()) {
                results = graph.modRecurseDepthSearch(startVertex, goalVertex);
            } else {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Не выбран способ!").show());
                return;
            }
            Platform.runLater(() -> {
                depthSearchResult.setText(results.getPath());
                depthSearchSteps.setText(String.valueOf(results.getSteps()));
            });
        }).start();
    }

}
