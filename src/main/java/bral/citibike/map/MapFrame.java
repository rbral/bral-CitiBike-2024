package bral.citibike.map;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import bral.citibike.CitiBikeService;
import bral.citibike.CitiBikeServiceFactory;
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
    private JXMapViewer mapViewer;
    private JPanel mainPanel;
    private JPanel controlPanel;
    private JButton mapButton;
    private JButton clearButton;
    private JTextField fromField;
    private JTextField toField;
    private GeoPosition from;
    private GeoPosition to;
    private GeoPosition startStation;
    private GeoPosition endStation;
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
        if (from == null || to == null)
        {
            JOptionPane.showMessageDialog(this, "Please select both From and To points.");
            return;
        }

        CitiBikeService service = new CitiBikeServiceFactory().getService();


        startStation = new GeoPosition(40.775, -73.950); // Simulated start station
        endStation = new GeoPosition(40.720, -73.960);   // Simulated end station

        route = List.of(from, startStation, endStation, to);

        waypoints = Set.of(
                new DefaultWaypoint(from),
                new DefaultWaypoint(startStation),
                new DefaultWaypoint(endStation),
                new DefaultWaypoint(to)
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
                Set.of(from, startStation, endStation, to),
                1.0
        );
    }

    private void clearMap()
    {
        // clear all data
        from = null;
        to = null;
        fromField.setText("");
        toField.setText("");
        waypoints = Set.of();
        route.clear();

        // Clear Painters
        mapViewer.setOverlayPainter(null);
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

                if (from == null)
                {
                    from = position;
                    fromField.setText(position.getLatitude() + ", " + position.getLongitude());
                } else if (to == null)
                {
                    to = position;
                    toField.setText(position.getLatitude() + ", " + position.getLongitude());
                }
            }
        });

    }
}