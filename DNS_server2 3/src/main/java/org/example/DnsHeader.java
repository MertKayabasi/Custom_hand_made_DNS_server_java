package org.example;
import java.nio.ByteBuffer;

public record DnsHeader(               //recordlar
                  int transactionId,
                  int flags,                  //12 byte header kayıtları
                  int questionCount,
                  int answerCount,
                  int authorityCount,
                  int additionalCount) {
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
}
