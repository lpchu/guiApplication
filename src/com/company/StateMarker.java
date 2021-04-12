package com.company;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.HashMap;


public class StateMarker extends CommonMarker {
    private final int nShooting;

    // constructor
    public StateMarker(PointFeature feature, HashMap<String, ArrayList<Integer>> statMap) {
        super(feature.location, feature.properties);
        String state = feature.getProperty("state").toString();
        this.nShooting = statMap.get(state).get(0);
        this.nKilled = statMap.get(state).get(1);
        this.nInjured = statMap.get(state).get(2);
    }

    /** Draw marker on the map */
    @Override
    public void drawMarker(PGraphics pg, float x, float y) {
        if (this.nShooting == 0) { // hide states with no shooting
            setHidden(true);
        } else {
            pg.pushStyle();
            pg.strokeWeight(0); // border thickness
            determineColorAndSize(pg, x, y); // color- and size-coded depending on the number of shootings in a given state

            // display the number of shootings in a given state
            pg.fill(10);
            pg.textAlign(PConstants.CENTER, PConstants.CENTER);
            pg.text(this.nShooting, x, y);
            pg.popStyle();
        }
    }

    @Override
    public String printTitle() {
        String info;

        // get stat
        String name = getTitle();
        String nShooting = "# Shootings: " + this.nShooting;
        String nKilled = "# People killed: " + this.nKilled;
        String nInjured = "# People injured: " + this.nInjured;

        info = name + "\n\n" + nShooting + "\n\n" + nKilled + "\n\n" + nInjured;
        return info;
    }

    @Override
    public String getTitle() {
        return getStateName();
    }

    public String getStateName() {
        return getStringProperty("state");
    }

    // helper methods
    private void determineColorAndSize(PGraphics pg, float x, float y) {
        /* determine the size of the marker and fill its color
        according to the number of shootings in a given state
         */
        float SMALL_RADIUS = 17;
        float LARGE_RADIUS = 25;
        float MED_RADIUS = (SMALL_RADIUS + LARGE_RADIUS) / 2f;
        float BUFFER = 10;
        int TRANSPARENCY = 100;

        int SMALL_NUMBER_SHOOTING_THRESHOLD = 10;
        int LARGE_NUMBER_SHOOTING_THRESHOLD = 30;

        if (this.nShooting < SMALL_NUMBER_SHOOTING_THRESHOLD) {
            pg.fill(120,186,73); // green-ish
            pg.ellipse(x, y, SMALL_RADIUS, SMALL_RADIUS);
            pg.fill(120,186,73,TRANSPARENCY);
            pg.ellipse(x, y, SMALL_RADIUS+BUFFER, SMALL_RADIUS+BUFFER);
        } else if (this.nShooting > LARGE_NUMBER_SHOOTING_THRESHOLD) {
            pg.fill(222,114,102); // red-ish
            pg.ellipse(x, y, LARGE_RADIUS, LARGE_RADIUS);
            pg.fill(222,114,102,TRANSPARENCY); // red-ish
            pg.ellipse(x, y, LARGE_RADIUS+BUFFER, LARGE_RADIUS+BUFFER);
        } else {
            pg.fill(237,193,50); // yellow-ish
            pg.ellipse(x, y, MED_RADIUS, MED_RADIUS);
            pg.fill(237,193,50,TRANSPARENCY); // yellow-ish
            pg.ellipse(x, y, MED_RADIUS+BUFFER, MED_RADIUS+BUFFER);
        }
    }
}
