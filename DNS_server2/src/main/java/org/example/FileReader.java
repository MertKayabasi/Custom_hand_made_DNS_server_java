package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileReader {
    DnsServer data;
    public Map<String, String> readAndSend() throws IOException {
        Map<String, String> equaling = new HashMap<>();   //map oluşturma
        List<String> domainLines = Files.readAllLines(
                Path.of("/Users/mertkayabasi/Desktop/domain.txt")  //bu pathde ki dosyadan satır satır okuyor
        );
        for (String domainLine : domainLines) {
            if (domainLine.isEmpty()) {
                continue;
            }
            String[] domainAndIp = domainLine.split("=", 2);  // = ifadesini ayırıyor
            if (domainAndIp.length != 2) {
                continue;
            }
            String domain = domainAndIp[0].trim();     /*burada gelen istek üzerine dosyadan
                                                        gelen veri okunuyor ve okunan verıler dig komutundan gelen
                                                        istek ile karşılaştırılıp resolve cevabı dönüyor*/
            String ip = domainAndIp[1].trim();
            equaling.put(domain, ip);
        }
        return equaling;   // map dönüyor içinde dosya verileri var.
    }
}