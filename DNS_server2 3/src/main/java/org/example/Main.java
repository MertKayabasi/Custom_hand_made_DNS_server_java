package org.example;
import java.net.DatagramPacket;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        FileReader fileReader = new FileReader();
        Map<String, Map<RecordType, byte[]>> map = fileReader.readAndSend();
        System.out.println("Records = " + map);
        DnsServer server = new DnsServer();
        server.server();
        while (true) {
            byte[] data = new byte[512];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            server.getRequest(map, packet);
        }
    }
}