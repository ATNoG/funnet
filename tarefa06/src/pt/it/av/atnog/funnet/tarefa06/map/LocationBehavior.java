package pt.it.av.atnog.funnet.tarefa06.map;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import pt.it.av.atnog.funnet.tarefa06.usersdb.UsersDB;

public class LocationBehavior {
    private static final int TIME = 1000 * 15;
    private Location lastLocation = null;
    private UsersDB db;

    LocationBehavior(LocationManager locationManager, UsersDB db) {
        this.db = db;
        locationManager.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, locationListener);
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private boolean isBetterLocation(Location location) {

        if (lastLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - lastLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TIME;
        boolean isSignificantlyOlder = timeDelta < -TIME;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - lastLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                lastLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private void changeLocation(Location location) {
        if (isBetterLocation(location)) {
            this.db.update(location.getLatitude(), location.getLongitude());
            lastLocation = location;
        }
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            changeLocation(location);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };
}
