package org.example;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class DnsServer {
    private DatagramSocket socket;
    private ByteBuffer buffer;
    public void server() throws IOException {
        socket = new DatagramSocket(9002);    //UDP Server oluşturuluyor
        System.out.println("Server opened.");
    }
    public void getRequest(Map<String, String> map, DatagramPacket packet) throws IOException {

        System.out.println("receiving...");
        socket.receive(packet);

        System.out.println("packet received");
        System.out.println("packet = " + packet.getLength());
        System.out.println("sender = " + packet.getPort());

        buffer = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());

        DnsHeader header = DnsHeader.readheader(buffer);
        DnsQuery query = DnsQuery.readQuery(buffer);

        int questionEnd = buffer.position();

        String key = "";

        if (query.getQtype() == 1) {
            key = "A:" + query.getDomain();
        }
        else if (query.getQtype() == 15) {
            key = "MX:" + query.getDomain();
        }

        String res = map.get(key);

        System.out.println("KEY = " + key);
        System.out.println("RES = " + res);

        // DOSYADA VAR
        if (res != null) {

            System.out.println("resolved from file = " + res);

            DnsServerResponse(
                    packet,
                    header,
                    query,
                    res,
                    questionEnd
            );
        }

        // DOSYADA YOK
        else {

            System.out.println("not found in file, forwarding...");

            forwardDns(packet);
        }

        System.out.println("transaction ID = " + header.transactionId());
        System.out.println("flags = " + header.flags());
        System.out.println("domain = " + query.getDomain());
        System.out.println("qTYPE = " + query.getQtype());
        System.out.println("qCLASS = " + query.getQclass());
    }
    public void DnsServerResponse(DatagramPacket packet, DnsHeader header,
                             DnsQuery query, String res,
                             int questionEnd) throws IOException {
        ByteBuffer response = ByteBuffer.allocate(512);
        response.put(packet.getData(), 0, questionEnd);
        response.putShort(2, (short) (header.flags() | 32768));//2.bayttan başlayarak ilerler.  //dns header i sorgu değil cevap olarak belirtiyoruz.
        //orlayarak tüm bitleri değiştirip son biti 1 yapıyoruz ve bu bir cevap oluyor
        response.putShort(6, (short) 1); //6.indis bunun bulunduğu yer bunun answer olduğu anlamına geliyor
        response.putShort(8, (short) 0); //8.indis AUTHORİTY count yok bu nedenle
        response.putShort(10, (short) 0); //10.indis Additional count yok ondan 0
        response.position(questionEnd);   //buffer ın positionu son a alındı
        response.putShort((short) 49164);
        response.putShort((short) query.getQtype());
        response.putShort((short) 1);
        response.putInt(60); //TTL süresi cache de 60 saniye tutulacak
        if (query.getQtype() == 1) {   //qtype = mx mi yoksa domaın midir?
            byte[] ip = InetAddress.getByName(res).getAddress(); //1 = domaın burada ip yi byte dizisine atıyoruz dönüştürüp
            response.putShort((short) 4); //gelecek  veri 4 byte uzunluğunda ip geliyor yani
            response.put(ip); //ip geldi
        }
        else if (query.getQtype() == 15) {  //queryType 15 = MX kaydı
            String[] mx = res.split(":", 2);   //Strıngı : bu atılmış susbtrınge dönüştürdü uzunluk limiti 2
            ByteBuffer data = ByteBuffer.allocate(256); // buffer da 256 lık alan aç
            data.putShort(Short.parseShort(mx[0])); //ilk kısım ":" öncesi
            writeDomain(data, mx[1]); //mx mail domaın

            response.putShort((short) data.position()); //kaç byte buffer a yazıldı
            response.put(data.array(), 0, data.position()); // buffer ın başından beri veriyi array e koy
        }
        DatagramPacket answer = new DatagramPacket( //answer paketi oluşturuluyor
                response.array(),
                response.position(),    //socket.send DatagramSocket döndüğü için önce bu veriler
                packet.getAddress(),    //yani answer yapısını datagram socket olarak paketliyoruz
                packet.getPort()
        );
        socket.send(answer);  //artık answer paketi dig komutunun answer kısmına yollanıyor
    }
    public void writeDomain(ByteBuffer buffer, String domain) { //MX Yanıtı için özel metod

        for (String part : domain.split("\\.")) { //regex ifade domaını buna göre parçalara bölüyor
            buffer.put((byte) part.length());     //byte formatında partın lengthını putluyor buffer a
            buffer.put(part.getBytes()); //buffer a domaın yazılıyor
        }
        buffer.put((byte) 0); //domaın bitti 0 a gelindi
    }
    public void mXDomain(DatagramPacket packet, DnsHeader header, int questionEnd) throws IOException {

        ByteBuffer response = ByteBuffer.allocate(512);


        response.put(packet.getData(), 0, questionEnd);

        // Response + NXDOMAIN
        int flags = 32768 | (header.flags() & 256) | 3;
        response.putShort(2, (short) flags);

        // Answer yok
        response.putShort(6, (short) 1);
        response.putShort(8, (short) 0);
        response.putShort(10, (short) 0);
        response.putInt(60);
        //response.put(response.array(), 0, response.position());
        DatagramPacket answer = new DatagramPacket(
                response.array(),
                response.position(),
                packet.getAddress(),
                packet.getPort()
        );

        socket.send(answer);
    }
    public void forwardDns(DatagramPacket packet) throws IOException {

        DatagramSocket dnsSocket = new DatagramSocket();

        DatagramPacket request = new DatagramPacket(
                packet.getData(),
                packet.getLength(),
                InetAddress.getByName("1.1.1.1"),
                53
        );

        dnsSocket.send(request);

        byte[] data = new byte[512];

        DatagramPacket response =
                new DatagramPacket(data, data.length);

        dnsSocket.receive(response);

        DatagramPacket answer = new DatagramPacket(
                response.getData(),
                response.getLength(),
                packet.getAddress(),
                packet.getPort()
        );

        socket.send(answer);

        dnsSocket.close();
    }
}