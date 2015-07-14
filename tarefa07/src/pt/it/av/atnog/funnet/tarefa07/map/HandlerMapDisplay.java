package pt.it.av.atnog.funnet.tarefa07.map;

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
import pt.it.av.atnog.funnet.tarefa07.usersdb.UsersDB;

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

    }
}
