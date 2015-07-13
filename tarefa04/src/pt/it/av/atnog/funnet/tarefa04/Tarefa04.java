package pt.it.av.atnog.funnet.tarefa04;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import pt.it.av.atnog.funnet.tarefa04.message.MessageFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;

public class Tarefa04 extends FragmentActivity {
    private final String teamName = "funnet"; // Atenção, o nome não pode ter espaços
    private final int icon = R.drawable.marker; // Nome do icon

    private MessageFragment messenger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_LEFT_ICON); // Adição do icon à title bar
        setContentView(R.layout.main);
        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher); // Associação do icon com o recurso
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) != ConnectionResult.SUCCESS)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Google Play Services were not detected, the application can't work correctly without them.")
                    .setCancelable(false)
                    .setTitle("Error")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            // Check that the activity is using the layout version with
            // the fragment_container FrameLayout
            if (findViewById(R.id.fragment_container) != null) {
                // However, if we're being restored from a previous state,
                // then we don't need to do anything and should return or else
                // we could end up with overlapping fragments.
                if (savedInstanceState != null) {
                    return;
                }

                messenger = new MessageFragment();
                Bundle bun = new Bundle();
                bun.putInt("icon", icon);
                bun.putString("name", teamName);
                messenger.setArguments(bun);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, messenger).commit();
            }
        }
    }

    public void sendMessage(View view) {
        messenger.sendMessage(view);
    }

    public void changeActivity(View view) {
        Bundle args = new Bundle();
        args.putString("name", teamName);
        messenger.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, messenger);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
