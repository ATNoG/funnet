package pt.ua.it.atnog;

import pt.it.av.atnog.utils.HTTP;
import pt.it.av.atnog.utils.json.JSONArray;
import pt.it.av.atnog.utils.json.JSONObject;
import pt.it.av.atnog.utils.json.JSONValue;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IMClient implements Runnable, Closeable {
    private static final int DEFAULT_RATE = 1;
    private static final String DEFAULT_URL = "http://twoflower.ddns.net";
    private static final int DEFAULT_MAX_SIZE = 10;
    private final ScheduledThreadPoolExecutor s;
    private final String user, url;
    private final int rate;
    private int id = 0;
    private CircularArray<String> bufferOut;
    private List<Msg> bufferIn;
    private Lock out, in;

    public IMClient(String user) {
        this(user, DEFAULT_URL, DEFAULT_RATE, DEFAULT_MAX_SIZE);
    }

    public IMClient(String user, String url, int rate, int maxSize) {
        this.user = user;
        this.url = url;
        this.rate = rate;
        s = new ScheduledThreadPoolExecutor(1);
        s.scheduleAtFixedRate(this, rate, rate, TimeUnit.SECONDS);
        bufferOut = new CircularArray<String>(maxSize);
        bufferIn = new ArrayList<Msg>();
        out = new ReentrantLock();
        in = new ReentrantLock();
    }

    public void reset() {
        id = 0;
    }

    public void send(String txt) {
        if (!txt.isEmpty()) {
            out.lock();
            bufferOut.in(txt);
            out.unlock();
        }
    }

    public List<Msg> recv() {
        List<Msg> rv = null;

        in.lock();
        if (bufferIn.size() > 0) {
            rv = new ArrayList<Msg>(bufferIn);
            bufferIn.clear();
        } else {
            rv = new ArrayList<Msg>(0);
        }
        in.unlock();

        return rv;
    }

    private JSONObject buffer2JSON() {
        JSONObject json = new JSONObject();
        json.add("user", user);
        JSONArray array = new JSONArray();
        for (String txt : bufferOut)
            array.add(txt);
        json.add("msgs", array);
        return json;
    }

    public void run() {
        out.lock();
        if (bufferOut.size() > 0) {
            JSONObject data = buffer2JSON();
            try {
                HTTP.post(url + "/im/send/", data);
                bufferOut.clear();
            } catch (IOException e) {
                // If HTTP Post fail, do no clean the outgoing buffer
                e.printStackTrace();
            }
        }
        out.unlock();

        JSONArray array = null;
        try {
            JSONObject json = JSONObject.read(HTTP.get(url + "/im/recv/" + id));
            array = json.get("msgs").asArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (array != null) {
            in.lock();
            for (JSONValue value : array) {
                JSONObject obj = value.asObject();
                bufferIn.add(new Msg(obj.get("user").asString(), obj.get("txt").asString(), obj.get("id").asInt()));
            }
            if (bufferIn.size() > 0)
                id = bufferIn.get(bufferIn.size() - 1).id();
            in.unlock();
        }
    }

    public void close() throws IOException {
        s.shutdown();
        while (!s.isTerminated()) ;
    }
}
