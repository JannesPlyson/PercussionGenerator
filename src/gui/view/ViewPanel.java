/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;

/**
 *
 * @author installer
 */
public class ViewPanel extends JPanel{

    public ViewPanel(View view){
        this.setLayout(new BorderLayout());
        setView(view);
    }

    public void setView(View view){
        this.add(view,BorderLayout.CENTER);
        this.add(view.viewOptions,BorderLayout.EAST);
    }

    public View getView(){
        Component[] list = this.getComponents();
        int i = 0;
        while(i < list.length){
            if(list[i] instanceof View){
                return (View)list[i];
            }
            i++;
        }
        return null;
    }

    public ViewOptions getViewOption(){
        Component[] list = this.getComponents();
        int i = 0;
        while(i < list.length){
            if(list[i] instanceof ViewOptions){
                return (ViewOptions)list[i];
            }
            i++;
        }
        return null;
    }

}
