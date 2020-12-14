package main.java.org.smallbox.farpoint.desktop;

public class TestLauncher {
    public static void main (String[] arg) {
        long time = System.currentTimeMillis();
        String str = null;
        for (int i = 0; i < 10000; i++) {
            System.out.println(String.format("hello %s", Math.random()));
//            System.out.println("hello " + Math.random());
//            System.out.println("hello ");
        }
        System.out.println("Duration: " + (System.currentTimeMillis() - time));
    }
}
