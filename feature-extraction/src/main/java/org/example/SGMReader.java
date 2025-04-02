package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SGMReader {
    private static final String FILE = "documents.ser";

    public static List<Document> readFiles(int start, int end) {
        File file = new File(FILE);
        if (file.exists()) {
            System.out.println("Wczytywanie dokumentów z cache...");
            return loadFromFile();
        }

        List<Document> extractedDocuments = new ArrayList<>();

       for (int i = start; i <= end; i++) {
           String fileName = String.format("reuters/reut2-%03d.sgm", i);
           System.out.println("Przetwarzanie pliku: " + fileName);

           try (InputStream is = SGMReader.class.getClassLoader().getResourceAsStream(fileName)) {
               if (is == null) {
                   throw new FileNotFoundException("File not found: " + fileName);
               }

               String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

               Pattern reutersPattern = Pattern.compile("<REUTERS.*?>(.*?)</REUTERS>", Pattern.DOTALL);
               Pattern placesPattern = Pattern.compile("<PLACES>(.*?)</PLACES>", Pattern.DOTALL);
               Pattern bodyPattern = Pattern.compile("<BODY>(.*?)</BODY>", Pattern.DOTALL);

               Set<String> allowedCountries = new HashSet<>(Arrays.asList("usa", "uk", "japan", "canada", "france", "west-germany"));

               Matcher reutersMatcher = reutersPattern.matcher(content);

               while (reutersMatcher.find()) {
                   String reutersContent = reutersMatcher.group(1);
                   Matcher placesMatcher = placesPattern.matcher(reutersContent);
                   Matcher bodiesMatcher = bodyPattern.matcher(reutersContent);

                   String place = extractPlaceFromContent(placesMatcher);
                   if (place != null && allowedCountries.contains(place)) {
                       String body = extractBodyFromContent(bodiesMatcher);
                       if (body != null) {

                           String cleanedBody = cleanBody(body);
                           Document doc = new Document(place, cleanedBody);
                           extractedDocuments.add(doc);
                       }
                   }
               }
               saveToFile(extractedDocuments);
           } catch (FileNotFoundException e) {
               System.err.println(e.getMessage());
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

        return extractedDocuments;
    }

    private static String extractPlaceFromContent(Matcher placesMatcher) {
        if (placesMatcher.find()) {
            List<String> places = extractPlace(placesMatcher.group(1));
            if (places.size() == 1) {
                return places.getFirst();
            }
        }
        return null;
    }

    private static String extractBodyFromContent(Matcher bodiesMatcher) {
        if (bodiesMatcher.find()) {
            return bodiesMatcher.group(1).trim();
        }
        return null;
    }

    private static String cleanBody(String body) {
        String decodedBody = decodeHTML(body);
        return decodedBody.replaceAll("(?i)Reuter[\\s\\S]*", "").trim();
    }


    private static List<String> extractPlace(String place) {
        Pattern placePattern = Pattern.compile("<D>(.*?)</D>");
        Matcher placeMatcher = placePattern.matcher(place);
        List<String> places = new ArrayList<>();

        while (placeMatcher.find()) {
            places.add(placeMatcher.group(1));
        }
        return places;
    }

    private static String decodeHTML(String text) {
        return text.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&");
    }

    private static List<Document> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            return (List<Document>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static void saveToFile(List<Document> documents) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(documents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
