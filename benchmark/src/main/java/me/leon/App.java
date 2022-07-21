package me.leon;

/**
 * ArrayList和ListedList集合性能测试对比
 *
 * @author liuchao
 */
public class App {

    public static final int SIZE = 100000;
    public static final int SIZE_SMALL = 1000;

    public static void main(String[] args) {

        System.out.println("头插 " + SIZE);
        ArrayListTest.addFromHeaderTest(SIZE);
        LinkedListTest.addFromHeaderTest(SIZE);
        System.out.println("头插 " + SIZE_SMALL);
        ArrayListTest.addFromHeaderTest(SIZE_SMALL);
        LinkedListTest.addFromHeaderTest(SIZE_SMALL);

        System.out.println();
        System.out.println("头删 " + SIZE);
        ArrayListTest.deleteFromHeaderTest(SIZE);
        LinkedListTest.deleteFromHeaderTest(SIZE);
        System.out.println("头删 " + SIZE_SMALL);
        ArrayListTest.deleteFromHeaderTest(SIZE_SMALL);
        LinkedListTest.deleteFromHeaderTest(SIZE_SMALL);

        System.out.println();
        System.out.println("中插 " + SIZE);
        ArrayListTest.addFromMidTest(SIZE);
        LinkedListTest.addFromMidTest(SIZE);
        System.out.println("中插 " + SIZE_SMALL);
        ArrayListTest.addFromMidTest(SIZE_SMALL);
        LinkedListTest.addFromMidTest(SIZE_SMALL);

        System.out.println();
        System.out.println("中删 " + SIZE / 10);
        ArrayListTest.deleteFromMidTest(SIZE / 10);
        LinkedListTest.deleteFromMidTest(SIZE / 10);
        System.out.println("中删 " + SIZE_SMALL);
        ArrayListTest.deleteFromMidTest(SIZE_SMALL);
        LinkedListTest.deleteFromMidTest(SIZE_SMALL);

        System.out.println();
        System.out.println("尾插 " + SIZE);
        ArrayListTest.addFromTailTest(SIZE);
        LinkedListTest.addFromTailTest(SIZE);
        System.out.println("尾插 " + SIZE_SMALL);
        ArrayListTest.addFromTailTest(SIZE_SMALL);
        LinkedListTest.addFromTailTest(SIZE_SMALL);

        System.out.println();
        System.out.println("尾删 " + SIZE * 10);
        ArrayListTest.deleteFromTailTest(SIZE * 10);
        LinkedListTest.deleteFromTailTest(SIZE * 10);
        System.out.println("尾删 " + SIZE_SMALL * 10);
        ArrayListTest.deleteFromTailTest(SIZE_SMALL * 10);
        LinkedListTest.deleteFromTailTest(SIZE_SMALL * 10);

        System.out.println();
        ArrayListTest.getByForTest(SIZE);
        LinkedListTest.getByForTest(SIZE);

        System.out.println();
        ArrayListTest.getByIteratorTest(SIZE * 10);
        LinkedListTest.getByIteratorTest(SIZE * 10);
    }
}
