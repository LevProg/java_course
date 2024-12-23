public class task6 {
    public static void main(String[] args) {
        int i1 = 128;
        Integer a1 = i1;
        Integer b1 = i1;

        System.out.println("a1==i1 " + (a1 == i1)); // true
        System.out.println("b1==i1 " + (b1 == i1)); // true
        System.out.println("a1==b1 " + (a1 == b1)); // false
        System.out.println("a1.equals(i1) -> " + a1.equals(i1)); // true
        System.out.println("b1.equals(i1) -> " + b1.equals(i1)); // true
        System.out.println("a1.equals(b1) -> " + a1.equals(b1)); // true

        int i2 = 127;
        Integer a2 = i2;
        Integer b2 = i2;

        System.out.println("a2==i2 " + (a2 == i2)); // true
        System.out.println("b2==i2 " + (b2 == i2)); // true
        System.out.println("a2==b2 " + (a2 == b2)); // true
        System.out.println("a2.equals(i2) -> " + a2.equals(i2)); // true
        System.out.println("b2.equals(i2) -> " + b2.equals(i2)); // true
        System.out.println("a2.equals(b2) -> " + a2.equals(b2)); // true
    }
}
