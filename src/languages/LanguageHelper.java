/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package languages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author jannes
 */
public class LanguageHelper {
    public static void updateLanguage(ResourceBundle resourceBundle, HashMap<String,JComponent> languageComponents){
        Iterator<String> it = languageComponents.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            JComponent component = languageComponents.get(key);
            if(component instanceof AbstractButton){
                ((AbstractButton)component).setText(resourceBundle.getString(key));
            }else if(component instanceof JLabel){
                ((JLabel)component).setText(resourceBundle.getString(key));
            }
        }
    }

    public static ResourceBundle getResourceBundle(){
        return ResourceBundle.getBundle("languages.labels");
    }
}
