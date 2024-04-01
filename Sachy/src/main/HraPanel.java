package main;

import piece.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class HraPanel extends JPanel implements Runnable {
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread hraThread;
    Deska deska = new Deska();
    Mys mys = new Mys();

    //Figurky

    public static ArrayList<Figurka> figurky = new ArrayList<>();
    public static ArrayList<Figurka> simFigurky = new ArrayList<>();
    ArrayList<Figurka> promoceFigurky = new ArrayList<>();
    Figurka aktivniF, sachF;
    public static Figurka rosadaF;

    // Barva figurek
    public static final int BILA = 0;
    public static final int CERNA = 1;
    int aktualniBarva = BILA;

    //Booleany
    boolean muzeSeHybat;
    boolean spravnepolicko;
    boolean promoce;
    boolean konecHry;
    boolean pat;
    public HraPanel(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mys);
        addMouseListener(mys);

        setFigurky();
        kopirujFigurky(figurky, simFigurky);
    }
    public void spustHru(){
        hraThread = new Thread(this);
        hraThread.start();
    }

    //Vložení figurek na sachovnici
    public void setFigurky(){

        //Bílé figurky

        figurky.add(new Prcek(BILA,0,6));
        figurky.add(new Prcek(BILA,1,6));
        figurky.add(new Prcek(BILA,2,6));
        figurky.add(new Prcek(BILA,3,6));
        figurky.add(new Prcek(BILA,4,6));
        figurky.add(new Prcek(BILA,5,6));
        figurky.add(new Prcek(BILA,6,6));
        figurky.add(new Prcek(BILA,7,6));
        figurky.add(new Vez(BILA, 0,7));
        figurky.add(new Vez(BILA, 7,7));
        figurky.add(new Kun(BILA, 1,7));
        figurky.add(new Kun(BILA, 6,7));
        figurky.add(new Strelec(BILA, 2, 7));
        figurky.add(new Strelec(BILA, 5, 7));
        figurky.add(new Kralovna(BILA, 3,7));
        figurky.add(new Kral(BILA,4,7));

        //Černé figurky

        figurky.add(new Prcek(CERNA,0,1));
        figurky.add(new Prcek(CERNA,1,1));
        figurky.add(new Prcek(CERNA,2,1));
        figurky.add(new Prcek(CERNA,3,1));
        figurky.add(new Prcek(CERNA,4,1));
        figurky.add(new Prcek(CERNA,5,1));
        figurky.add(new Prcek(CERNA,6,1));
        figurky.add(new Prcek(CERNA,7,1));
        figurky.add(new Vez(CERNA, 0,0));
        figurky.add(new Vez(CERNA, 7,0));
        figurky.add(new Kun(CERNA, 1,0));
        figurky.add(new Kun(CERNA, 6,0));
        figurky.add(new Strelec(CERNA, 2, 0));
        figurky.add(new Strelec(CERNA, 5, 0));
        figurky.add(new Kralovna(CERNA, 3,0));
        figurky.add(new Kral(CERNA,4,0));

    }
    private void kopirujFigurky(ArrayList<Figurka> zdroj, ArrayList<Figurka> cil){
        cil.clear();
        for (int i = 0; i < zdroj.size(); i++){
            cil.add(zdroj.get(i));
        }
    }
    @Override
    public void run() {
        //Hrací smyčka
        double spustInterval = 1000000000/FPS;
        double delta = 0;
        long posledni = System.nanoTime();
        long aktualni;

        //tato smyčka volá update() a paintComponent() metodu 60x za vteřinu
        while (hraThread != null){
            aktualni = System.nanoTime();

            delta += (aktualni - posledni)/spustInterval;
            posledni = aktualni;

            if (delta >= 1){
                update();
                repaint();
                delta--;
            }
        }

    }
    private void update(){
        if (promoce){
            promovani();
        }
        else if (konecHry == false && pat == false){
            //Zjistí zda je myš stisknutá

            if (mys.stisk){
                if (aktivniF == null){

                    //Pokud je aktivniF null, zkontroluje zda mužu hrát figurkou
                    for (Figurka figurka : simFigurky){
                        //Pokud je myš na figurce stejné barvy, vybere figurku jako aktivniF
                        if (figurka.barva == aktualniBarva &&
                                figurka.sloupec == mys.x/Deska.SQUARE_SIZE &&
                                figurka.radek == mys.y/Deska.SQUARE_SIZE) {
                            aktivniF = figurka;
                        }
                    }
                }
                else {
                    //Pokud hráč drží figurku, ale ještě neprovedl tah simuluje tah
                    simuluj();
                }
            }
            //Uvolnění myši a puštění figurky
            if (mys.stisk == false){
                if (aktivniF != null){
                    if (spravnepolicko){

                        //posun figurky potvrzen

                        //Update seznamu figurek v případě že byla figurka zajmutá a odstraněna během simulace
                        kopirujFigurky(simFigurky,figurky);
                        aktivniF.updatePozice();
                        if (rosadaF != null){
                            rosadaF.updatePozice();
                        }
                        if (kralMaSach() && jeSachMat()){
                            konecHry = true;
                        } else if (jePat() && kralMaSach() == false) {
                            pat = true;

                        } else { // Hra stále běží
                            if(muzePromovat()){
                                promoce = true;
                            }
                            else {
                                zmenaHrace();
                            }
                        }
                    }
                    else {
                        //Krok hrače je neplatný takže se resetuje zajmutí
                        kopirujFigurky(figurky, simFigurky);
                        aktivniF.resetPozice();
                        aktivniF = null;
                    }
                }
            }
        }
    }
    private void simuluj(){
        muzeSeHybat = false;
        spravnepolicko = false;

        //Reset seznamu figurek v každé smyčce
        //Pro "znovuoživení" odstraněných figurek dehem simulace
        kopirujFigurky(figurky, simFigurky);

        //Reset pozice figurky provadějící rošádu
        if (rosadaF != null){
            rosadaF.sloupec = rosadaF.preSloupec;
            rosadaF.x = rosadaF.getX(rosadaF.sloupec);
            rosadaF = null;
        }

        //pokud figurka bude hrát tah, updatni její pozici
        aktivniF.x = mys.x - Deska.HALF_SQUARE_SIZE;
        aktivniF.y = mys.y - Deska.HALF_SQUARE_SIZE;
        aktivniF.sloupec = aktivniF.getSloupec(aktivniF.x);
        aktivniF.radek = aktivniF.getRadek(aktivniF.y);

        //Kontrola zda je figurka nad políčkem kam může
        if (aktivniF.muzeSeHybat(aktivniF.sloupec, aktivniF.radek)){
            muzeSeHybat = true;

            //Odstranění zajmuté figurky
            if (aktivniF.zajmutiF != null){
                simFigurky.remove(aktivniF.zajmutiF.getIndex());
            }
            zkontrolujRosadu();

            if (jeNemozne(aktivniF) == false && protihracMuzeVzitKrale() == false){
                spravnepolicko = true;
            }
        }
    }
    private boolean jeNemozne(Figurka kral){

        if (kral.typ == Typ.KRAL){
            for (Figurka figurka : simFigurky){
                if (figurka != kral && figurka.barva != kral.barva && figurka.muzeSeHybat(kral.sloupec, kral.radek)){
                    return true;
                }
            }
        }

        return false;
    }
    private boolean protihracMuzeVzitKrale(){
        Figurka kral = vzitKrale(false);

        for (Figurka figurka : simFigurky){
            if (figurka.barva != kral.barva && figurka.muzeSeHybat(kral.sloupec, kral.radek)){
                return true;
            }
        }
        return false;
    }
    private boolean kralMaSach(){
        Figurka kral = vzitKrale(true);

        if (aktivniF.muzeSeHybat(kral.sloupec, kral.radek)){
            sachF = aktivniF;
            return true;
        }
        else {
            sachF = null;
        }
        return false;
    }
    private Figurka vzitKrale(boolean protihrac){
        Figurka kral = null;

        for (Figurka figurka : simFigurky){
            if (protihrac){
                if (figurka.typ == Typ.KRAL && figurka.barva != aktualniBarva){
                    kral = figurka;
                }
            }
            else {
                if (figurka.typ == Typ.KRAL && figurka.barva == aktualniBarva){
                    kral = figurka;
                }
            }
        }
        return kral;
    }
    private boolean jeSachMat(){

        Figurka kral = vzitKrale(true);

        if (kralMuzeUhnout(kral)){
            return false;
        }
        else {
            //Kontrola zda lze zablokovta utok na krále jinou figurkou daného hráče

            //Kontorla pozice figurky která dáva šach a daného krále
            int colDiff = Math.abs(sachF.sloupec - kral.sloupec);
            int rowDiff = Math.abs(sachF.radek - kral.radek);

            if(colDiff == 0) {
                //Kontrola zda utok přichází vertikálně
                if (sachF.radek < kral.radek) {
                    //Figurka která dává šach je nad králem
                    for (int radek = sachF.radek; radek < kral.radek; radek++) {
                        for (Figurka figurka : simFigurky) {
                            if (figurka != kral && figurka.barva != aktualniBarva && figurka.muzeSeHybat(sachF.sloupec, radek)) {
                                return false;
                            }
                        }
                    }
                }
                if (sachF.radek > kral.radek) {
                    //Figurka která dává šach je pod králem
                    for (int radek = sachF.radek; radek > kral.radek; radek--) {
                        for (Figurka figurka : simFigurky) {
                            if (figurka != kral && figurka.barva != aktualniBarva && figurka.muzeSeHybat(sachF.sloupec, radek)) {
                                return false;
                            }
                        }
                    }
                }
            }
            else if (rowDiff == 0) {
                //Kontrola zda utok přichází horizontálně
                if (sachF.sloupec < kral.sloupec){
                    //Figurka která dává šach je na levo od krále
                    for (int sloupec = sachF.sloupec; sloupec < kral.sloupec; sloupec++) {
                        for (Figurka figurka : simFigurky) {
                            if (figurka != kral && figurka.barva != aktualniBarva && figurka.muzeSeHybat(sachF.sloupec, sachF.sloupec)) {
                                return false;
                            }
                        }
                    }
                }
                if (sachF.sloupec > kral.sloupec){
                    //Figurka která dává šach je na pravo od krále
                    for (int sloupec = sachF.sloupec; sloupec > kral.sloupec; sloupec--) {
                        for (Figurka figurka : simFigurky) {
                            if (figurka != kral && figurka.barva != aktualniBarva && figurka.muzeSeHybat(sachF.sloupec, sachF.sloupec)) {
                                return false;
                            }
                        }
                    }
                }
            }
            else if (colDiff == rowDiff) {
                //Kontrola zda utok přichází diagonálně
                if (sachF.radek < kral.radek) {
                    //Figurka která dává šach je nad králem
                    if(sachF.sloupec < kral.sloupec){
                        //Figurka která dává šach je nad králem a do leva
                        for (int sloupec = sachF.sloupec, radek = sachF.radek; sloupec < kral.sloupec; sloupec++, radek++){
                            for (Figurka figurka : simFigurky){
                                if (figurka != kral && figurka.barva != aktualniBarva && figurka.muzeSeHybat(sloupec, radek)){
                                    return false;
                                }
                            }
                        }
                    }
                    if (sachF.sloupec > kral.sloupec){
                        //Figurka která dává šach je nad králem a do prava
                        for (int sloupec = sachF.sloupec, radek = sachF.radek; sloupec > kral.sloupec; sloupec--, radek++){
                            for (Figurka figurka : simFigurky){
                                if (figurka != kral && figurka.barva != aktualniBarva && figurka.muzeSeHybat(sloupec, radek)){
                                    return false;
                                }
                            }
                        }
                    }
                }
                if (sachF.radek > kral.radek){
                    //Figurka která dává šach je pod králem
                    if (sachF.sloupec < kral.sloupec){
                        //Figurka která dává šach je pod králem a v levo
                        for (int sloupec = sachF.sloupec, radek = sachF.radek; sloupec < kral.sloupec; sloupec++, radek--){
                            for (Figurka figurka : simFigurky){
                                if (figurka != kral && figurka.barva != aktualniBarva && figurka.muzeSeHybat(sloupec, radek)){
                                    return false;
                                }
                            }
                        }
                    }
                    if (sachF.sloupec > kral.sloupec){
                        //Figurka která dává šach je pod králem a v pravo
                        for (int sloupec = sachF.sloupec, radek = sachF.radek; sloupec > kral.sloupec; sloupec--, radek--){
                            for (Figurka figurka : simFigurky){
                                if (figurka != kral && figurka.barva != aktualniBarva && figurka.muzeSeHybat(sloupec, radek)){
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            else {
                //Kontrola zda utok přichází od koně
            }
        }

        return true;
    }
    private boolean kralMuzeUhnout(Figurka kral){
        // Tato metoda Simuluje jestli je poličko kam král může utéct
        if (jeValidniKrok(kral, -1, -1)){return true;}
        if (jeValidniKrok(kral, 0, -1)){return true;}
        if (jeValidniKrok(kral, 1, -1)){return true;}
        if (jeValidniKrok(kral, -1, 0)){return true;}
        if (jeValidniKrok(kral, 1, 0)){return true;}
        if (jeValidniKrok(kral, -1, 1)){return true;}
        if (jeValidniKrok(kral, 0, 1)){return true;}
        if (jeValidniKrok(kral, 1, 1)){return true;}

        return false;
    }
    private boolean jeValidniKrok(Figurka kral, int colPlus, int rowPlus){
        boolean jeValidniKrok = false;

        //Update královi pozice na sekundu
        kral.sloupec += colPlus;
        kral.radek += rowPlus;

        if (kral.muzeSeHybat(kral.sloupec, kral.radek)){
            if (kral.zajmutiF != null){
                simFigurky.remove(kral.zajmutiF.getIndex());
            }
            if (jeNemozne(kral) == false){
                jeValidniKrok = true;
            }
        }
        //Reset královi pozice a uchování odstraněných figurek
        kral.resetPozice();
        kopirujFigurky(figurky, simFigurky);

        return jeValidniKrok;
    }
    private boolean jePat(){

        int pocet = 0;
        //Počet figurek
        for (Figurka figurka : simFigurky){
            if (figurka.barva != aktualniBarva){
                pocet++;
            }
        }
        //Pokud zbýva pouze jedna figurka (král)
        if (pocet == 1){
            if (kralMuzeUhnout(vzitKrale(true)) == false){
                return true;
            }
        }
        return false;
    }
    private void zkontrolujRosadu(){
        if (rosadaF != null){
            //Velká rošáda
            if (rosadaF.sloupec == 0){
                rosadaF.sloupec +=3;
            }
            //Malá rošáda
            else if (rosadaF.sloupec == 7){
                rosadaF.sloupec -=2;
            }
            rosadaF.x = rosadaF.getX(rosadaF.sloupec);
        }
    }
    private void zmenaHrace(){
        if (aktualniBarva == BILA){
            aktualniBarva = CERNA;
            //Reset dvoukrokového statusu černého hráče
            for (Figurka figurka : figurky){
                if (figurka.barva == CERNA){
                    figurka.udelalaDvaKroky = false;
                }
            }
        }
        else{
            aktualniBarva = BILA;
            //Reset dvoukrokového statusu bíleho hráče
            for (Figurka figurka : figurky){
                if (figurka.barva == BILA){
                    figurka.udelalaDvaKroky = false;
                }
            }
        }
        aktivniF = null;
    }
    private boolean muzePromovat(){
        if (aktivniF.typ == Typ.PRCEK){
            if (aktualniBarva == BILA && aktivniF.radek == 0 || aktualniBarva == CERNA && aktivniF.radek == 7){
                promoceFigurky.clear();
                promoceFigurky.add(new Vez(aktualniBarva, 9,2));
                promoceFigurky.add(new Kun(aktualniBarva,9,3));
                promoceFigurky.add(new Strelec(aktualniBarva,9,4));
                promoceFigurky.add(new Kralovna(aktualniBarva,9,5));
                return true;
            }
        }
        return false;
    }
    private void promovani(){
        if (mys.stisk){
            for (Figurka figurka : promoceFigurky){
                if (figurka.sloupec == mys.x/Deska.SQUARE_SIZE && figurka.radek == mys.y/Deska.SQUARE_SIZE){
                    switch (figurka.typ){
                        case VEZ: simFigurky.add(new Vez(aktualniBarva, aktivniF.sloupec, aktivniF.radek)); break;
                        case KUN: simFigurky.add(new Kun(aktualniBarva, aktivniF.sloupec, aktivniF.radek)); break;
                        case STRELEC: simFigurky.add(new Strelec(aktualniBarva, aktivniF.sloupec, aktivniF.radek)); break;
                        case KRALOVNA: simFigurky.add(new Kralovna(aktualniBarva, aktivniF.sloupec, aktivniF.radek)); break;
                        default: break;
                    }
                    simFigurky.remove(aktivniF.getIndex());
                    kopirujFigurky(simFigurky, figurky);
                    aktivniF = null;
                    promoce = false;
                    zmenaHrace();
                }
            }
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Deska

        deska.nakresli(g2);

        //Figurky

        for (Figurka f : simFigurky){
            f.nakresli(g2);
        }
        if (aktivniF != null){
            // Vybarví hratelná pole bíle
            if (muzeSeHybat){
                if (jeNemozne(aktivniF) || protihracMuzeVzitKrale()){
                    g2.setColor(Color.gray);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F));
                    g2.fillRect(aktivniF.sloupec*Deska.SQUARE_SIZE, aktivniF.radek*Deska.SQUARE_SIZE,
                            Deska.SQUARE_SIZE, Deska.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                }
                else {

                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F));
                    g2.fillRect(aktivniF.sloupec * Deska.SQUARE_SIZE, aktivniF.radek * Deska.SQUARE_SIZE,
                            Deska.SQUARE_SIZE, Deska.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                }
            }
            //Vykreslení tahnuté figurky aby nebyla skrytá za šachovnicí nebo vybarveném čtverci
            aktivniF.nakresli(g2);
        }
        //Statusové zprávy v pravé časti vedle desky
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN,40));
        g2.setColor(Color.white);

        if (promoce){
            g2.drawString("Promuj figurku na: ", 840,150);
            for (Figurka figurka : promoceFigurky){
                g2.drawImage(figurka.obrazek, figurka.getX(figurka.sloupec), figurka.getY(figurka.radek),
                        Deska.SQUARE_SIZE,Deska.SQUARE_SIZE, null);
            }
        }
        else {
            if (aktualniBarva == BILA){
                g2.drawString("Bílá je na řadě",840,550);
                if (sachF != null && sachF.barva == CERNA){
                    g2.setColor(Color.red);
                    g2.drawString("Král", 840, 650);
                    g2.drawString("má šach!!!", 840, 700);
                }
            }
            else {
                g2.drawString("Černá je na řadě", 840, 550);
                if (sachF != null && sachF.barva == BILA){
                    g2.setColor(Color.red);
                    g2.drawString("Král", 840, 100);
                    g2.drawString("má šach!!!", 840, 150);
                }
            }
        }
        if (konecHry){
            String s = "";
            if (aktualniBarva == BILA){
                s = "Bílá vyhrává!";
            }
            else {
                s = "Černá vyhrává!";
            }
            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.green);
            g2.drawString(s, 200,420);
        }
        if (pat){
            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.lightGray);
            g2.drawString("Pat", 200,420);
        }
    }
}
