package com.example.fazlulhoque.iiucbustracking.Modules;

/**
 * Created by Md Azad on 12/21/2017.
 */

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}