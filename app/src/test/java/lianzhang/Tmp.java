package lianzhang;

import java.util.Arrays;

public class Tmp {
    public static void main(String[] args) {
        String[] list = new String[] {"111", "2222", "3333"};
        Arrays.stream(list).filter(s -> s.length() > 1).map(String::hashCode);
    }
}
