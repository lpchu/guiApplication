package com.company;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;


public class demo extends PApplet {
    private UnfoldingMap map;

    public void setup() {
        // set up canvas
        size(700,600, OPENGL);

        // set up map
        map = new UnfoldingMap(this, 50, 50, 600, 500, new OpenStreetMap.OpenStreetMapProvider());
        map.zoomToLevel(2);
        MapUtils.createDefaultEventDispatcher(this, map); // make map interactive
    }

    public void draw() {
        background(100);
        map.draw();
    }

    public static void main(String[] args) {
        System.setProperty("http.agent", "Chrome");
        PApplet.main("com.company.demo");

    }
}
