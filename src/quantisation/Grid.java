/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package quantisation;

import java.util.ArrayList;
import java.util.Collections;
import model.Pair;

/**
 *
 * @author jannes
 */
public class Grid {
    private ArrayList<Long> gridPoints;
    private DistanceFunction distanceFunction;

    public Grid(long gridPoint){
        this(gridPoint,new DistanceFunction());
    }

    public Grid(long gridPoint, DistanceFunction distanceFunction){
        gridPoints = new ArrayList<Long>();
        gridPoints.add(0l);
        gridPoints.add(gridPoint);
        this.distanceFunction = distanceFunction;
    }   

    public void setDistanceFunction(DistanceFunction distanceFunction){
        this.distanceFunction = distanceFunction;
    }

    public void addGridPoint(long point){
        gridPoints.add(point);
        Collections.sort(gridPoints);
    }

    public long getLength(){
        return gridPoints.get(gridPoints.size()-1);
    }

    public ArrayList<Pair<Long,Long>> fitToGrid(ArrayList<Long> originalData,long gridOffset){
        long length = getLength();
        if(gridOffset > 0){
            gridOffset = gridOffset%length-length;
        }
        ArrayList<Pair<Long,Long>> result = new ArrayList<Pair<Long,Long>>();
        for(int i=0; i < originalData.size(); i++){
            long data = (originalData.get(i) - gridOffset)%length;            
            long closest = 0;
            long distance = distanceFunction.getDistance(closest, data);
            for(int j = 1; j < gridPoints.size(); j++){
                long newDistance = distanceFunction.getDistance(gridPoints.get(j), data);
                if(newDistance < distance){
                    closest = gridPoints.get(j);
                    distance = newDistance;
                }
            }           
            result.add(new Pair<Long,Long>(closest - data +originalData.get(i),distance));
        }
        return result;
    }

    public ArrayList<Pair<Long,Long>> getGridValues(ArrayList<Long> originalData,long gridOffset){
        long length = getLength();
        if(gridOffset > 0){
            gridOffset = gridOffset%length-length;
        }
        long indexOffset = 0;
        for(int i = 0; i < gridPoints.size(); i++){            
            if(gridPoints.get(i) + gridOffset < 0){
                indexOffset++;
            }
        }         
        ArrayList<Pair<Long,Long>> result = fitToGrid(originalData, gridOffset);

        for(int i=0; i < result.size(); i++){
            Pair<Long,Long> value = result.get(i);            
            long numberInGrid = gridPoints.indexOf((value.first-gridOffset)%length);
            long index = (value.first-gridOffset)%length;
            if (gridOffset != 0 && index == 0 && value.first-gridOffset != 0){
                index = length;
            }            
            value.first = (value.first/getLength())*(gridPoints.size()-1)       //all previous gridpoints
                    +gridPoints.indexOf(index)                                  //number in grid
                    -indexOffset;                                               //number of gridpoints before zero mark            
        }
        return result;
    }
}
