/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.view;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author jannes
 */
public class SimpleViewOptions extends ViewOptions{
    public int grid;

    public SimpleViewOptions(View parent){
        super(parent);
        grid = 10;
    }

    public Element createDomNodes(Document doc) {
        Element elSimpleView = doc.createElement("simpleView");
        Element elGrid = doc.createElement("grid");
        elSimpleView.appendChild(elGrid);
        elGrid.appendChild(doc.createTextNode("" + grid));
        return elSimpleView;
    }
}
