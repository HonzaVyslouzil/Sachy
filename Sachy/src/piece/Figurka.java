package piece;

import main.Deska;
import main.HraPanel;
import main.Typ;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Figurka {
    public Typ typ;
    public BufferedImage obrazek;
    public int x,y;
    public int sloupec, radek, preSloupec, preRadek;
    public int barva;
    public Figurka zajmutiF;
    public boolean pohnulaSe, udelalaDvaKroky;

    public Figurka(int barva, int sloupec, int radek){
        this.barva = barva;
        this.sloupec = sloupec;
        this.radek = radek;
        x = getX(sloupec);
        y = getY(radek);
        preSloupec = sloupec;
        preRadek = radek;
    }
    public BufferedImage getObrazek(String obrazekCesta){
        BufferedImage obrazek = null;
        try {
            obrazek = ImageIO.read(getClass().getResourceAsStream(obrazekCesta + ".png"));
        }catch (IOException e){
            e.printStackTrace();
        }
        return obrazek;
    }
    public int getX(int sloupec){
        return sloupec* Deska.SQUARE_SIZE;
    }
    public int getY(int radek){
        return radek* Deska.SQUARE_SIZE;
    }
    public int getSloupec(int x){
        return (x + Deska.HALF_SQUARE_SIZE)/Deska.SQUARE_SIZE;
    }
    public int getRadek(int y){
        return (y + Deska.HALF_SQUARE_SIZE)/Deska.SQUARE_SIZE;
    }
    public int getIndex(){
        for (int index = 0; index < HraPanel.simFigurky.size(); index++){
            if (HraPanel.simFigurky.get(index) == this){
                return index;
            }
        }
        return 0;
    }
    public void updatePozice(){
        //Kontrola En Passant
        if (typ == Typ.PRCEK){
            if (Math.abs(radek - preRadek) == 2){
                udelalaDvaKroky = true;
            }
        }

        x = getX(sloupec);
        y = getY(radek);
        preSloupec = getSloupec(x);
        preRadek = getRadek(y);
        pohnulaSe = true;
    }
    public void resetPozice(){
        sloupec = preSloupec;
        radek = preRadek;
        x = getX(sloupec);
        y = getY(radek);
    }
    public boolean muzeSeHybat(int cilovySloupec, int cilovyRadek){
        return false;
    }
    public boolean jeNaDesce(int cilovySloupec, int cilovyRadek){
        if (cilovySloupec >= 0 && cilovySloupec <= 7 && cilovyRadek >= 0 && cilovyRadek <= 7){
            return true;
        }
        return false;
    }
    public boolean jeStejnePole(int cilovySloupec, int cilovyRadek){
        if(cilovySloupec == preSloupec && cilovyRadek == preRadek){
            return true;
        }
        return false;
    }
    public Figurka zajmutiFigurky(int cilovySloupec, int cilovyRadek){
        for (Figurka figurka : HraPanel.simFigurky){
            if (figurka.sloupec == cilovySloupec && figurka.radek == cilovyRadek && figurka != this){
                return figurka;
            }
        }
        return null;
    }
    public boolean spravnePolicko(int cilovySloupec, int cilovyRadek){
        zajmutiF = zajmutiFigurky(cilovySloupec, cilovyRadek);
        //toto pole je volné a mohu tam tahnout
        if (zajmutiF == null) {
            return true;
        }
        //Toto pole je obsazeno ale mohu zajmout figurku protihráče
        else {
            //Pokud je barva figurky v poli jiná než hráče který je na tahu může ji sebrat
            if (zajmutiF.barva != this.barva){
                return true;
            }
            else {
                zajmutiF = null;
            }
        }
        return false;
    }

    //Zastaví figurku(věž) pokud v cestě je figurka protihráče a zajme ji
    public boolean figurkaVCeste(int cilovySloupec, int cilovyRadek){
        //Když je figurka v cestě směrem do leva
        for (int c = preSloupec-1; c > cilovySloupec; c--){
            for (Figurka figurka : HraPanel.simFigurky){
                if (figurka.sloupec == c && figurka.radek == cilovyRadek){
                    zajmutiF = figurka;
                    return true;
                }
            }
        }
        //Když je figurka v cestě směrem do prava
        for (int c = preSloupec+1; c < cilovySloupec; c++){
            for (Figurka figurka : HraPanel.simFigurky){
                if (figurka.sloupec == c && figurka.radek == cilovyRadek){
                    zajmutiF = figurka;
                    return true;
                }
            }
        }
        //Když je figurka v cestě směrem nahoru
        for (int r = preRadek-1; r > cilovyRadek; r--){
            for (Figurka figurka : HraPanel.simFigurky){
                if (figurka.sloupec == cilovySloupec && figurka.radek == r){
                    zajmutiF = figurka;
                    return true;
                }
            }
        }
        //Když je figurka v cestě směrem dolu
        for (int r = preRadek+1; r < cilovyRadek; r++){
            for (Figurka figurka : HraPanel.simFigurky){
                if (figurka.sloupec == cilovySloupec && figurka.radek == r){
                    zajmutiF = figurka;
                    return true;
                }
            }
        }
        return false;
    }
    public boolean figurkaVCesteDiagonalne(int cilovySloupec, int cilovyRadek){
        if (cilovyRadek < preRadek){
            //Nahoře vlevo
            for (int c = preSloupec -1; c > cilovySloupec; c--){
                int rozdil = Math.abs(c - preSloupec);
                for (Figurka figurka : HraPanel.simFigurky){
                    if (figurka.sloupec == c && figurka.radek == preRadek - rozdil){
                        zajmutiF = figurka;
                        return true;
                    }
                }
            }
            //Nahoře vpravo
            for (int c = preSloupec +1; c < cilovySloupec; c++){
                int rozdil = Math.abs(c - preSloupec);
                for (Figurka figurka : HraPanel.simFigurky){
                    if (figurka.sloupec == c && figurka.radek == preRadek - rozdil){
                        zajmutiF = figurka;
                        return true;
                    }
                }
            }
        }
        if (cilovyRadek > preRadek) {
            //Dole vlevo
            for (int c = preSloupec - 1; c > cilovySloupec; c--) {
                int rozdil = Math.abs(c - preSloupec);
                for (Figurka figurka : HraPanel.simFigurky) {
                    if (figurka.sloupec == c && figurka.radek == preRadek + rozdil) {
                        zajmutiF = figurka;
                        return true;
                    }
                }
            }

            //Dole vpravo
            for (int c = preSloupec + 1; c < cilovySloupec; c++) {
                int rozdil = Math.abs(c - preSloupec);
                for (Figurka figurka : HraPanel.simFigurky) {
                    if (figurka.sloupec == c && figurka.radek == preRadek + rozdil) {
                        zajmutiF = figurka;
                        return true;
                    }
                }
            }
        }

        return false;
    }
    public void nakresli(Graphics2D g2){
        g2.drawImage(obrazek,x,y,Deska.SQUARE_SIZE,Deska.SQUARE_SIZE,null);
    }
}
