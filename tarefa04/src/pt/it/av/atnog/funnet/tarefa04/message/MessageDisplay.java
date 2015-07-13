package pt.it.av.atnog.funnet.tarefa04.message;

import pt.it.av.atnog.funnetlib.Msg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDisplay {
    private MessageAdapter adapter;
    private List<Map<String,String>> messages;

    public MessageDisplay(MessageAdapter adapter, List<Map<String,String>> messages)
    {
        this.adapter = adapter;
        this.messages = messages;
    }

    public void print(Msg msg)
    {
        int lastMessageIndex = messages.size()-1;
        if(lastMessageIndex >= 0 && messages.get(lastMessageIndex).get("name").equals(msg.user())) {
            // Caso se queira meter as mensagens da mesma pessoa no mesmo item
            messages.get(lastMessageIndex).put("message", messages.get(lastMessageIndex).get("message") + "\n" + msg.txt());
        } else {
            // Caso contrário
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", msg.txt());
            map.put("name", msg.user());
            messages.add(map);
        }
        adapter.notifyDataSetChanged();
    }
}
