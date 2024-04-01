package main;

import java.awt.*;

public class Deska {

    //Vykreslení sloupcu a radku na hrací desce, nastavení velikosti jednoho políčka
    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;

    // Vykreslení hrací desky
    public void nakresli(Graphics2D g2){
        int barva = 0;
        for (int radek = 0; radek < MAX_ROW; radek++){
            for (int sloupec = 0; radek< MAX_COL; sloupec++){
                //2 barvy polí
                if (barva == 0){
                    g2.setColor(new Color(17, 125, 125));
                    barva = 1;
                }
                else {
                    g2.setColor(new Color(255, 255, 255));
                    barva = 0;
                }
                g2.fillRect(sloupec*SQUARE_SIZE, radek*SQUARE_SIZE,SQUARE_SIZE,SQUARE_SIZE);
            }
            if (barva == 0) {
                barva = 1;
            }
            else {
                barva = 0;
            }
        }
    }
}
