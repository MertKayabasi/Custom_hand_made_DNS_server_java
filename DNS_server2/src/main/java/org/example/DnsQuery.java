package org.example;

import java.nio.ByteBuffer;

public class DnsQuery {
    private int qtype;
    private int qclass;
    private String domain;
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

    public DnsQuery(int qtype, int qclass, String domain) {
        this.qtype = qtype; //A mı MX mi
        this.qclass = qclass;    //ınter net mi değil mi
        this.domain = domain;    //domain
    }

    public static DnsQuery readQuery(ByteBuffer buffer) {
        String domain = readDomainName(buffer);
        int qtype = Short.toUnsignedInt(buffer.getShort());  //Dns question = istemci ne soruyor
        int qclass = Short.toUnsignedInt(buffer.getShort()); //qname ve qtype burada okunuyor
        return new DnsQuery(qtype, qclass, domain);       //newleyerek bu nesneler constructorında başlatılıp dönüyor
                                                            //bi nevi nesenler metoddan okunup constructora veriliyor
    }
    public static String readDomainName(ByteBuffer buffer) {   //domaın name burada okunuyor
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
    }

}
