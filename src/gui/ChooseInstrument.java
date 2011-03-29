/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import model.Track;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.midi.Instrument;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author jannes
 */
public class ChooseInstrument extends JDialog{
    public ChooseInstrument(final Track track, Instrument[] instruments){
        DefaultListModel dlm = new DefaultListModel();
        final JList list = new JList(dlm);
        for(int i = 0; i < instruments.length; i++){
            dlm.addElement(instruments[i]);
        }
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    track.setInstrument((Instrument)list.getSelectedValue());
                    dispose();
                }                
            }
        });
        list.setSelectedValue(track.getInstrument(), true);
        this.setModal(true);
        JPanel panel = new JPanel(new BorderLayout());
        this.setContentPane(panel);
        this.setMinimumSize(new Dimension(300,300));
        panel.add(new JScrollPane(list),BorderLayout.CENTER);        
    }
}
