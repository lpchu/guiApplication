package com.company;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.data.PointFeature;
import parsing.ParseFeed;
import processing.core.PApplet;

import java.util.*;
import java.util.List;

/**
 * Generates an interactive map (UnfoldingMap library) that displays earthquake events around the world;
 */
public class EarthquakeCityMap extends PApplet {
    private UnfoldingMap map;

    // feed with magnitude 2.5+ Earthquakes
    String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";


    public static void main(String[] args) {
        System.setProperty("http.agent", "Chrome");
        PApplet.main("com.company.EarthquakeCityMap");
    }

    public void setup() {
        // set up canvas
        size(850, 600, OPENGL);

        // set up map;
        map = new UnfoldingMap(this, 200, 50, 600, 500, new OpenStreetMap.OpenStreetMapProvider());
        map.zoomToLevel(2);
        MapUtils.createDefaultEventDispatcher(this, map); // make map interactive

        // read in earthquake data
        List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);

        // create a list of markers corresponding to the list of earthquakes
        List<Marker> markers = createMarkers(earthquakes);
        map.addMarkers(markers);
    }

    private List<Marker> createMarkers(List<PointFeature> features) {
        /*
        Creates a list of markers (SimplePointMarker objects) from locations extracted
        from a list of PointFeature, then style each marker according the
        magnitude of each earthquake event:
            Magnitude 5.0+ -> moderate -> big red dots
            Magnitude 4.0+ -> light -> medium yellow dots
            Magnitude <4.0 -> minor -> small blue dots
         */
        double THRESHOLD_MODERATE = 5.;
        double THRESHOLD_LIGHT = 4.;
        List<Marker> markers = new ArrayList<>();
        for (PointFeature feature : features) {
//            System.out.println(feature.properties); // print all properties of PointFeature
            Location loc = feature.getLocation();
            SimplePointMarker marker = new SimplePointMarker(loc, feature.properties);
            double magnitude = Double.parseDouble(marker.getProperty("magnitude").toString());
            if (magnitude > THRESHOLD_MODERATE) {
                marker.setRadius(15);
                marker.setColor(color(255,0,0));
            } else if (magnitude > THRESHOLD_LIGHT && magnitude < THRESHOLD_MODERATE) {
                marker.setRadius(10);
                marker.setColor(color(255,255,0));
            } else {
                marker.setRadius(5);
                marker.setColor(color(0,0,255));
            }
            markers.add(marker);
        }
        return markers;
    }

    private void addLegend() {
        /*
        Use processing package to draw legends
         */
        // shapes
        strokeWeight(1);
        fill(255);
        rect(20,50,150,180);
        fill(255,0,0);
        ellipse(43,105,15,15);
        fill(255,255,0);
        ellipse(43,145,10,10);
        fill(0,0,255);
        ellipse(43,185,5,5);

        // text
        strokeWeight(2);
        fill(0);
        text("Earthquake Keys", 50, 70);
        text("5.0+ Magnitude", 60, 110);
        text("4.0+ Magnitude", 60, 150);
        text("<4.0 Magnitude", 60, 190);
    }

    public void draw() {
        background(200);
        map.draw();
        addLegend();
    }
}
