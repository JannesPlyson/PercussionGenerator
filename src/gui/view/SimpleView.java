/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.view;

import model.Track;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import model.Note;

/**
 *
 * @author installer
 */
public class SimpleView extends View implements KeyListener{
    private int division;
    private Color backgroundColor;
    private Color gridColor;
    private Color foregroundColor;
    protected Timer timer;

    public SimpleView(Track track){
        super(track);
        viewOptions = new SimpleViewOptions(this);
        this.setPreferredSize(new Dimension(300,100));        
        division = 10;
        backgroundColor = Color.WHITE;
        gridColor = Color.GRAY;
        foregroundColor = Color.BLACK;
        timePointerColor = Color.RED;
        this.addKeyListener(this);        
        this.setFocusable(true);
        timer = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
    }
    
    @Override
    public void paintView(Graphics g) {         
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth(), getHeight());
        paintTrack(g);
        paintGrid(g);
        paintTimePointer(g);
    }

    private void paintGrid(Graphics g){
        g.setColor(gridColor);
        int gridWidth = getWidth()/division;
        for(int i = 1; i < division; i++){
            g.drawLine(i*gridWidth, 0, i*gridWidth, getHeight());
        }
        //TODO: text bij de grid;
    }

    private void paintTrack(Graphics g){
        g.setColor(foregroundColor);        
        long patternLength = track.getLength();
        int yOffset = getHeight()/4;
        int halfHeight = getHeight()/2;        
        Iterator<Note> it = track.getIterator();
        Note n = null;
        while(it.hasNext()){
            n = it.next();            
            int xOffset = (int)(n.getStart() * getWidth() / patternLength);
            int width = (int)(n.getDuration() * getWidth() / patternLength);
            g.fillRect(xOffset, yOffset, 2, halfHeight);
            g.fillRect(xOffset,halfHeight-1,width,2);            
        }
        if(n != null && n.getStart() + n.getDuration() > track.getLength()){
            int width = (int)((n.getStart() + n.getDuration() - track.getLength()) * getWidth() / patternLength);
            //g.fillRect(0, yOffset, width, halfHeight);
            g.fillRect(0,halfHeight-1,width,2);            
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {        
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            track.getPattern().shift(50000000);
            this.repaint();            
        }else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            track.getPattern().shift(-50000000);
            this.repaint();
        }        
    }

    public void keyReleased(KeyEvent e) {
    }

    public void updateLanguage(ResourceBundle labels) {
    }
       

    public void addElements(JPopupMenu popup,Object caller) {                
    }

}
