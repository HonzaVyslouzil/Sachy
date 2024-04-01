package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

public interface MysInterface extends MouseListener, MouseWheelListener, MouseMotionListener {
    void mysStisknuti(MouseEvent e);

    void mysUvolneni(MouseEvent e);

    void mysTahnuti(MouseEvent e);

    void mysHnuti(MouseEvent e);
}
