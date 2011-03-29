/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author jannes
 */
public abstract class ViewOptions extends JPanel{
    public abstract Element createDomNodes(Document doc);
    protected View parent;
    
    public ViewOptions(final View parent){
        this.parent = parent;
        this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        final JCheckBox mute = new JCheckBox("mute");
        mute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.getTrack().setMute(mute.isSelected());
            }
        });
        this.add(mute);
    }
}
