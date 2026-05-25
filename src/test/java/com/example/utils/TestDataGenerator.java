package com.example.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TestDataGenerator {

    private static final Random random = new Random();

    public static String uniqueCategoryName(String prefix) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HHmmss"));

        int number = random.nextInt(90) + 10; // always 2 digits

        String shortPrefix = prefix.length() > 3 ? prefix.substring(0, 3) : prefix;

        // Example: Cat231045 = 9 characters
        return shortPrefix + timestamp.substring(2, 6) + number;
    }

    public static String duplicateTestCategoryName() {
        return uniqueCategoryName("Dup");
    }

    public static String invalidShortCategoryName() {
        return "AB";
    }

    public static String nonExistingSearchName() {
        return "Inv" + random.nextInt(999);
    }
}