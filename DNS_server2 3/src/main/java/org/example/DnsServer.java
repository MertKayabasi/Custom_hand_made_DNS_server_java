package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DnsServer implements Responder{

    private DatagramSocket socket;
    private ByteBuffer buffer;

    public void server() throws IOException {
        socket = new DatagramSocket(9003);
        System.out.println("Server opened.");
    }

    public void getRequest(Map<String, Map<RecordType, byte[]>> map, DatagramPacket packet) throws IOException {
        System.out.println("receiving...");
        socket.receive(packet);
        System.out.println("packet received");
        System.out.println("packet = " + packet.getLength());
        System.out.println("sender = " + packet.getPort());
        buffer = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
        DnsHeader header = DnsHeader.readheader(buffer);
        DnsQuery query = DnsQuery.readQuery(buffer);
        int questionEnd = buffer.position();
        String domain = domainNormalization(query.getDomain());
        if (query.getRecordType() != RecordType.A && query.getRecordType() != RecordType.MX) {
            System.out.println("record type not supported");
            sendResponse(packet, questionEnd, 4);
        }
        Map<RecordType, byte[]> records = map.get(domain);
        if (records.isEmpty()) {
            System.out.println("domain not found");
            sendResponse(packet, questionEnd, 3);
            return;
        }
        byte[] res = records.get(query.getRecordType());
        if (res == null) {
            System.out.println("record not found");
            sendResponse(packet, questionEnd);
            return;
        }
        String ip = new String(res, StandardCharsets.UTF_8).trim();
        if (query.getRecordType() == RecordType.A) {

            res = ipToBytes(ip);

            if (res == null) {
                System.out.println("very very bad ip unfortunately, sorry");
                sendResponse(packet, questionEnd, 2);
                return;
            }
        }
        System.out.println("record found");
        System.out.println("transaction ID = " + header.transactionId());
        System.out.println("domain = " + domain);
        System.out.println("ip = " + ip);
        System.out.println("record type = " + query.getRecordType());
        System.out.println("qCLASS = " + query.getQclass());
        sendResponse(packet, header, query, res, questionEnd);
    }
    @Override
    public void sendResponse(DatagramPacket packet, DnsHeader header, DnsQuery query, byte[] res, int questionEnd) throws IOException {
        ByteBuffer response = ByteBuffer.allocate(512);
        response.put(packet.getData(), 0, questionEnd);
        int flags = 32768;
        response.putShort(2, (short) flags);
        response.putShort(4, (short) 1);
        response.putShort(6, (short) 1);
        response.putShort(8, (short) 0);
        response.putShort(10, (short) 0);
        response.position(questionEnd);
        response.putShort((short) 0xC00C);
        response.putShort((short) query.getQtype());
        response.putShort((short) 1);
        response.putInt(60);
        if (query.getRecordType() == RecordType.A) {
            response.putShort((short) 4);
            response.put(res);
        }
        else if (query.getRecordType() == RecordType.MX) {
            String mxValue = new String(res, StandardCharsets.UTF_8).trim();
            String[] mx = mxValue.split(":", 2);
            int priority = Integer.parseInt(mx[0].trim());
            String mailServer = domainNormalization(mx[1]);
            ByteBuffer mxData = ByteBuffer.allocate(256);
            mxData.putShort((short) priority);
            writeDomain(mxData, mailServer);
            response.putShort((short) mxData.position());
            response.put(mxData.array(), 0, mxData.position());
        }
        byte[] responseData = new byte[response.position()];
        response.flip();
        response.get(responseData);
        DatagramPacket answer = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
        socket.send(answer);
        System.out.println("answer sent");
    }
    @Override
    public void sendResponse(DatagramPacket packet, int questionEnd, int rcode) throws IOException {
        ByteBuffer response = ByteBuffer.allocate(512);
        response.put(packet.getData(), 0, questionEnd);
        int flags = 32768 | rcode;
        response.putShort(2, (short) flags);
        response.putShort(4, (short) 1);
        response.putShort(6, (short) 0);
        response.putShort(8, (short) 0);
        response.putShort(10, (short) 0);
        byte[] responseData = new byte[response.position()];
        response.flip();
        response.get(responseData);
        DatagramPacket answer = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
        socket.send(answer);
        System.out.println("error response sent = " + rcode);
    }
    @Override
    public void sendResponse(DatagramPacket packet, int questionEnd) throws IOException {
        ByteBuffer response = ByteBuffer.allocate(512);
        response.put(packet.getData(), 0, questionEnd);
        int flags = 32768;
        response.putShort(2, (short) flags);
        response.putShort(4, (short) 1);
        response.putShort(6, (short) 0);
        response.putShort(8, (short) 0);
        response.putShort(10, (short) 0);
        byte[] responseData = new byte[response.position()];
        response.flip();
        response.get(responseData);
        DatagramPacket answer = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
        socket.send(answer);
        System.out.println("Error!!! no answer response sent!!!!");
    }

    private byte[] ipToBytes(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return null;
        }
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            int number;
            try {
                number = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                return null;
            }
            if (number < 0 || number > 255) {
                return null;
            }
            result[i] = (byte) number;
        }
        return result;
    }
    private void writeDomain(ByteBuffer buffer, String domain) {
        String[] labels = domain.split("\\.");

        for (String label : labels) {
            byte[] labelBytes = label.getBytes(StandardCharsets.UTF_8);
            buffer.put((byte) labelBytes.length);
            buffer.put(labelBytes);
        }
        buffer.put((byte) 0);
    }
    private String domainNormalization(String domain) {
        domain = domain.trim().toLowerCase();
        while (domain.endsWith(".")) {
            domain = domain.substring(0, domain.length() - 1);
        }
        return domain;
    }
}