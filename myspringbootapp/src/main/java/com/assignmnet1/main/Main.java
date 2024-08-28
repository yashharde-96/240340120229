package com.assignmnet1.main;

import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN> <JSON file path>");
            return;
        }

        String prnNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];

        try {
            JsonObject jsonObject = JsonParser.parseReader(new FileReader(jsonFilePath)).getAsJsonObject();

            String destinationValue = findDestinationValue(jsonObject);
            if (destinationValue == null) {
                System.out.println("No 'destination' key found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);

            String concatenatedString = prnNumber + destinationValue + randomString;

            String md5Hash = DigestUtils.md5Hex(concatenatedString);
            
            System.out.println(md5Hash + ";" + randomString);
        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        }
    }

    private static String findDestinationValue(JsonObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            JsonElement element = jsonObject.get(key);

            if (key.equals("destination")) {
                return element.getAsString();
            }

            if (element.isJsonObject()) {
                String result = findDestinationValue(element.getAsJsonObject());
                if (result != null) {
                    return result;
                }
            } else if (element.isJsonArray()) {
                for (JsonElement arrayElement : element.getAsJsonArray()) {
                    if (arrayElement.isJsonObject()) {
                        String result = findDestinationValue(arrayElement.getAsJsonObject());
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;  
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
}
}