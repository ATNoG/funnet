package pt.it.av.atnog.funnetlib;

import pt.it.av.atnog.funnetlib.json.JSONObject;

public class Msg {
    private final String user, txt;
    private int id;

    public Msg(String user, String txt, int id) {
        this.user = user;
        this.txt = txt;
        this.id = id;
    }

    public Msg(JSONObject json) {
        user = json.get("user").asString();
        txt = json.get("txt").asString();
        id = json.get("id").asInt();
    }

    public String user() {
        return user;
    }

    public String txt() {
        return txt;
    }

    public int id() {
        return id;
    }
}
