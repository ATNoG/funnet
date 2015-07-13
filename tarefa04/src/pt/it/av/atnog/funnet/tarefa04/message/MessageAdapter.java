package pt.it.av.atnog.funnet.tarefa04.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MessageAdapter extends SimpleAdapter {
    private List<? extends Map<String, ?>> values;
    private static Map<String, Integer> colors;
    private static int[] color = {Color.YELLOW, Color.CYAN, Color.RED, Color.GREEN, Color.parseColor("#FF7F00"), Color.parseColor("#FFC0CB")};

    public MessageAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.values = data;
        colors = new HashMap<String, Integer>();
        // EASTER EGG
        colors.put("REDES_DIVERTIDAS",Color.WHITE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent); // The whole item
        TextView title = (TextView) view.findViewById(android.R.id.text1); // The title
        TextView message = (TextView) view.findViewById(android.R.id.text2); // The message

        // Alternates the background and text with different colors
        if(position % 2 == 0)
        {
            view.setBackgroundColor(0x30808080);
            message.setTextColor(Color.WHITE);
        }
        else
        {
            view.setBackgroundColor(0x30ffffff);
            message.setTextColor(Color.WHITE);
        }

        // If the name doesn't have an associated color, associate one
        if(colors.get(values.get(position).get("name")) == null)
        {
            colors.put((String) values.get(position).get("name"), color[colors.size()%color.length]);
        }

        // Set associated color to name
        title.setTextColor(colors.get(values.get(position).get("name")));

        // EASTER EGG
        if(values.get(position).get("name").equals("REDES_DIVERTIDAS"))
        {
            view.setBackgroundColor(Color.RED);
        }

        return view;
    }

    /**
     * Doesn't work with neutral colors
     * @param color
     * @return
     */
    public static int getContrastColor(int color)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[0] = (hsv[0] + 180) % 360;
        return Color.HSVToColor(hsv);
    }
}
