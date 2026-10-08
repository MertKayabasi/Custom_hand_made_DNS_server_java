package org.example;

import java.nio.ByteBuffer;

public class DnsQuery {
    private int qtype;
    private int qclass;
    private static String domain;
    private RecordType recordType;
   //AŞAĞIDA GETTER VE SETTER LAR VAR
    public int getQtype() {
        return qtype;
    }
    public int getQclass() {
        return qclass;
    }
    public String getDomain() {
        return domain;
    }
    public DnsQuery(int qtype, int qclass, String domain, RecordType recordType) {
        this.qtype = qtype;
        this.qclass = qclass;
        this.domain = domain;
        this.recordType = recordType;
    }

    public static DnsQuery readQuery(ByteBuffer buffer) {
        StringBuilder domainBuilder = new StringBuilder();
        while (true) {
            int length = Byte.toUnsignedInt(buffer.get());
            if (length == 0) {
                break;
            }
            byte[] domain = new byte[length];
            buffer.get(domain);
            if (!domainBuilder.isEmpty()) {
                domainBuilder.append(".");
            }
            domainBuilder.append(new String(domain));
        }
        String domain = domainBuilder.toString();
        int qtype = Short.toUnsignedInt(buffer.getShort());
        RecordType recordType = null;
        if (qtype == 1) {
            recordType = RecordType.A;
        } else if (qtype == 15) {
            recordType = RecordType.MX;
        }
        int qclass = Short.toUnsignedInt(buffer.getShort());
        return new DnsQuery(qtype, qclass, domain, recordType);
    }
    public RecordType getRecordType() {
        return recordType;
    }
}
