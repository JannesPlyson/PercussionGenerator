/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package quantisation;

/**
 *
 * @author jannes
 */
public class DistanceFunction {
    public long getDistance(long value1,long value2){
        return Math.abs(value1-value2);
    }
}
