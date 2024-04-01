package piece;

import main.HraPanel;
import main.Typ;

public class Kralovna extends Figurka{
    public Kralovna(int barva, int sloupec, int radek) {
        super(barva, sloupec, radek);

        typ = Typ.KRALOVNA;

        if (barva == HraPanel.BILA){
            obrazek = getObrazek("/piece/w-queen");
        }
        else {
            obrazek = getObrazek("/piece/b-queen");
        }
    }
    public boolean muzeSeHybat(int cilovySloupec, int cilovyRadek){
        if (jeNaDesce(cilovySloupec, cilovyRadek) && jeStejnePole(cilovySloupec, cilovyRadek) == false){
           //Vertikální a horizontáůlní pohyb
           if (cilovySloupec == preSloupec || cilovyRadek == preRadek){
               if (spravnePolicko(cilovySloupec,cilovyRadek) && figurkaVCeste(cilovySloupec, cilovyRadek) == false){
                   return true;
               }
           }
           //Diagonální pohyb
            if (Math.abs(cilovySloupec - preSloupec) == Math.abs(cilovyRadek - preRadek)){
                if (spravnePolicko(cilovySloupec, cilovyRadek) && figurkaVCesteDiagonalne(cilovySloupec, cilovyRadek) == false){
                    return true;
                }
            }
        }
        return false;
    }
}
