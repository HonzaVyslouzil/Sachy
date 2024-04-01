package piece;

import main.HraPanel;
import main.Typ;

public class Strelec extends Figurka{
    public Strelec(int barva, int sloupec, int radek) {
        super(barva, sloupec, radek);

        typ = Typ.STRELEC;

        if (barva == HraPanel.BILA){
            obrazek = getObrazek("/piece/w-bishop");
        }
        else {
            obrazek = getObrazek("/piece/b-bishop");
        }
    }
    public boolean muzeSeHybat(int cilovySloupec, int cilovyRadek){
        if (jeNaDesce(cilovySloupec,cilovyRadek) && jeStejnePole(cilovySloupec, cilovyRadek) == false){
                if (Math.abs(cilovySloupec - preSloupec) == Math.abs(cilovyRadek - preRadek)){
                    if (jeStejnePole(cilovySloupec, cilovyRadek) && figurkaVCesteDiagonalne(cilovySloupec, cilovyRadek) == false){
                        return true;
                    }
                }
        }
        return false;
    }
}
