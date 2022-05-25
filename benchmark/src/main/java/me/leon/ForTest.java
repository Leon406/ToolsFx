package me.leon;

import java.util.ArrayList;
import java.util.List;

public class ForTest {

    public static final int SIZE = 1000;
    public static List<String> list = new ArrayList<>(SIZE);

    static {
        for (int i = 0; i < SIZE; i++) {
            list.add("" + i);
        }
    }

    public static void forI() {
        for (int i = 0; i < list.size(); i++) {
            ForTest.action(list.get(i));
        }
    }

    public static void forI2() {
        for (int i = 0, size = list.size(); i < size; i++) {
            ForTest.action(list.get(i));
        }
    }

    public static void forIn() {
        for (String s : list) {
            ForTest.action(s);
        }
    }

    public static void forEach() {
        list.forEach(ForTest::action);
    }

    public static void streamForEach() {
        list.stream().forEach(ForTest::action);
    }

    public static void action(String obj) {}
}
