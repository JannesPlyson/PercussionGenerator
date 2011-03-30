/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package quantisation;

import java.util.ArrayList;
import java.util.Random;
import model.Pair;

/**
 *
 * @author jannes
 */
public class testQuantisation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Grid grid = new Grid(100);
        ArrayList<Long> originalData = new ArrayList<Long>();
        Random r = new Random();

        for(int i = 0; i < 19; i++){
            originalData.add(Math.abs(r.nextLong()% 1000));
        }        
        originalData.add(0l);
        ArrayList<Pair<Long,Long>> result = grid.getGridValues(originalData,55);
        for(int i = 0; i < originalData.size(); i++){
            System.out.println("" + originalData.get(i) + "->" + result.get(i).first + "--" + result.get(i).second);
        }
    }

}
