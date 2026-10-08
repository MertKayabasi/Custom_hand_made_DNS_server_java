package org.example;

import java.io.IOException;
import java.net.DatagramPacket;

public interface Responder {

    void sendResponse(DatagramPacket packet, DnsHeader header, DnsQuery query, byte[] res, int questionEnd) throws IOException;

    void sendResponse(DatagramPacket packet, int questionEnd, int rcode) throws IOException;

    void sendResponse(DatagramPacket packet, int questionEnd) throws IOException;
}
