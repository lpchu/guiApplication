package com.company;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

public abstract class CommonMarker extends SimplePointMarker {
    protected int nKilled;
    protected int nInjured;

    // constructors
    public CommonMarker() {
        super();
    }

    public CommonMarker(Location location) {
        super(location);
    }

    public CommonMarker(Location location, java.util.HashMap<java.lang.String,java.lang.Object> properties) {
        super(location, properties);
    }

    // common draw method to share with derived classes
    public void draw(PGraphics pg, float x, float y) {
        if (!hidden) {
            drawMarker(pg, x, y);
            if (selected) {
                printTitle();
            }
        }
    }

    /** getters for feature properties */
    public int getKilled() {
        return this.nKilled;
    }

    public int getInjured() {
        return this.nInjured;
    }

    // to be specified by derived classes
    public abstract String getTitle();
    public abstract String printTitle();
    public abstract void drawMarker(PGraphics pg, float x, float y);
}
