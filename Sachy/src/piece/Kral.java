package piece;

import main.HraPanel;
import main.Mys;
import main.Typ;

public class Kral extends Figurka{
    public Kral(int barva, int sloupec, int radek) {
        super(barva, sloupec, radek);

        typ = Typ.KRAL;

        if (barva == HraPanel.BILA){
            obrazek = getObrazek("/piece/w-king");
        }
        else {
            obrazek = getObrazek("/piece/b-king");
        }
    }
    public boolean muzeSeHybat(int cilovySloupec, int cilovyRadek){
        if (jeNaDesce(cilovySloupec,cilovyRadek)){
            if (Math.abs(cilovySloupec-preSloupec) + Math.abs(cilovyRadek-preRadek) ==1 ||
                Math.abs(cilovySloupec-preSloupec) * Math.abs(cilovyRadek-preRadek) ==1 ){
                if (spravnePolicko(cilovySloupec, cilovyRadek)){
                    return true;
                }
            }
            //Rošáda
            if (pohnulaSe == false){
                //Malá rošáda
                if(cilovySloupec == preSloupec + 2 && cilovyRadek == preRadek && figurkaVCeste(cilovySloupec, cilovyRadek) == false){
                    for (Figurka figurka : HraPanel.simFigurky){
                        if (figurka.sloupec == preSloupec + 3 && figurka.radek == preRadek && figurka.pohnulaSe == false){
                            HraPanel.rosadaF = figurka;
                            return true;
                        }
                    }
                }
                //Velká rošáda
                if (cilovySloupec == preSloupec - 2 && cilovyRadek == preRadek && figurkaVCeste(cilovySloupec,cilovyRadek) == false ){
                    Figurka f [] = new Figurka[2];
                    for (Figurka figurka : HraPanel.simFigurky){
                        if (figurka.sloupec == preSloupec - 3 && figurka.radek == cilovyRadek){
                            f[0] = figurka;
                        }
                        if (figurka.sloupec == preSloupec - 4 && figurka.radek == cilovyRadek){
                            f[1] = figurka;
                        }

                        System.out.println(f[1]);

                        if (f[0] == null && f[1] != null && f[1].pohnulaSe == false){
                            HraPanel.rosadaF = f[1];
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
