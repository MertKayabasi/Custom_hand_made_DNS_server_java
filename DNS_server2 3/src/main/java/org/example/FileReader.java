package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileReader {
    public Map<String, Map<RecordType, byte[]>> readAndSend() throws IOException {
        Map<String, Map<RecordType, byte[]>> first = new HashMap<>();
        List<String> domainLines = Files.readAllLines(Path.of("/Users/mertkayabasi/Desktop/domain.txt"));
        for (String domainLine : domainLines) {
            if (domainLine.isEmpty()) {
                continue;
            }
            String[] domainAndIp = domainLine.split("=", 2);
            if (domainAndIp.length != 2) {
                continue;
            }
            String[] records = domainAndIp[0].split(":", 2);
            if (records.length != 2) {
                continue;
            }
            RecordType record = RecordType.valueOf(records[0].trim().toUpperCase());
            String domain = records[1].trim().toLowerCase();
            if (domain.endsWith(".")) {
                domain = domain.substring(0, domain.length() - 1
                );
            }
            String value = domainAndIp[1].trim();
            byte[] arr = value.getBytes(StandardCharsets.UTF_8);
            Map<RecordType, byte[]> second = first.get(domain);
            if (second == null) {
                second = new HashMap<>();
                first.put(domain, second);
            }
            second.put(record, arr);
        }
        return first;
    }
}