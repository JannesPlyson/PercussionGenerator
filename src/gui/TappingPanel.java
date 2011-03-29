/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import input.InputStartOnly;
import java.awt.BorderLayout;
import model.Track;
import model.TrackCollection;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Pattern;

/**
 *
 * @author installer
 */
public class TappingPanel extends JPanel implements FocusListener,KeyListener{
    private InputStartOnly input;
    private Color c1;
    private Color c2;
    private boolean pressed;
    private TrackCollection trackCollection;
    private JLabel label;

    public TappingPanel(TrackCollection trackCollection){
        this.setPreferredSize(new Dimension(100,100));
        this.setMinimumSize(new Dimension(100,100));
        //this.setMaximumSize(new Dimension(250,100));
        this.trackCollection = trackCollection;
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        this.addFocusListener(this);
        this.addKeyListener(this);
        this.setOpaque(true);
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createTitledBorder("Keyboard"));
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                requestFocus();
            }
        });
        c1 = Color.WHITE;
        c2 = Color.BLACK;        
        this.setBackground(c1);        
        pressed = false;
        this.setLayout(new BorderLayout());
        label = new JLabel();
        this.add(label,BorderLayout.CENTER);        
    }

    public void start(){
        input = new InputStartOnly();        
        label.setText("<html>Start tapping by pressing space or a letter. To end input press enter</html>");
    }

    public void focusGained(FocusEvent e) {
        start();
    }

    public void focusLost(FocusEvent e) {        
        label.setText("<html>Click here to start tapping using the keyboard");
    }

    public void keyTyped(KeyEvent e){        
    }

    public void keyPressed(KeyEvent e) {
        if(!e.isActionKey() && e.getKeyCode() != KeyEvent.VK_ENTER){
            if(!pressed){
                input.addInput(System.nanoTime());
                if(this.getBackground() == c1){
                    this.setBackground(c2);
                    label.setForeground(c1);
                }else{
                    this.setBackground(c1);
                    label.setForeground(c2);
                }
            }
            pressed = true;
        }else if(e.getKeyCode() == KeyEvent.VK_ENTER){
            Pattern pattern = input.getPattern();
            if(pattern != null){
                trackCollection.addTrack(new Track(pattern));
            }
            start();
            this.requestFocus();            
        }
    }

    public void keyReleased(KeyEvent e) {
        pressed = false;
    }
}
