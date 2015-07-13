package pt.ua.it.atnog;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.eclipsesource.json.JsonValue;
import pt.ua.it.atnog.http.HTTP;

public class IMClient implements Runnable, Closeable {
    private static final int DEFAULT_RATE = 1;
    private static final String DEFAULT_URL="http://funnet.aws.atnog.av.it.pt:8080";
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

    public IMClient(String user, String url, int rate, int maxSize)
    {
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
        if(!txt.isEmpty()) {
            out.lock();
            bufferOut.in(txt);
            out.unlock();
        }
    }

    public List<Msg> recv() {
        List<Msg> rv = null;

        in.lock();
        if(bufferIn.size() > 0) {
            rv = new ArrayList<Msg>(bufferIn);
            bufferIn.clear();
        } else {
            rv = new ArrayList<Msg>(0);
        }
        in.unlock();

        return rv;
    }

    private JsonObject buffer2JSON() {
        JsonObject json = new JsonObject();
        json.set("user", user);
        JsonArray array = new JsonArray();
        for(String txt : bufferOut)
            array.add(txt);
        json.set("msgs", array);
        return json;
    }

    public void run() {
        out.lock();
        if (bufferOut.size() > 0) {
            JsonObject data = buffer2JSON();
            try {
                HTTP.post(url + "/im/send/", "application/json", data.toString());
                bufferOut.clear();
            } catch (IOException e) {
                // If HTTP Post fail, do no clean the outgoing buffer
                e.printStackTrace();
            }
        }
        out.unlock();

        JsonArray array = null;
        try {
            JsonObject json = JsonObject.readFrom(HTTP.get(url + "/im/recv/" + id));
            array = json.get("msgs").asArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (array != null) {
            in.lock();
            for (JsonValue value : array) {
                JsonObject obj = value.asObject();
                bufferIn.add(new Msg(obj.get("user").asString(), obj.get("txt").asString(), obj.get("id").asInt()));
            }
            if (bufferIn.size() > 0)
                id = bufferIn.get(bufferIn.size() - 1).id();
            in.unlock();
        }
    }

    public void close() throws IOException {
        s.shutdown();
        while(!s.isTerminated());
    }
}
