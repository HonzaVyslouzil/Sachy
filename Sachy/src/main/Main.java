package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        //Vytvoření okna hry

        JFrame okno = new JFrame("Šachy");
        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okno.setResizable(false);

        //Přidání HraPanel do okna
        HraPanel hp = new HraPanel();
        okno.add(hp);
        okno.pack();
        okno.setLocationRelativeTo(null);
        okno.setVisible(true);
        hp.spustHru();
    }
}
