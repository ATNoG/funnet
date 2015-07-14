package pt.it.av.atnog.funnet.tarefa05.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import pt.it.av.atnog.funnet.tarefa05.R;

public class Options {
    private static Marker crasto;
    private static Marker caloirodromo;
    private static Marker it;
    private static Marker deti;
    private static Marker greenland;
    private static Marker greenlandC;
    private static Marker antartica;
    private static Marker antarticaC;

    public static void EEInicialization(GoogleMap map)
    {
        crasto = map.addMarker(new MarkerOptions()
                .position(new LatLng(40.624686,-8.657))
                .title("Cantina")
                .snippet("Não confiem muito no prato vegetariano...")
                .icon(BitmapDescriptorFactory.fromResource( R.drawable.dinner)));
        crasto.setVisible(false);

        caloirodromo = map.addMarker(new MarkerOptions()
                .position(new LatLng(40.6296,-8.65576))
                .title("Caloirodromo")
                .snippet("Não alimentar os caloiros.")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinkiepie)));
        caloirodromo.setVisible(false);

        it = map.addMarker(new MarkerOptions()
                .position(new LatLng(40.634215,-8.659962))
                .title("Instituto de Telecomunicações")
                .snippet("Aqui faz-se ciência!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.it)));
        it.setVisible(false);

        deti = map.addMarker(new MarkerOptions()
                .position(new LatLng(40.63328,-8.65955))
                .title("DETI")
                .snippet("Aqui estão os alunos que trabalham.")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.nintendo)));
        deti.setVisible(false);

        greenland = map.addMarker(new MarkerOptions()
                .position(new LatLng(78.080156,-42.1875))
                .title("Gronelândia (1)")
                .snippet("Apesar das aparências, a Europa é 4.7 vezes maior.")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.question)));

        greenlandC = map.addMarker(new MarkerOptions()
                .position(new LatLng(72.080156,-42.1875))
                .title("Gronelândia (2)")
                .snippet("Isto acontece devido à projecção de Mercator.")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.question)));

        antartica = map.addMarker(new MarkerOptions()
                .position(new LatLng(-80, 0))
                .title("Antártica (1)")
                .snippet("Apesar de parecer o maior continente, é o quinto.")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.question)));

        antarticaC = map.addMarker(new MarkerOptions()
                .position(new LatLng(-80, 30))
                .title("Antártica (2)")
                .snippet("Mercator é novamente o culpado!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.question)));

        map.setOnCameraChangeListener(new OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position)
            {
                //System.err.println("ZoomLevel: " + position.zoom);
                if(position.zoom < 17.5)
                {
                    crasto.setVisible(false);
                    caloirodromo.setVisible(false);
                    it.setVisible(false);
                    deti.setVisible(false);
                }
                else
                {
                    crasto.setVisible(true);
                    caloirodromo.setVisible(true);
                    it.setVisible(true);
                    deti.setVisible(true);
                }
            }
        });
    }
}
