package pt.it.av.atnog.funnet.tarefa06.map;

import java.util.ArrayList;
import java.util.List;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Handler;
import android.os.Message;
import pt.it.av.atnog.funnet.tarefa06.usersdb.UsersDB;

public class HandlerMapDisplay extends Handler {
    private boolean firstTime = true;
    private ArrayList<Marker> markers;
    private GoogleMap map;
    private UsersDB db;
    private String nome;

    public HandlerMapDisplay(GoogleMap map, UsersDB db, String nome) {
        this.map = map;
        this.nome = nome;
        this.db = db;
        this.markers = new ArrayList<Marker>();
    }

    public void handleMessage(Message message) {
        if (firstTime) {
            LatLng c = db.selfPos();
            if (c != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(c, 18.0f));
                markers.add(map.addMarker(new MarkerOptions()
                        .position(db.selfPos())
                        .title(db.selfName())
                        .icon(BitmapDescriptorFactory.fromBitmap(db.selfIcon()))));
                firstTime = false;
            }
        } else {

            for (Marker m : markers)
                m.remove();
            markers.clear();

            List<UsersDB.User> users = db.getUsers();
            for (UsersDB.User user : users) {
                markers.add(map.addMarker(new MarkerOptions()
                        .position(user.latlng)
                        .title(user.name)
                        .icon(BitmapDescriptorFactory.fromBitmap(db.selfIcon()))));
            }
        }
    }
}
