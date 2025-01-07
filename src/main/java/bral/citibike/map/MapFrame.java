package bral.citibike.map;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import bral.citibike.*;

import bral.citibike.json.StationObject;
import bral.citibike.json.StationObjects;
import bral.citibike.json.StatusObjects;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Set;

public class MapFrame extends JFrame
{
    private JPanel mainPanel;
    private JPanel controlPanel;
    private JButton mapButton;
    private JButton clearButton;
    private JTextField fromField;
    private JTextField toField;
    private JXMapViewer mapViewer;
    private StationObjects stationsResponse;
    private StatusObjects statusesResponse;
    private CitiBikeUtils utils;
    private GeoPosition fromUserPosition;
    private GeoPosition toUserPosition;
    private GeoPosition startStationPosition;
    private GeoPosition endStationPosition;
    private List<GeoPosition> route;
    private Set<Waypoint> waypoints;

    public MapFrame()
    {
        initializeMapViewer();

        // Display the viewer in a JFrame
        setTitle("CitiBike NYC");
        setSize(850, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);
        mainPanel.add(mapViewer);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));

        fromField = new JTextField(30);
        controlPanel.add(fromField, BorderLayout.WEST);

        toField = new JTextField(30);
        controlPanel.add(toField, BorderLayout.WEST);

        mapButton = new JButton("Map");
        controlPanel.add(mapButton, BorderLayout.WEST);

        clearButton = new JButton("Clear");
        controlPanel.add(clearButton, BorderLayout.WEST);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // button actions
        mapButton.addActionListener(e -> mapRoute());
        clearButton.addActionListener(e -> clearMap());
    }

    private void mapRoute()
    {
        if (fromUserPosition == null || toUserPosition == null)
        {
            JOptionPane.showMessageDialog(this, "Please select both From and To points.");
            return;
        }

        CitiBikeService service = new CitiBikeServiceFactory().getService();
        stationsResponse = service.getStationInformation().blockingGet();
        statusesResponse = service.getStationStatus().blockingGet();

        utils = new CitiBikeUtils(stationsResponse, statusesResponse);

        StationObject startStation = utils.closestStationWithBikes(
                fromUserPosition.getLatitude(), fromUserPosition.getLongitude());
        StationObject endStation = utils.closestStationWithSlots(
                toUserPosition.getLatitude(), toUserPosition.getLongitude());

        startStationPosition = new GeoPosition(startStation.lat, startStation.lon); // Simulated start station
        endStationPosition = new GeoPosition(endStation.lat, endStation.lon);   // Simulated end station

        route = List.of(fromUserPosition, startStationPosition, endStationPosition, toUserPosition);

        waypoints = Set.of(
                new DefaultWaypoint(fromUserPosition),
                new DefaultWaypoint(startStationPosition),
                new DefaultWaypoint(endStationPosition),
                new DefaultWaypoint(toUserPosition)
        );
        updateMap();
    }

    private void updateMap()
    {

        // Waypoint Painter
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        waypointPainter.setWaypoints(waypoints);

        // Route Painter
        RoutePainter routePainter = new RoutePainter(route);

        // using multiple painters
        List<Painter<JXMapViewer>> painters = List.of(
                routePainter,
                waypointPainter
        );

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        mapViewer.setOverlayPainter(painter);


        mapViewer.zoomToBestFit(
                Set.of(fromUserPosition, startStationPosition, endStationPosition, toUserPosition),
                1.0
        );
    }

    private void clearMap()
    {
        // clear all data
        fromUserPosition = null;
        toUserPosition = null;
        startStationPosition = null;
        endStationPosition = null;
        fromField.setText("");
        toField.setText("");

        // clear the route and waypoints
        route = null;
        waypoints = Set.of();

        // Clear Painters
        mapViewer.setOverlayPainter(null);

        mapViewer.repaint();
    }

    public void initializeMapViewer()
    {
        mapViewer = new JXMapViewer();

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        // Set the focus
        GeoPosition newYork = new GeoPosition(40.77, -73.97);

        mapViewer.setZoom(5);
        mapViewer.setAddressLocation(newYork);

        // make it interactive
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);

        mapViewer.addMouseListener(new CenterMapListener(mapViewer));

        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Point2D.Double point = new Point2D.Double(x, y);
                GeoPosition position = mapViewer.convertPointToGeoPosition(point);

                if (fromUserPosition == null)
                {
                    fromUserPosition = position;
                    fromField.setText(position.getLatitude() + ", " + position.getLongitude());
                } else if (toUserPosition == null)
                {
                    toUserPosition = position;
                    toField.setText(position.getLatitude() + ", " + position.getLongitude());
                }
            }
        });

    }
}