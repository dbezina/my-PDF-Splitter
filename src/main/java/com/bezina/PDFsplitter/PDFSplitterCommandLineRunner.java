package com.bezina.PDFsplitter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class PDFSplitterCommandLineRunner implements CommandLineRunner  {

        @Override
        public void run(String... args) throws Exception {
                // Открываем диалог выбора файла
                File pdfFile = PDFFileChooser.selectPdfFile();

                if (pdfFile == null) {
                        System.out.println("Файл не выбран.");
                        return;
                }

                // Спрашиваем у пользователя, какие страницы сохранить
                Scanner scanner = new Scanner(System.in);
                System.out.println("Введите номера страниц для сохранения (через запятую):");
                String input = scanner.nextLine();
                List<Integer> pages = Arrays.stream(input.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                       // .toList();

                // Спрашиваем у пользователя, куда сохранить файлы
                System.out.println("Введите директорию для сохранения новых PDF файлов:");
                String outputDir = scanner.nextLine();

                // Спрашиваем у пользователя шаблон имени выходного файла
                System.out.println("Введите шаблон имени выходного файла:");
                String nameTemplate = scanner.nextLine();

                // Разделяем и сохраняем PDF файл
                PDFSplitter.splitPdf(pdfFile, pages, outputDir,nameTemplate);

                System.out.println("PDF файлы успешно сохранены.");
        }
}
