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

    public void setQtype(int qtype) {
        this.qtype = qtype;
    }

    public int getQclass() {
        return qclass;
    }

    public void setQclass(int qclass) {
        this.qclass = qclass;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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
   /*public static String readDomainName(ByteBuffer buffer) {   //domaın name burada okunuyor
        int length = Byte.toUnsignedInt(buffer.get());
        StringBuilder domainName = new StringBuilder();    //STRINGBUILDER KULLANDIM APPEND METHODU İÇİN
        while (length > 0) {
            if (domainName.length() > 0) {     //length > 0 ise domaın name e "." ekle domaının ilk kısmı okundu çünkü
                domainName.append("."); //nokta ekle
            }
            for (int i = 0; i < length; i++) {
                Byte part = buffer.get();  //buffer dan her turda bir byte al
                domainName.append((char) part.byteValue()); // domaın name içeriğindeki veri append ediliyor
            }
            length = Byte.toUnsignedInt(buffer.get());     //uzunluk byttan ınt e dönüşüyor
        }
        return domainName.toString();  //strıng buılder strınge dönüşüp domaın adı return ediliyor.
    }*/


    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }
}
