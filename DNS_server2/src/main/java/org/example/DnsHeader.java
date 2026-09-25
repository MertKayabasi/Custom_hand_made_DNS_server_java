package org.example;

import java.net.SocketException;
import java.nio.ByteBuffer;

public record DnsHeader(               //recordlar
                  int transactionId,
                  int flags,                  //12 byte header kayıtları
                  int questionCount,
                  int answerCount,
                  int authorityCount,
                  int additionalCount) {
    static DnsServer dnsServer;
    public static DnsHeader readheader(ByteBuffer buffer) {
        //DNS HEADER 12 BYTE
        int transactionId = Short.toUnsignedInt(buffer.getShort());
        int flags = Short.toUnsignedInt(buffer.getShort());     //her birini 2 byte okuyup ınt değere atıyor
        int questionCount = Short.toUnsignedInt(buffer.getShort()); //buffer dan okuyor 2 byte
        int answerCount = Short.toUnsignedInt(buffer.getShort()); //buffer verinin geçici olarak tutulduğu sırayla okunan alan
        int authorityCount = Short.toUnsignedInt(buffer.getShort());
        int additionalCount = Short.toUnsignedInt(buffer.getShort());
        return new DnsHeader(   //newleyerek değerler döndürülüyor
                transactionId,
                flags,
                questionCount,
                answerCount,
                authorityCount,
                additionalCount
        );
    }

    @Override
    public int transactionId() {
        return transactionId;
    }

    @Override
    public int flags() {
        return flags;
    }

    @Override
    public int questionCount() {
        return questionCount;
    }

    @Override
    public int answerCount() {
        return answerCount;
    }

    @Override
    public int authorityCount() {
        return authorityCount;
    }

    @Override
    public int additionalCount() {
        return additionalCount;
    }

    public static DnsServer getDnsServer() {
        return dnsServer;
    }

    public static void setDnsServer(DnsServer dnsServer) {
        DnsHeader.dnsServer = dnsServer;
    }
}
