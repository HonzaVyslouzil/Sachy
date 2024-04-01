package piece;

import main.HraPanel;
import main.Typ;

public class Vez extends Figurka{
    public Vez(int barva, int sloupec, int radek) {
        super(barva, sloupec, radek);

        typ = Typ.VEZ;

        if (barva == HraPanel.BILA){
            obrazek = getObrazek("/piece/w-rook");
        }
        else {
            obrazek = getObrazek("/piece/b-rook");
        }
    }
    public boolean muzeSeHybat(int cilovySloupec,int cilovyRadek){
        if (jeNaDesce(cilovySloupec, cilovyRadek) && jeStejnePole(cilovySloupec, cilovyRadek) == false) {
            if (cilovySloupec == preSloupec || cilovyRadek == preRadek){
                if (spravnePolicko(cilovySloupec, cilovyRadek) && figurkaVCeste(cilovySloupec, cilovyRadek) == false){
                    return true;
                }
            }
        }
        return false;
    }
}
