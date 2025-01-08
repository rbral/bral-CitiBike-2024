package bral.citibike.map;

import bral.citibike.CitiBikeService;
import bral.citibike.CitiBikeServiceFactory;
import bral.citibike.CitiBikeUtils;
import bral.citibike.json.StationObject;
import bral.citibike.json.StationObjects;
import bral.citibike.json.StatusObjects;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.util.List;
import java.util.Set;

public class MapController
{
    private MapFrame view;

    private CitiBikeService service;
    private StationObjects stationsResponse;
    private StatusObjects statusesResponse;
    private CitiBikeUtils utils;

    public MapController(MapFrame view)
    {
        this.view = view;
    }

    public void mapRoute()
    {
        if (view.fromUserPosition == null || view.toUserPosition == null)
        {
            JOptionPane.showMessageDialog(view, "Please select both From and To points.");
            return;
        }

        service = new CitiBikeServiceFactory().getService();
        stationsResponse = service.getStationInformation().blockingGet();
        statusesResponse = service.getStationStatus().blockingGet();

        utils = new CitiBikeUtils(stationsResponse, statusesResponse);

        StationObject startStation = utils.closestStationWithBikes(
                view.fromUserPosition.getLatitude(), view.fromUserPosition.getLongitude());
        StationObject endStation = utils.closestStationWithSlots(
                view.toUserPosition.getLatitude(), view.toUserPosition.getLongitude());

        view.startStationPosition = new GeoPosition(startStation.lat, startStation.lon); // Simulated start station
        view.endStationPosition = new GeoPosition(endStation.lat, endStation.lon);   // Simulated end station

        view.route = List.of(view.fromUserPosition, view.startStationPosition, view.endStationPosition, view.toUserPosition);

        view.waypoints = Set.of(
                new DefaultWaypoint(view.fromUserPosition),
                new DefaultWaypoint(view.startStationPosition),
                new DefaultWaypoint(view.endStationPosition),
                new DefaultWaypoint(view.toUserPosition)
        );
        view.updateMap();
    }

    public void clearMap()
    {
        // clear all data
        view.fromUserPosition = null;
        view.toUserPosition = null;
        view.startStationPosition = null;
        view.endStationPosition = null;
        view.fromField.setText("");
        view.toField.setText("");

        // clear the route and waypoints
        view.route = null;
        view.waypoints = Set.of();

        // Clear Painters
        view.mapViewer.setOverlayPainter(null);

        view.mapViewer.repaint();
    }
}
