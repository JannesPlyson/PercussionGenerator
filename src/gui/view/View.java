/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.view;

import model.Track;
import gui.PopupMenuUser;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import languages.LanguageDependent;

/**
 *
 * @author installer
 */
public abstract class View extends JPanel implements LanguageDependent,MouseListener,PopupMenuUser{
    public static int SIMPLE_VIEW = 0;
    protected int zoom;
    protected Track track;
    protected ViewOptions viewOptions;        
    protected Color timePointerColor;

    public View(Track track){
        this.track = track;
        zoom =0;
        timePointerColor = Color.RED;        
        this.addMouseListener(this);
    }

    public void zoomIn(int zoom){
        this.zoom += zoom;
        repaint();
    }

    public void paintView(Graphics g){
    }

    @Override
    public final void paint(Graphics g){
        super.paint(g);        
        paintView(g);
        paintTimePointer(g);        
    }

    public void zoomOut(int zoom){
        this.zoom -= zoom;
        if(this.zoom < 0){
            zoom = 0;
        }
        repaint();
    }

    public ViewOptions getViewOptions(){
        return viewOptions;
    }

    public Track getTrack(){
        return track;
    }

    protected void paintTimePointer(Graphics g){
        g.setColor(timePointerColor);
        int xOffset = (int)(track.getTime() * getWidth() / track.getLength());
        g.drawLine(xOffset, 0, xOffset, getHeight());
    }  
        
    public final void mouseClicked(MouseEvent e) {
        this.requestFocus();
        if(e.getButton() == MouseEvent.BUTTON3){
            JPopupMenu popupMenu = new JPopupMenu();
            Container c = getParent();
            while(c != null){
                if(c instanceof PopupMenuUser){
                    ((PopupMenuUser)c).addElements(popupMenu,track);
                }
                c = c.getParent();
            }
            addElements(popupMenu,track);
            popupMenu.show(e.getComponent(),e.getX(), e.getY());
        }
        MouseClickedExtra(e);
    }

    public void MouseClickedExtra(MouseEvent e){        
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
