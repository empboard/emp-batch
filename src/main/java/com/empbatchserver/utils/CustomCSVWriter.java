package com.empbatchserver.utils;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.util.List;

@Slf4j
public class CustomCSVWriter {
    public static int write(final String fileName, List<String[]> data) {
        int rows = 0;

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeAll(data);
            rows = data.size();
        } catch (Exception e) {
            log.error("CustomCSVWriter - write: CSV 파일 생성에 실패, fileName: {}", fileName);
        }

        return rows;
    }
}
