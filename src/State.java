/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.geom.Point2D;

/**
 *
 * @author wkren
 */
public class State {

    /**
     * @return the geometricData
     */
    public Geometry getGeometricData() {
        return geometricData;
    }

    /**
     * @param geometricData the geometriData to set
     */
    public void setGeometricData(Geometry geometricData) {
        this.geometricData = geometricData;
    }
    
    /**
     * @return the location
     */
    public Point2D getLocation() {
        return this.geometricData.getLocation();
    }
    
    /**
     * @param location the location to set
     */
    public void setLocation(int x, int y) {
        this.geometricData.setLocation(x, y);
    }
    
    /**
     * @param location the location to set
     */
    public void setLocation(Point2D p) {
        this.geometricData.setLocation(p);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the initialFlag
     */
    public boolean isInitial() {
        return initialFlag;
    }

    /**
     * @param initialFlag the initialFlag to set
     */
    public void setInitial(boolean initialFlag) {
        this.initialFlag = initialFlag;
    }

    /**
     * @return the finalFlag
     */
    public boolean isFinal() {
        return finalFlag;
    }

    /**
     * @param finalFlag the finalFlag to set
     */
    public void setFinal(boolean finalFlag) {
        this.finalFlag = finalFlag;
    }
    
    private Geometry geometricData;
    Drawing drawingData;
    
    private int id;
    private String name;
    private boolean initialFlag;
    private boolean finalFlag;
    
    public State() {
        id = -1;
        drawingData = new Drawing();
        initialFlag = false;
        finalFlag = false;
        name = new String();
    }
}
