package me.leon;

import java.util.ArrayList;
import java.util.Iterator;

public class ArrayListTest {

    /**
     * @param size
     */
    public static void addFromHeaderTest(int size) {
        ArrayList<String> list = new ArrayList<>(size);

        long timeStart = System.nanoTime();
        for (int i = 0; i < size; i++) {
            list.add(0, i + "aaavvv");
        }

        long timeEnd = System.nanoTime();

        System.out.println("ArrayList头部位置新增元素花费的时间 ops/ns: " + (timeEnd - timeStart) / size);
    }

    /**
     * @param size
     */
    public static void addFromMidTest(int size) {
        ArrayList<String> list = new ArrayList<>(size);
        long timeStart = System.nanoTime();
        for (int i = 0; i < size; i++) {
            int temp = list.size();
            list.add(temp / 2, i + "aaavvv");
        }
        long timeEnd = System.nanoTime();

        System.out.println("ArrayList从集合中间位置新增元素花费的时间 ops/ns: " + (timeEnd - timeStart) / size);
    }

    /**
     * @param size
     */
    public static void addFromTailTest(int size) {
        ArrayList<String> list = new ArrayList<>(size);

        long timeStart = System.nanoTime();
        for (int i = 0; i < size; i++) {
            list.add(i + "aaavvv");
        }

        long timeEnd = System.nanoTime();

        System.out.println("ArrayList从集合尾部位置新增元素花费的时间 ops/ns: " + (timeEnd - timeStart) / size);
    }

    /**
     * @param size
     */
    public static void deleteFromHeaderTest(int size) {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            list.add(i + "aaavvv");
        }
        long timeStart = System.nanoTime();
        for (int i = 0; i < size; i++) {
            list.remove(0);
        }

        long timeEnd = System.nanoTime();

        System.out.println("ArrayList从集合头部位置删除元素花费的时间 ops/ns: " + (timeEnd - timeStart) / size);
    }

    /**
     * @param size
     */
    public static void deleteFromMidTest(int size) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i + "aaavvv");
        }
        long timeStart = System.nanoTime();
        for (int i = 0; i < size; i++) {
            int temp = list.size();
            list.remove(temp / 2);
        }

        long timeEnd = System.nanoTime();

        System.out.println("ArrayList从集合中间位置删除元素花费的时间 ops/ns: " + (timeEnd - timeStart) / size);
    }

    /**
     * @param size
     */
    public static boolean deleteFromTailTest(int size) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i + "aaavvv");
        }

        long timeStart = System.nanoTime();

        for (int i = 0; i < size; i++) {
            int temp = list.size();
            list.remove(temp - 1);
        }

        long timeEnd = System.nanoTime();

        System.out.println("ArrayList从集合尾部位置删除元素花费的时间 ops/ns: " + (timeEnd - timeStart) / size);
        return true;
    }

    /**
     * @param size
     */
    public static void getByForTest(int size) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i + "aaavvv");
        }
        long timeStart = System.nanoTime();

        for (int j = 0; j < size; j++) {
            list.get(j);
        }

        long timeEnd = System.nanoTime();

        System.out.println("ArrayList for(;;)循环花费的时间 ops/ns: " + (timeEnd - timeStart) / size);
    }

    /**
     * @param size
     */
    public static void getByIteratorTest(int size) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i + "aaavvv");
        }
        long timeStart = System.nanoTime();

        for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
            it.next();
        }

        long timeEnd = System.nanoTime();

        System.out.println("ArrayList 迭代器迭代循环花费的时间 ops/ns: " + (timeEnd - timeStart) / size);
    }
}
