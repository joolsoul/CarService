package ru.kudinov;

public class Main {

    public static void main(String[] args) {


        solve(7);


    }

    public static void solve(int h) {
        if (h < 1) {
            System.out.println("invalid value");
        } else {
            int counter = h - 3;
            for (int i = 0; i < h; i++) {
                if (i == 0) {
                    System.out.print('*');
                    int lineCounter = h - 3;
                    for (int j = 0; j < lineCounter; j++) {
                        System.out.print('-');
                    }
                    System.out.println('*');
                } else if (i == h - 1) {
                    System.out.print('*');
                } else {
                    System.out.print('\\');
                    for (int j = 0; j < counter; j++) {
                        System.out.print('*');
                    }
                    counter--;
                    System.out.println('|');
                }

            }
        }
    }
}
