package com.bezina.PDFsplitter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * JavaFX PDFSplitterApplication
 */
@SpringBootApplication
public class PDFSplitterApplication extends Application {

    private File selectedFile;
    private Label fileLabel;
    private TextField pagesField;
    private TextField nameTemplateField;
    private Label messageLabel;

    @Override
    public void start(Stage stage) {
        Button openButton = new Button("Open PDF");
        fileLabel = new Label("File not selected");
        pagesField = new TextField();
        pagesField.setPromptText("Enter pages (e.g., 1,2,5-10)");
        nameTemplateField = new TextField();
        nameTemplateField.setPromptText("Enter file name template (e.g., page-%d)");
        Button saveButton = new Button("Save");
        Button clearButton = new Button("Clear");
        messageLabel = new Label();

        openButton.setOnAction(event -> selectPdfFile(stage));
        saveButton.setOnAction(event -> savePdfFiles(stage));
        clearButton.setOnAction(event -> clearSelection());

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(openButton, fileLabel, pagesField, nameTemplateField, saveButton, clearButton, messageLabel);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("PDF Splitter");
        stage.show();

    }

    private void selectPdfFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            fileLabel.setText("Selected file: " + selectedFile.getName());
            messageLabel.setText("");
        } else {
            fileLabel.setText("File not selected");
        }
    }
    private void savePdfFiles(Stage stage) {
        if (selectedFile == null) {
            messageLabel.setText("No file selected");
            return;
        }

        String pagesText = pagesField.getText();
        if (pagesText.isEmpty()) {
            messageLabel.setText("No pages specified");
            return;
        }

        String nameTemplate = nameTemplateField.getText();
        if (nameTemplate.isEmpty()) {
            messageLabel.setText("No file name template specified, will be saved with page-% name");
            nameTemplate = "page";
          // return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            try {
                List<Integer> pages = parsePages(pagesText);
                PDFSplitter.splitPdf(selectedFile, pages, selectedDirectory.getAbsolutePath(), nameTemplate);
                messageLabel.setText("Files saved successfully");
            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error saving files");
            }
        } else {
            messageLabel.setText("No directory selected");
        }
    }
    private void clearSelection() {
        selectedFile = null;
        fileLabel.setText("File not selected");
        pagesField.clear();
        messageLabel.setText("");
        nameTemplateField.clear();
    }
    private List<Integer> parsePages(String pagesText) {
        try {

            return Arrays.stream(pagesText.split(","))
                    .flatMap(range -> {
                        if (range.contains("-")) {
                            String[] bounds = range.split("-");
                            int start = Integer.parseInt(bounds[0]);
                            int end = Integer.parseInt(bounds[1]);
                            if (start > 0 && end > 0 && start <= end) {
                                return IntStream.rangeClosed(start, end).boxed();
                            }
                            int page = Integer.parseInt(range);
                            if (page > 0) {
                                return IntStream.of(page).boxed();
                            }
                        }
                        return IntStream.empty().boxed();
                    })
                    .collect(Collectors.toList());
        }
        catch (NumberFormatException e) {
                return Collections.emptyList();
            }
    }

    public static void main(String[] args) {
        launch(args);
    }
}