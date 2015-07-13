package pt.it.av.atnog.funnet.tarefa04.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;


import pt.it.av.atnog.funnetlib.IMClient;
import pt.it.av.atnog.funnet.tarefa04.R;
import pt.it.av.atnog.funnet.tarefa04.TTMethod;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

public class MessageFragment extends Fragment {
    private HandlerDisplay markerHandler;
    private Timer timer;
    private static IMClient im;
    private static MessageAdapter adapter;
    private static List<Map<String,String>> messages;
    private static MessageDisplay display;
    private String nome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bun = getArguments();
        this.nome = bun.getString("name");

        if(adapter == null)
        {
            messages = new ArrayList<Map<String,String>>();
            im = new IMClient(nome);
            adapter = new MessageAdapter(getActivity(), messages, android.R.layout.simple_list_item_2, new String[] {"name", "message"}, new int[] { android.R.id.text1, android.R.id.text2});
            display = new MessageDisplay(adapter, messages);
        }

        markerHandler = new HandlerDisplay(display, im);

        timer = new Timer();
        TTMethod method = new TTMethod(markerHandler);
        timer.schedule(method, 500, 1000);

        return inflater.inflate(R.layout.activity_messages, container, false);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView list = (ListView) getActivity().findViewById(R.id.counter);
        list.setAdapter(adapter);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setStackFromBottom(true);
    }

    public void sendMessage(View view) {
        EditText editText = (EditText) getActivity().findViewById(R.id.edit_message);
        markerHandler.receiveMessage(editText);
    }
}
