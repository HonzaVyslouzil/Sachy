package piece;

import main.HraPanel;
import main.Typ;

public class Kun extends Figurka {
    public Kun(int barva, int sloupec, int radek) {
        super(barva, sloupec, radek);

        typ = Typ.KUN;

        if (barva == HraPanel.BILA) {
            obrazek = getObrazek("/piece/w-knight");
        } else {
            obrazek = getObrazek("/piece/b-knight");
        }
    }

    public boolean muzeSeHybat(int cilovySloupec, int cilovyRadek) {
        if (jeNaDesce(cilovySloupec, cilovyRadek)) {
            if (Math.abs(cilovySloupec - preSloupec) * Math.abs(cilovyRadek - preRadek) == 2){
                if (spravnePolicko(cilovySloupec, cilovyRadek)){
                    return true;
                }
            }
        }
        return false;
    }
}
