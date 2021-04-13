package com.company;


import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;
import java.util.List;

/**
 * Interactive map showing gun violence incidences in USA from 2018-2020
 * Data source (https://www.gunviolencearchive.org/):
 *
 * The map:
 *      - Each state is marked with a bubble marker that shows the number of gun violence incidents
 *      recorded in each year (2018/2019/2020):
 *          + Data change (and are refreshed) when a specific year is selected;
 *          + Marker is color-coded:
 *              Green if number of shooting < 10
 *              Red if number of shooting > 30
 *              Yellow is anything in between
 *      - When a state is clicked, the state marker disappears and incident markers belonging to that
 *      state appear;
 *      - When the mouse hoovers over a state/incident, information about the gun violence incident(s)
 *      will be displayed on the right hand side of the map including:
 *          + State name (if it is a state marker)
 *          + Address (if it is a specific incident)
 *          + Date of crime (if it is a specific incident)
 *          + Number of shootings (if it is a state marker)
 *          + Number of people killed
 *          + Number of people injured
 */

public class GunViolenceMap extends PApplet {
    private UnfoldingMap map;
    private String selectedYear = "2020"; // default value
    private List<Marker> stateCapitalMarkers;
    private List<Marker> incidentMarkers;
    private Marker lastSelectedMarker;
    private Marker currClick;
    private Marker lastClick;
    private List<Feature> stateCapitals;
    private List<PointFeature> gunIncidents;
    private HashMap<String,HashMap<String,ArrayList<Integer>>> statByYearByState;

    // for debugging purposes
//    private String gunViolenceFile = "data/mass_shooting_test.csv";

    // CONSTANTS
    int BUTTON_SIZE = 12;

    public static void main(String[] args) {
        System.setProperty("http.agent", "Chrome");
        PApplet.main("com.company.GunViolenceMap");
    }

    public void setup() {
        // set up canvas
        size(1000, 700, OPENGL);

        // set up map
        map = new UnfoldingMap(this, 50, 50, 700, 600, new OpenStreetMap.OpenStreetMapProvider());
        map.zoomAndPanTo(4, new Location(31.79, -100.09)); // centered on USA
        MapUtils.createDefaultEventDispatcher(this, map); // make map interactive

        // read in gun violence data (from CSV)
        String gunViolenceFile = "data/mass_shooting_03-2018_03-2021_with_geodata.csv";
        gunIncidents = ParseFeed.readGunViolenceDataFromCSV(this, gunViolenceFile);
        statByYearByState = createStatByYearByState(gunIncidents);

        // create a list of stateCapitalMarkers and add them to map
        /* stateCapitalMarker: located at a state's capital;
         when selected, it displays the number of shooting,
         number of people killed and injured in a selected year.
         */
        String stateFile = "data/usa-state-capitals.geo.json";
        stateCapitals = GeoJSONReader.loadData(this, stateFile);
        stateCapitalMarkers = createStateMarkers(stateCapitals, statByYearByState);
        map.addMarkers(stateCapitalMarkers);

        // create a list of incidentMarkers and add them to map
        /* incidentMarker: located at a specific address (specified by longitude and latitude) where shooting happened;
        when selected, it displays the address, date of crime, number of people killed and injured.
         */
        incidentMarkers = createIncidentMarkers(gunIncidents);
        map.addMarkers(incidentMarkers);
        hideAllMarkers(incidentMarkers);
    }

    public void draw() {
        background(200);
        map.draw();
        drawButtons();
        showInfoBox();
    }

    /** Event handler that gets called automatically when the mouse moves. */
    @Override
    public void mouseReleased() {
        /*
        Refresh the display of markers when a year (different from selectedYear) is selected;
         */
        if (mouseX > 770 && mouseX < 770+BUTTON_SIZE) {
            if (mouseY > 575 && mouseY < 575+BUTTON_SIZE) {
                selectedYear = "2020";
            } else if (mouseY > 600 && mouseY < 600+BUTTON_SIZE) {
                selectedYear = "2019";
            } else if (mouseY > 625 && mouseY < 625+BUTTON_SIZE) {
                selectedYear = "2018";
            }
            map.getDefaultMarkerManager().clearMarkers();
            stateCapitalMarkers = createStateMarkers(stateCapitals, statByYearByState);
            incidentMarkers = createIncidentMarkers(gunIncidents);
            map.addMarkers(stateCapitalMarkers);
            map.addMarkers(incidentMarkers);
            hideAllMarkers(incidentMarkers);
        }
    }

    @Override
    public void mouseMoved() {
        // clear the last selection
        if (lastSelectedMarker != null) {
            lastSelectedMarker.setSelected(false);
            lastSelectedMarker = null;
        }
        selectMarkerIfHover(stateCapitalMarkers);
        if (lastSelectedMarker == null) {
            selectMarkerIfHover(incidentMarkers);
        }
    }

    @Override
    public void mouseClicked() {
        /*
        If you click a new state: the state marker disappears and incident markers
        belonging to that state show up.
        Additionally, incident markers in last clicked state will disappear and
        last clicked state marker will appear again.
         */

        checkStateMarkersForClick(); // get currClick
        if (currClick != null) {
            if (lastClick != null
            && !currClick.getStringProperty("state").equals(lastClick.getStringProperty("state"))) {
                lastClick.setHidden(false); // un-hide the last clicked state
                hideAllMarkers(incidentMarkers); // hide incident markers belonging to the last clicked state
            }
//            map.zoomAndPanTo(7, currClick.getLocation()); // zoom in on clicked state
            currClick.setHidden(true); // hide currently clicked state
            showIncidentMarkersInClickedState(); // show incidents located in the clicked state

            // update lastClick and clear currClick
            lastClick = currClick;
            currClick = null;
        }
    }

    /** helper methods */
    private void showInfoBox() {
//        // draw a box
//        fill(255);
//        rect(770, 50, 200, 200, 10);

        List<Marker> allMarkers = new ArrayList<>();
        allMarkers.addAll(stateCapitalMarkers);
        allMarkers.addAll(incidentMarkers);
        for (Marker m : allMarkers) {
            if (!m.isHidden() && m.isSelected()) {
                CommonMarker marker = (CommonMarker) m;
                String markerInfo = marker.printTitle();
                // show text
                fill(0);
                text(markerInfo, 770, 55);
            }
        }
    }

    private void showIncidentMarkersInClickedState() {
        for (Marker m : incidentMarkers) {
            if (m.getStringProperty("state").equals(currClick.getStringProperty("state"))) {
                m.setHidden(false);
            }
        }
    }

    private void checkStateMarkersForClick() {
        for (Marker m : stateCapitalMarkers) {
            if (m.isInside(map, mouseX, mouseY)) {
                currClick = m;
             }
        }
    }

    private void hideAllMarkers(List<Marker> markers) {
        for (Marker m : markers) {
            m.setHidden(true);
        }
    }

    private void selectMarkerIfHover(List<Marker> markers) {
        // abort if a marker is already selected
        if (lastSelectedMarker != null) return;

        for (Marker m : markers) {
            if (m.isInside(map, mouseX, mouseY)) {
                lastSelectedMarker = m;
                m.setSelected(true);
                return;
            }
        }
    }

    private void drawButtons() {
        /*
        Draw clickable buttons that indicate "Year"
         */

        // make boxes
        fill(255);
        rect(770, 625, BUTTON_SIZE, BUTTON_SIZE); // bottom box (2018)
        rect(770, 600, BUTTON_SIZE, BUTTON_SIZE); // 2019
        rect(770, 575, BUTTON_SIZE, BUTTON_SIZE); // 2020

        // add text
        fill(0);
        textAlign(PConstants.LEFT, PConstants.TOP);
        text("2018", 790, 624);
        text("2019", 790, 599);
        text("2020", 790, 574);

        // shade the selected box
        fill(100);
        switch (selectedYear) {
            case "2018":
                rect(770, 625, BUTTON_SIZE, BUTTON_SIZE);
                break;
            case "2019":
                rect(770, 600, BUTTON_SIZE, BUTTON_SIZE);
                break;
            case "2020":
                rect(770, 575, BUTTON_SIZE, BUTTON_SIZE);
                break;
        }
    }

    private HashMap<String,HashMap<String,ArrayList<Integer>>> createStatByYearByState(List<PointFeature> gunIncidents) {
        /*
        Generates stat for each state in a given year:
            - Number of shootings
            - Number of people killed
            - Number of people injured
         */
        HashMap<String,HashMap<String,ArrayList<Integer>>> statByYearByState = new HashMap<>();
        for (Feature incident: gunIncidents) {
            // get stats
            ArrayList<Integer> stat;
            String state = incident.getProperty("state").toString();
            String year = incident.getProperty("year").toString();
            int nKilled = Integer.parseInt(incident.getProperty("nKilled").toString());
            int nInjured = Integer.parseInt(incident.getProperty("nInjured").toString());
            int nShooting = 0;

            if (! statByYearByState.containsKey(year)) {
                statByYearByState.put(year, new HashMap<>());
            }

            if (! statByYearByState.get(year).containsKey(state)) {
                stat = new ArrayList<>(Arrays.asList(nShooting+1, nKilled, nInjured));
            } else {
                nShooting = statByYearByState.get(year).get(state).get(0);
                int currKilled = statByYearByState.get(year).get(state).get(1);
                int currInjured = statByYearByState.get(year).get(state).get(2);
                stat = new ArrayList<>(Arrays.asList(nShooting+1, nKilled+currKilled, nInjured+currInjured));
            }
            statByYearByState.get(year).put(state, stat);
        }
        return statByYearByState;
    }

    private List<Marker> createStateMarkers(List<Feature> stateCapitals, HashMap<String,HashMap<String,ArrayList<Integer>>> statByYearByState) {
        List<Marker> markers = new ArrayList<>();
        HashMap<String,ArrayList<Integer>> statByState = statByYearByState.get(selectedYear);

        for (Feature state : stateCapitals) {
            try {
                StateMarker m = new StateMarker((PointFeature) state, statByState);
                markers.add(m);
            } catch (Exception ignored) {}
        }
        return markers;
    }

    private List<Marker> createIncidentMarkers(List<PointFeature> gunIncidents) {
        List<Marker> markers = new ArrayList<>();
        for (PointFeature gunIncident : gunIncidents) {
            String year = gunIncident.getStringProperty("year");
            if (year.equals(selectedYear)) {
                Marker m = new IncidentMarker(gunIncident, loadImage("data/marker8.png"));
                markers.add(m);
            }
        }
        return markers;
    }
}
