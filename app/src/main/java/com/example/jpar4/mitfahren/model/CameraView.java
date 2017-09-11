package com.example.jpar4.mitfahren.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jpar4 on 2017-09-09.
 */

public class CameraView {
    LatLng centertLocation;
    float properZoomLevel;

    public LatLng getCentertLocation() {
        return centertLocation;
    }

    public void setCentertLocation(LatLng centertLocation) {
        this.centertLocation = centertLocation;
    }

    public float getProperZoomLevel() {
        return properZoomLevel;
    }

    public void setProperZoomLevel(float properZoomLevel) {
        this.properZoomLevel = properZoomLevel;
    }
}
