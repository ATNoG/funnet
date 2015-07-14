package pt.it.av.atnog.funnet.tarefa07;

import java.util.TimerTask;
import android.os.Handler;

public class TTMethod extends TimerTask {
    private Handler h;

    public TTMethod(Handler h)
    {
        this.h = h;
    }

    public void run() {
        h.obtainMessage(1).sendToTarget();
    }
}