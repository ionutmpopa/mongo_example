package my.city;

public class School {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        String[] stringArray = {"", ""};

        something("some", stringArray);


    }

    private static int something(String var, String... vargars) {
        return vargars.length;
    }

    private static int something(String var, float myFloat) {
        return (int) myFloat;
    }
}
