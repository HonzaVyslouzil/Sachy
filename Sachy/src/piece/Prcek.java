package piece;

import main.HraPanel;
import main.Typ;

public class Prcek extends Figurka{
    public Prcek(int barva, int sloupec, int radek) {
        super(barva, sloupec, radek);

        typ = Typ.PRCEK;
        if (barva == HraPanel.BILA){
            obrazek = getObrazek("/piece/w-pawn");
        }
        else {
            obrazek = getObrazek("/piece/b-pawn");
        }
    }
    public boolean muzeSeHybat(int cilovySloupec, int cilovyRadek){
        if (jeNaDesce(cilovySloupec, cilovyRadek) && jeStejnePole(cilovySloupec,cilovyRadek) == false){
         //Definujeme pohyb dle barvy figurky
         int hodnotaPohybu;
         if (barva == HraPanel.BILA){
             hodnotaPohybu = -1;
         }
         else {
             hodnotaPohybu = 1;
         }
         //Sebrání figurky protihráče
            zajmutiF = zajmutiFigurky(cilovySloupec, cilovyRadek);

         // Pohyb o jedno pole
            if(cilovySloupec == preSloupec && cilovyRadek == preRadek + hodnotaPohybu && zajmutiF == null){
                return true;
            }
            //Pohyb o dvě pole
            if (cilovySloupec == preSloupec && cilovyRadek == preRadek + hodnotaPohybu*2 && zajmutiF == null && pohnulaSe == false &&
                    figurkaVCeste(cilovySloupec, cilovyRadek) == false){
                return true;
            }
            //Diagonální pohyb a zajmutí figurky
            if (Math.abs(cilovySloupec - preRadek) == 1 && cilovyRadek == preRadek + hodnotaPohybu && zajmutiF != null &&
                    zajmutiF.barva != barva){
                return true;
            }

            //En Passant
            if (Math.abs(cilovySloupec - preRadek) == 1 && cilovyRadek == preRadek + hodnotaPohybu){
                for (Figurka figurka : HraPanel.simFigurky){
                    if (figurka.sloupec == cilovySloupec && figurka.radek == preRadek && figurka.udelalaDvaKroky == true){
                        zajmutiF = figurka;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
