package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileSaver {
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 2 + 1024 * 256;

    public static void saveDocumentsToFile(List<Document> documents, String filePath) {
        int fileIndex = 0;
        File file = new File(filePath);
        FileWriter writer;
        int docIndex = 0;

        try {
            writer = new FileWriter(file);

            for (Document doc : documents) {
                if (file.length() > MAX_FILE_SIZE) {
                    writer.close();
                    fileIndex++;
                    file = new File(getNewFileName(filePath, fileIndex));
                    writer = new FileWriter(file);
                }

                writer.write("=== Dokument "+ doc.getIndex() +"===\n");
                writer.write("Miejsce: " + doc.getPlace() + "\n");
                writer.write("Treść:\n" + doc.getBody() + "\n\n");
                docIndex++;
            }

            writer.close();
            System.out.println("Zapisano " + docIndex + " dokumentów w plikach: " + filePath);
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisu do pliku!");
            e.printStackTrace();
        }
    }

    private static String getNewFileName(String filePath, int fileIndex) {
        String[] parts = filePath.split("\\.");
        return parts[0] + "_" + fileIndex + "." + parts[1];
    }

    public static void saveExtractedFeatures(List<ExtractedFeatures> features, String file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(features);
            System.out.println("Lista ExtractedFeatures została zapisana do pliku.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
