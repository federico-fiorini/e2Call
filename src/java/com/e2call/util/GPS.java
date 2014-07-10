package com.e2call.util;

/**
 * Simply class for manage GPS latitude, longitude
 * @author FEDE
 */
final public class GPS {
    private final double latitude;
    private final double longitude;
    private final double altitude;

    public GPS(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    
    public double getAltitude() {
        return altitude;
    }

}
