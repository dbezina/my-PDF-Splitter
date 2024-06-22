package com.bezina.PDFsplitter;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFSplitter {

   public static void splitPdf(File file, List<Integer> pages, String outputDir, String nameTemplate) throws IOException {
       try (PDDocument document = PDDocument.load(file)) {
           Splitter splitter = new Splitter();
           List<PDDocument> splitPages = splitter.split(document);
           int pageIndex = 1;
           for (PDDocument pageDoc : splitPages) {
               if (pages.contains(pageIndex)) {
                   pageDoc.save(new File(outputDir, nameTemplate + "-" + pageIndex + ".pdf"));
               }
               pageDoc.close();
               pageIndex++;
           }
       }
   }
}
