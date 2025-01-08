package bral.citibike;

import bral.citibike.json.StationObject;
import bral.citibike.json.StationObjects;
import bral.citibike.json.StatusObject;
import bral.citibike.json.StatusObjects;

public class CitiBikeUtils
{
    private StationObjects stations;
    private StatusObjects statuses;

    public CitiBikeUtils(StationObjects stations, StatusObjects statuses)
    {
        this.stations = stations;
        this.statuses = statuses;
    }

    // Find the Status of a Station given the Station’s station_id
    public StatusObject findStationStatus(String stationId)
    {
        for (StatusObject status : statuses.data.stations)
        {
            if (status.station_id.equals(stationId))
            {
                return status;
            }
        }
        return null;
    }

    // Find the closest station (with available bikes) to a given location.
    public StationObject closestStationWithBikes(double userLat, double userLon)
    {
        StationObject closestStation = null;
        double minDistance = Double.MAX_VALUE;

        for (StationObject station : stations.data.stations)
        {
            StatusObject currStatus = findStationStatus(station.station_id);
            if (currStatus != null && currStatus.num_bikes_available > 0)
            {
                double distance = calculateDistance(station.lat, station.lon,
                        userLat, userLon);
                if (distance < minDistance)
                {
                    minDistance = distance;
                    closestStation = station;
                }
            }
        }
        return closestStation;
    }

    // Find the closest station (with available slots) to a given location.
    public StationObject closestStationWithSlots(double userLat, double userLon)
    {
        StationObject closestStation = null;
        double minDistance = Double.MAX_VALUE;

        for (StationObject station : stations.data.stations)
        {
            StatusObject currStatus = findStationStatus(station.station_id);
            if (currStatus != null && currStatus.num_docks_available > 0)
            {
                double distance = calculateDistance(station.lat, station.lon,
                        userLat, userLon);
                if (distance < minDistance)
                {
                    minDistance = distance;
                    closestStation = station;
                }
            }
        }
        return closestStation;
    }


    private double calculateDistance(double lat1, double lon1,
                                     double lat2, double lon2)
    {
        final int earthRadiusKm = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }
}
