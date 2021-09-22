import me.leon.encode.base.Base64Kt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 明文I Love You小可爱无密匙加密后密文为U2FsdGVkX1/ouFei55jKdzY1fWNS4jxHVNf/AfKWjnBrOGY=
 * 明文I Love You 521无密匙加密后密文为U2FsdGVkX19DvuEo5PvBA8TuLrM2t+EZBvUkzlAa
 * 明文I Love You 521密匙为666加密后密文为U2FsdGVkX18w6vxXxux/ivRVwo3xMzTxmUyk7cHz
 */
public class RabbitTest {
    public static void main(String[] args) {
        RabbitTest test = new RabbitTest();
//        test.test(new Rabbit());
        byte[] key = "6666666666666666666".getBytes(StandardCharsets.UTF_8);

        byte[] data = "I Love You 521".getBytes(StandardCharsets.UTF_8);
        byte[] iv = null;
//        iv = "".getBytes(StandardCharsets.UTF_8);
        Rabbit rabbit = new Rabbit();
        rabbit.reset();
        rabbit.setupKey(key);
        if (iv != null)
            rabbit.setupIV(iv);
        byte[] crypt = rabbit.crypt(data.clone());
        System.out.println(Base64Kt.base64(crypt,Base64Kt.BASE64_DICT));
        rabbit.reset();
        rabbit.setupKey(key);
        if (iv != null)
            rabbit.setupIV(iv);
        rabbit.crypt(crypt);
    }

    private byte[] convertData(String... data) {
        byte[] array = new byte[data.length * 16];
        int i = 0;
        for (String tdata : data) {
            for (String value : tdata.split(" ")) {
                array[i++] = (byte) (Integer.parseInt(value, 16) & 0xFF);
            }
        }
        return array;
    }

    private byte[] convertIVData(String data) {
        byte[] array = new byte[8];
        int i = 0;
        for (String value : data.split(" ")) {
            array[i++] = (byte) (Integer.parseInt(value, 16) & 0xFF);
        }
        return array;
    }

    private void test(Rabbit rabbit, byte[] key, byte[] iv, byte[] data) {
        rabbit.reset();
        rabbit.setupKey(key);
        if (iv != null)
            rabbit.setupIV(iv);
        byte[] crypt = rabbit.crypt(data.clone());
        rabbit.reset();
        rabbit.setupKey(key);
        if (iv != null)
            rabbit.setupIV(iv);
        rabbit.crypt(crypt);
        if (Arrays.equals(data, crypt)) {
            System.out.println("SUCCESS");
        } else {
            System.out.println("FAILED");
        }
    }

    public void test(Rabbit rabbit) {
        /* Appendix A: Test Vectors */
        /* A.1. Testing without IV Setup */
        System.out.println("Appendix A: Test Vectors");
        System.out.println();
        System.out.println("A.1. Testing without IV Setup");

        //TEST 1
        System.out.print("\tTEST 1... ");
        test(rabbit,
                convertData("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),
                null,
                convertData(
                        "B1 57 54 F0 36 A5 D6 EC F5 6B 45 26 1C 4A F7 02",
                        "88 E8 D8 15 C5 9C 0C 39 7B 69 6C 47 89 C6 8A A7",
                        "F4 16 A1 C3 70 0C D4 51 DA 68 D1 88 16 73 D6 96"));

        System.out.print("\tTEST 2... ");
        test(rabbit,
                convertData("91 28 13 29 2E 3D 36 FE 3B FC 62 F1 DC 51 C3 AC"),
                null,
                convertData(
                        "3D 2D F3 C8 3E F6 27 A1 E9 7F C3 84 87 E2 51 9C",
                        "F5 76 CD 61 F4 40 5B 88 96 BF 53 AA 85 54 FC 19",
                        "E5 54 74 73 FB DB 43 50 8A E5 3B 20 20 4D 4C 5E"));

        System.out.print("\tTEST 3... ");
        test(rabbit,
                convertData("83 95 74 15 87 E0 C7 33 E9 E9 AB 01 C0 9B 00 43"),
                null,
                convertData(
                        "0C B1 0D CD A0 41 CD AC 32 EB 5C FD 02 D0 60 9B",
                        "95 FC 9F CA 0F 17 01 5A 7B 70 92 11 4C FF 3E AD",
                        "96 49 E5 DE 8B FC 7F 3F 92 41 47 AD 3A 94 74 28"));

        System.out.println();
        System.out.println("A.2. Testing with IV Setup");

        System.out.print("\tTEST 1... ");
        test(rabbit,
                convertData("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),
                convertIVData("00 00 00 00 00 00 00 00"),
                convertData(
                        "C6 A7 27 5E F8 54 95 D8 7C CD 5D 37 67 05 B7 ED",
                        "5F 29 A6 AC 04 F5 EF D4 7B 8F 29 32 70 DC 4A 8D",
                        "2A DE 82 2B 29 DE 6C 1E E5 2B DB 8A 47 BF 8F 66"));

        System.out.print("\tTEST 2... ");
        test(rabbit,
                convertData("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),
                convertIVData("C3 73 F5 75 C1 26 7E 59"),
                convertData(
                        "1F CD 4E B9 58 00 12 E2 E0 DC CC 92 22 01 7D 6D",
                        "A7 5F 4E 10 D1 21 25 01 7B 24 99 FF ED 93 6F 2E",
                        "EB C1 12 C3 93 E7 38 39 23 56 BD D0 12 02 9B A7"));

        System.out.print("\tTEST 3... ");
        test(rabbit,
                convertData("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),
                convertIVData("A6 EB 56 1A D2 F4 17 27"),
                convertData(
                        "44 5A D8 C8 05 85 8D BF 70 B6 AF 23 A1 51 10 4D",
                        "96 C8 F2 79 47 F4 2C 5B AE AE 67 C6 AC C3 5B 03",
                        "9F CB FC 89 5F A7 1C 17 31 3D F0 34 F0 15 51 CB"));
    }
}