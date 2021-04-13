package com.company;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;


public class IncidentMarker extends CommonMarker {
    PImage img;

    // constructor
    public IncidentMarker(PointFeature feature, PImage img) {
        super(feature.location, feature.properties);
        this.nKilled = Integer.parseInt(feature.getProperty("nKilled").toString());
        this.nInjured = Integer.parseInt(feature.getProperty("nInjured").toString());
        this.img = img;
    }

    public String getAddress() {
        return getStringProperty("address");
    }

    public String getStateName() {
        return getStringProperty("state");
    }

    @Override
    public String getTitle() {
        return getAddress();
    }

    @Override
    public void drawMarker(PGraphics pg, float x, float y) {
        pg.pushStyle();
        pg.imageMode(PConstants.CORNER);
        // The image is drawn in object coordinates, i.e. the marker's origin (0,0) is at its geo-location.
        pg.image(img, x-20, y-39);
        pg.popStyle();

    }

    @Override
    public String printTitle() {
        String info;

        // get stat
        String name = getTitle();
        String county = getStringProperty("county");
        String state = getStringProperty("state");
        String date = getStringProperty("date").replaceAll("^\"|\"$", "");
        String nKilled = "# People killed: " + this.nKilled;
        String nInjured = "# People injured: " + this.nInjured;

        info = name + "\n\n" + county + ", " + state + "\n\n" + date + "\n\n" + nKilled + "\n\n" + nInjured;
        return info;
    }

}
