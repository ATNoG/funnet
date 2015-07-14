package pt.it.av.atnog.funnet.tarefa07.usersdb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.google.android.gms.maps.model.LatLng;
import pt.it.av.atnog.funnetlib.json.JSONArray;
import pt.it.av.atnog.funnetlib.json.JSONObject;
import pt.it.av.atnog.funnetlib.json.JSONValue;
import pt.it.av.atnog.funnetlib.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UsersDB implements Runnable, Closeable{
    private static final String DEFAULT_URL="http://twoflower.ddns.net";
    private static final int DEFAULT_RATE = 3000;
    private static final int MAX_SIDE = 64;
    private final ScheduledThreadPoolExecutor s;
    private final String  url;
    private final User selfUser;
    private final Map<String, User> map;
    private final int rate;
    private long timestamp = 0;
    private boolean insert = true;
    private Lock selfLock = new ReentrantLock(), mapLock = new ReentrantLock();

    public UsersDB(String user, Bitmap icon) {
        this(user, icon, DEFAULT_URL, DEFAULT_RATE);
    }

    public UsersDB(String user, Bitmap icon, String url, int rate) {
        Bitmap scaledIcon;

        if(icon.getWidth() > icon.getHeight() && icon.getWidth() > MAX_SIDE) {
            double ratio = MAX_SIDE/(double) icon.getWidth();
            scaledIcon = Bitmap.createScaledBitmap(icon, MAX_SIDE, (int)Math.round(icon.getHeight()*ratio), false);
        } else if(icon.getHeight() > icon.getWidth() && icon.getHeight() > MAX_SIDE) {
            double ratio = MAX_SIDE /(double) icon.getHeight();
            scaledIcon = Bitmap.createScaledBitmap(icon, (int)Math.round(icon.getWidth()*ratio), MAX_SIDE, false);
        } else {
            scaledIcon = icon;
        }

        this.selfUser = new User(user, scaledIcon);
        this.url = url;
        this.rate = rate;
        map = new HashMap<String, User>();
        s = new ScheduledThreadPoolExecutor(1);
        s.scheduleWithFixedDelay(this, rate, rate, TimeUnit.MILLISECONDS);
    }

    public void update(double lat, double lon) {
        selfLock.lock();
        try {
            selfUser.latlng = new LatLng(lat, lon);
        } finally {
            selfLock.unlock();
        }
    }

    public LatLng selfPos() {
        LatLng rv = null;

        selfLock.lock();
        try {
            rv = selfUser.latlng;
        } finally {
            selfLock.unlock();
        }

        return rv;
    }

    public String selfName() { return selfUser.name; }

    public Bitmap selfIcon() {
        return selfUser.icon;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<User>();

        mapLock.lock();
        try {
            users.addAll(map.values());
        } finally {
            mapLock.unlock();
        }

        selfLock.lock();
        try {
            users.add(selfUser);
        } finally {
            selfLock.unlock();
        }

        return users;
    }

    public void run() {
        // Sync myself
        boolean OK = true;
        System.err.println("SYNC MYSELF");
        JSONObject data = null;
        selfLock.lock();
        try {
        if(selfUser.latlng != null) {
            if (insert)
                data = selfUser.insert2JSON();
            else
                data = selfUser.update2JSON();
        } else
            OK = false;
        } finally {
            selfLock.unlock();
        }

        if(OK) {
            try {
                if (insert) {
                    HTTP.post(url + "/usersdb/insert", data);
                    insert = false;
                } else
                    HTTP.post(url + "/usersdb/update", data);
            } catch (Exception e) {
                e.printStackTrace();
                insert = true;
                OK = false;
            }
        }
        System.err.println("SYNC MYSELF DONE");
        System.err.println("INSERT = "+insert);
        System.err.println("OK     = "+OK);
        System.err.println("SYNC OTHERS");
        // Sync the remaing users
        if(OK) {
            JSONArray update = null, insert = null;
            try {
                JSONObject json = JSONObject.read(HTTP.get(url + "/usersdb/sync/?user=" + selfUser.name + "&time=" + timestamp));
                update = json.get("update").asArray();
                insert = json.get("insert").asArray();
            } catch (Exception e) {
                OK = false;
                e.printStackTrace();
            }
            if(OK) {
                System.err.println("Valid arrays");
                mapLock.lock();
                try {
                    System.err.println("UPDATES and DELETES");
                    Set<String> localUsers = map.keySet();
                    for (String user : localUsers) {
                        int idx = idx(user, update);
                        System.err.println("USER = "+user+" IDX = "+idx);
                        if (idx >= 0) {
                            JSONObject json = update.get(idx).asObject();
                            map.get(user).latlng = new LatLng(json.get("lat").asDouble(),
                                    json.get("lon").asDouble());
                        } else {
                            map.remove(user);
                        }
                    }
                    System.err.println("UPDATES and DELETES DONE");
                    System.err.println("INSERT");
                    for (JSONValue value : insert) {
                        JSONObject json = value.asObject();
                        System.err.println(json);
                        String user = json.get("user").asString();
                        map.put(user, new User(user,
                                json.get("iconB64").asString(),
                                json.get("lat").asDouble(),
                                json.get("lon").asDouble()));
                        if (json.get("time").asLong() > timestamp)
                            timestamp = json.get("time").asLong();
                    }
                } finally {
                    mapLock.unlock();
                }
                System.err.println("INSERT DONE");
            }
        }
        System.err.println("SYNC OTHERS DONE");
    }

    public void close() throws IOException {
        s.shutdown();
        while(!s.isTerminated());
    }

    public class User {
        public LatLng latlng;
        public final String name;
        public final Bitmap icon;

        public User(String name, Bitmap icon) {
            this(name, icon, null);
        }

        public User(String name, String iconB64, double lat, double lon) {
            this(name, base642Bitmap(iconB64), new LatLng(lat, lon));
        }

        public User(String name, Bitmap icon, double lat, double lon) {
            this(name, icon, new LatLng(lat, lon));
        }

        public User(String name, Bitmap icon, LatLng latlng) {
            System.err.println("NEW USER");
            this.name = name;
            this.icon = icon;
            this.latlng = latlng;
            System.err.println("NEW USER DONE");
        }

        public JSONObject update2JSON() {
            JSONObject json = new JSONObject();
            json.add("user", name);
            json.add("lat", latlng.latitude);
            json.add("lon", latlng.longitude);
            return json;
        }

        public JSONObject insert2JSON() {
            JSONObject json = update2JSON();
            json.add("iconB64", bitmap2Base64(icon));
            return json;
        }
    }

    private static String bitmap2Base64(Bitmap img)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, os);
        return Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
    }

    private static Bitmap base642Bitmap(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private static int idx(String user, JSONArray array) {
        int idx = -1;
        for(int i = 0; i < array.size() && idx < 0; i++)
        {
            JSONObject json = array.get(i).asObject();
            if(json.get("user").asString().equals(user))
                idx = i;
        }
        return idx;
    }
}