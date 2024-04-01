package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mys extends MouseAdapter implements MysInterface {

    public int x, y;
    public boolean stisk;


    @Override
    public void mysStisknuti(MouseEvent e){
        stisk = true;
    }
    @Override
    public void mysUvolneni(MouseEvent e){
        stisk = false;
    }
    @Override
    public void mysTahnuti(MouseEvent e){
        x = e.getX();
        y = e.getY();
    }
    @Override
    public void mysHnuti(MouseEvent e){
        x = e.getX();
        y = e.getY();
    }
}
