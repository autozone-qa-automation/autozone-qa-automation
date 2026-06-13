package com.autozone.tests.e2e.support;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CsvHelper {

    private CsvHelper() {
    }

    public static File waitForCsvFile(String downloadDir) {
        long deadline = System.currentTimeMillis() + 15_000;
        while (System.currentTimeMillis() < deadline) {
            File dir = new File(downloadDir);
            File[] csvFiles = dir.listFiles(f -> f.getName().endsWith(".csv"));
            if (csvFiles != null && csvFiles.length > 0) {
                // Return the most recently created file
                File latest = csvFiles[0];
                for (File f : csvFiles) {
                    if (f.lastModified() > latest.lastModified()) latest = f;
                }
                return latest;
            }
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        throw new RuntimeException("No CSV file found in download directory: " + downloadDir);
    }

    public static boolean noCsvDownloaded(String downloadDir) {
        try { Thread.sleep(3_000); } catch (InterruptedException ignored) {}
        File dir = new File(downloadDir);
        File[] csvFiles = dir.listFiles(f -> f.getName().endsWith(".csv"));
        return csvFiles == null || csvFiles.length == 0;
    }

    // Reads the header row (first line). Strips UTF-8 BOM and surrounding quotes.
    public static List<String> readHeaders(File csvFile) throws IOException {
        List<String> lines = Files.readAllLines(csvFile.toPath(), StandardCharsets.UTF_8);
        if (lines.isEmpty()) throw new RuntimeException("CSV file is empty: " + csvFile.getName());
        String headerLine = lines.get(0).replace("﻿", ""); // strip BOM
        return Arrays.stream(headerLine.split(","))
                .map(h -> h.trim().replace("\"", ""))
                .collect(Collectors.toList());
    }

    // Returns all data rows (excluding the header).
    public static List<String> readDataRows(File csvFile) throws IOException {
        List<String> lines = Files.readAllLines(csvFile.toPath(), StandardCharsets.UTF_8);
        if (lines.size() <= 1) return List.of();
        return lines.subList(1, lines.size());
    }

    public static void deleteAllCsvFiles(String downloadDir) {
        File dir = new File(downloadDir);
        File[] csvFiles = dir.listFiles(f -> f.getName().endsWith(".csv"));
        if (csvFiles != null) {
            for (File f : csvFiles) f.delete();
        }
    }
}