package com.toppecraft.toppecheat.violations;

/**
 * @author Toppe5
 * @since 2.0
 */
public class Violation {

    private int VL;
    private long lastUpdate;
    private long first;


    public Violation(int vl) {
        this.first = System.currentTimeMillis();
        this.lastUpdate = System.currentTimeMillis();
        this.VL = vl;
    }

    /**
     * Gets the violation levl.
     *
     * @return the violation level
     */
    public int getVL() {
        return VL;
    }

    /**
     * Sets the violation level.
     *
     * @param VL the violation to set
     */
    public void setVL(int VL) {
        lastUpdate = System.currentTimeMillis();
        this.VL = VL;
    }

    /**
     * Gets the last time this was updated.
     *
     * @return the last time in millis when this Violation was updated
     */
    public long getLastUpdate() {
        return lastUpdate;
    }


    /**
     * Gets the time this was created.
     *
     * @return the create time of this Violation
     */
    public long getFirst() {
        return first;
    }


}
