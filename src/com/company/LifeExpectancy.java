package com.company;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

import java.util.*;


/**
 * Generates an interactive map (UnfoldingMap library) that displays the life expectancy
 * in various countries around the world using data from WHO (recorded in 2018):
 *      Blue color indicates high life expectancy
 *      Red color indicates low life expectancy
 */
public class LifeExpectancy extends PApplet {
    private UnfoldingMap worldMap;
    private Map<String, Double> lifeExpByCountry;
    private List<Marker> countryMarkers;

    public void setup() {
        // set up canvas
        size(700, 600, OPENGL);

        // set up worldmap
        worldMap = new UnfoldingMap(this, 50, 50, 600, 500, new OpenStreetMap.OpenStreetMapProvider());
        worldMap.zoomToLevel(2);
        MapUtils.createDefaultEventDispatcher(this, worldMap); // make map interactive

        // create lists of features and markers
        List<Feature> countries = GeoJSONReader.loadData(this, "data/countries.geo.json");
        countryMarkers = MapUtils.createSimpleMarkers(countries);
        worldMap.addMarkers(countryMarkers); // add markers to worldmap

        // load life expectancy data
        String pathToFile = "data/LifeExpectancyWorldBank.csv";
        lifeExpByCountry = loadLifeExpectancyFromCSV(pathToFile);

        // shade countries according to recorded life expectancies
        shadeCountries();
    }

    public void draw() {
        background(200);
        worldMap.draw();
    }

    private void shadeCountries() {
        /*
        Colors countries according to their life expectancies recorded in 2018 by WHO;
            The higher the life expectancy, the more "blue" it is;
            The lower the life expectancy, the more "red" it is;
         */
        for (Marker marker : countryMarkers) {
            String countryID = marker.getId();
            if (lifeExpByCountry.containsKey(countryID)) {
                double age = lifeExpByCountry.get(countryID);
                int colorLevel = (int) map((float) age, 40, 90, 0, 255);
                marker.setColor(color(255-colorLevel, 100, colorLevel));
            } else {
                marker.setColor(color(150)); // grey color for countries with no recorded life expectancy
            }
        }
    }

    private Map<String, Double> loadLifeExpectancyFromCSV(String pathToFile) {
        /*
        Reads life expectancy data in CSV format (from WHO) and
        returns a map that maps a countryID to its recorded life expectancy in 2018;
         */
        Map<String, Double> lifeExpectancyMap = new HashMap<String, Double>();
        String[] rows = loadStrings(pathToFile);
        for (String row : rows) {
            String[] columns = row.split(",");
            try {
                String countryID = columns[4];
                double age = Double.parseDouble(columns[17]);
                lifeExpectancyMap.put(countryID, age);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }
        }
        return lifeExpectancyMap;
    }

    public static void main(String[] args) {
        System.setProperty("http.agent", "Chrome");
        PApplet.main("com.company.LifeExpectancy");
    }
}
