package joseph.shortenurl;

import java.util.zip.CRC32;

/**
 * Created by Semin on 2017-11-30.
 */

public class UrlShorten {
    private String codec = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private long id;

    public UrlShorten() {
    }

    public long getCRCHashValue(String originalUrl) {
        CRC32 crc = new CRC32();
        crc.update(originalUrl.getBytes());
        System.out.println(crc.getValue());
        id = crc.getValue();
        return crc.getValue();
    }

    public String toBase62(long hashDigit) {
        StringBuilder tempSb = new StringBuilder();
        int mod;

        while (hashDigit > 0) {
            mod = (int) (hashDigit % 62);
            hashDigit = hashDigit / 62;
            //System.out.println(mod);
            tempSb.insert(0, codec.charAt(mod));
        }

        return tempSb.toString();
    }

    public long fromBase62(String s) {
        long result=0;
        long power=1;
        for (int i = s.length()-1; i>=0 ; i--) {
            int digit = codec.indexOf(s.charAt(i));
            result += digit * power;
            power *= 62;
        }
        return result;
    }

    public String getShortUrl(String originalUrl) {
        long hashDigit = getCRCHashValue(originalUrl);
        return toBase62(hashDigit);
    }

    public long getId() {
        return id;
    }

}


