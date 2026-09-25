package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        byte[] arr = new byte[512];

        DatagramPacket packet = new DatagramPacket(arr, arr.length);
        DnsServer server = new DnsServer();    //NESNE OLUŞTURMA
        FileReader client = new FileReader();   //NESNE OLUŞTURMA
        Map<String, String> map = client.readAndSend();  //MAP OLUŞTURMA
        System.out.println("Records = " + map);    //RECORLAR YAZDIRILIYOR
        //SERVER BAĞLANMA
        server.server();
        while (true) {

            server.getRequest(map, packet); //request kısmı

        }
    }
}