package com.example.fazlulhoque.iiucbustracking.Modules;

/**
 * Created by Md Azad on 12/21/2017.
 */


     //   import com.google.android.gms.maps.model.LatLng;

import com.directions.route.RouteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);

    void onRoutingFailure(RouteException e);

    void onRoutingStart();

    void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex);

    void onRoutingCancelled();
}