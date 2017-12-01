package joseph.shortenurl;

/**
 * URL을 변환하는 기능이 있는 클래스
 * 기존의 URL -> 데이터베이스의 키값을 62진법을 이용해 변환
 */

public class UrlShorten {
    private String codec = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public UrlShorten() {
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
}


