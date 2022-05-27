package ru.digital.services.sp.Utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Utils {
    public static ArrayList<String> generateWords() {
        ArrayList<String> result = new ArrayList<>();

        try (Scanner s = new Scanner(new FileReader("src/main/java/ru/digital/services/sp/RussianWords.txt"))) {
            while (s.hasNext()) {
                result.add(s.nextLine());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Генератор случайной строки
    private static final String ALPHA_NUMERIC_STRING_RU =
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789";
    private static final String ALPHA_NUMERIC_STRING_EN =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String randomAlphaNumericRU(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = new Random().nextInt(ALPHA_NUMERIC_STRING_RU.length());
            builder.append(ALPHA_NUMERIC_STRING_RU.charAt(character));
        }
        return builder.toString();
    }

    public static String randomAlphaNumericEN(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = new Random().nextInt(ALPHA_NUMERIC_STRING_EN.length());
            builder.append(ALPHA_NUMERIC_STRING_EN.charAt(character));
        }
        return builder.toString();
    }

    public static ArrayList<String> words = generateWords();

    public static String randomRussianWords(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = new Random().nextInt(words.size());
            builder.append((words.get(character)));
            if (count != 0) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    //Returns phone in format: +7(800)555-35-35
    public static String randomRussianPhone() {
        return "+7("
                + randomInt(3)
                + ")"
                + randomInt(3)
                + "-"
                + randomInt(2)
                + "-"
                + randomInt(2);
    }

    public static Float randomFloat(int wholeDigits) {
        int firstDigit = (int) Math.round(Math.random() * 10);
        if (wholeDigits == 1)
            return (float) (firstDigit + Math.random());

        if (firstDigit == 0)
            firstDigit = 1;

        return Float.valueOf(String.valueOf(firstDigit) + Math.random() * Math.pow(10, wholeDigits - 1));
    }

    public static Integer randomInt(int wholeDigits) {
        int firstDigit = (int) Math.round(Math.random() * 10);
        if (wholeDigits == 1)
            return firstDigit;

        if (firstDigit == 0)
            firstDigit = 1;

        return Integer.valueOf(firstDigit + String.valueOf(Math.round(Math.random() * Math.pow(10, wholeDigits - 1))));
    }

    public static String randomEmail(String domain) {
        return randomAlphaNumericEN(254) + "@" + randomAlphaNumericEN(10)
                + "." + (domain != null ? domain : "ru");
    }

    public static String getBase64ImageDate() {
        //name dots5x5.jpg
        //mimeType image/jpeg
        //size 796
        return "/9j/4AAQSkZJRgABAQEBLAEsAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQ" +
                "EBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2" +
                "wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB" +
                "AQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAAFAAUDASIAAhEBAxEB/8QAHwAAAQU" +
                "BAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQ" +
                "IDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoK" +
                "So0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJ" +
                "ipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uH" +
                "i4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBg" +
                "cICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIF" +
                "EKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RV" +
                "VldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKm" +
                "qsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9" +
                "oADAMBAAIRAxEAPwD+sz9m3WPEmhftUftV/s/+F9R0TSNG8LeDfhf+0LYWZ8CeC" +
                "LPwRocf7QX7R37dPgrUvD3hbQ/B2heC/iPeeKdb8T/s6+Ivjb8bfid8YvjH8ada" +
                "+Lfxq+NnjnxV4c0r4R6Mr+GNVKKKmEVCKinJpXs5znUlq29ZzlKT30vJ2VkrJJH" +
                "0uc5tio5tmcFSy3lpY7FUaaeS5O3GlQrTo0o3eBbfJThGN5Nydrybbbf/2Q==";
    }

    public static String logSearchingInArrayAssertMessage(List<Object> list, String s) {
        return "Искали: " + s + " в списке: " + list;
    }
}