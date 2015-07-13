package pt.it.av.atnog.funnet.tarefa04.message;

import java.util.List;


import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import pt.it.av.atnog.funnetlib.IMClient;
import pt.it.av.atnog.funnetlib.Msg;

public class HandlerDisplay extends Handler {
    private MessageDisplay display;
    private IMClient im;

    public HandlerDisplay(MessageDisplay display, IMClient im) {
        this.display = display;
        this.im = im;
    }

    public void handleMessage(Message msg) {
        List<Msg> msgs = im.recv();
        for(Msg m : msgs)
            display.print(m);
    }

    public void receiveMessage(EditText text)
    {
        if (text.getText().toString().length() != 0) {
            im.send(text.getText().toString());
            text.setText("");
        }
    }
}
