package com.bezina.PDFsplitter;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PDFFileChooser extends Application {

    private static CompletableFuture<File> fileChooserFuture;

    @Override
    public void start(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        fileChooserFuture.complete(selectedFile);

        // Закрываем JavaFX приложение после выбора файла
        primaryStage.close();
    }

    public static File selectPdfFile() throws InterruptedException, ExecutionException {
        fileChooserFuture = new CompletableFuture<>();

        // Запускаем JavaFX приложение
        new Thread(() -> Application.launch(PDFFileChooser.class)).start();

        // Ждем результата выбора файла
        return fileChooserFuture.get();
    }

    public static void main(String[] args) {
       // launch(args);
      try {
            File pdfFile = PDFFileChooser.selectPdfFile();
            if (pdfFile != null) {
                System.out.println("Selected file: " + pdfFile.getAbsolutePath());
            } else {
                System.out.println("No file selected");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
