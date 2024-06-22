package fr.doranco.myquiz.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.doranco.myquiz.R;
import fr.doranco.myquiz.model.User;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView mGreetingText;
    private EditText mNameInput;
    private Button mPlayButton;
    private User mUser;
    public static final int GAME_ACTIVITY_REQUEST_CODE = 22; // on peut mettre la valeur que l'on souhaite
    private SharedPreferences mPreferences; // va contenir les infos de l'utilisateur comme des cookies (nom, score, ...)

    public static final String PREF_KEY_FIRSTNAME = "PREF_KEY_FIRSTNAME"; // param utilisé pour stocker le nom dans les préférences
    public static final String PREF_KEY_SCORE = "PREF_KEY_SCORE"; // param utilisé pour stocker le score dans les préférences

    // Quand une activité nous renvoie un résultat, une méthode spécifique est appelée sur notre activité principale :
    // elle s'appelle "onActivityResult(...)" qu'on devra surcharger.
    // Dans cette méthode appelée automatiquement par android on va donc récupérer le résultat du score.
    // Pour générer cette méthode : faire click sur bouton droit de la souris puis cliquer sur "override Methods" ensuite choisir
    // la méthode "onActivityResult(...)"
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Récupérer le score à partir de l'Intent nommé ici "data"
        if (requestCode == GAME_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            //
            int score = data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0); // Le second paramètre correspond à
            // la valeur par défaut si le BUNDLE_EXTRA_SCORE n'est pas renseigné, ici on a mis 0.
            mPreferences.edit().putInt(PREF_KEY_SCORE, score).apply();

            Log.i(TAG, "Le score réalisé : " + score);
            // Log.e(String, String) (error)
            // Log.w(String, String) (warning)
            // Log.i(String, String) (information)
            // Log.d(String, String) (debug)
            // Log.v(String, String) (verbose)
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("MainActivity::onCreate()");

        mUser = new User();

        mPreferences = getPreferences(MODE_PRIVATE); // MODE_PRIVATE => seule notre application (activité) aura accès à ces préférences
        // On ne souhaite pas que d'autres applis sur le téléphone y accèdent.

        // pour référencer les éléments graphiques dans cette classe java
        // (permet de trouver la vue dans l'interface qui porte un identifiant donné)
        mGreetingText = findViewById(R.id.activity_main_greeting_txt);
        mNameInput = findViewById(R.id.activity_main_name_input);
        mPlayButton = findViewById(R.id.activity_main_play_btn);

        mPlayButton.setEnabled(false);

        /*
            Exercie :
            =========
            On vérifie ici si une partie est déjà jouée auparavant avec ce même utilisateur,
            et si c'est le cas alors saluer l'utilisateur (exp. : bon retour !) et lui rappeler son score
        */
//        StringBuilder message = new StringBuilder("Bon retour ");
//        message.append(mPreferences.getString(PREF_KEY_FIRSTNAME, ""));
//        message.append(" !\n");
//        message.append("Votre dernier score était de ");
//        message.append(mPreferences.getInt(PREF_KEY_SCORE, -1));
//        message.append(", feriez-vous mieux cette fois-ci ?");
//        mGreetingText.setText(message.toString());
        //Log.i(TAG, message.toString());

        mNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPlayButton.setEnabled(charSequence.toString().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUser.setFirstname(mNameInput.getText().toString());

                // Sauvegarder le prénom de l'utilisateur dans les préférences
                mPreferences.edit().putString(PREF_KEY_FIRSTNAME, mUser.getFirstname()).apply();

                // On va demander à Android de démarrer lui-même une  activité pour nous qu'on souhaite démarrer :
                // il s'agit ici de démarrer l'activité GameActivity
                Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);

                // Permet de démarrer l'activité sans récupérer le score depuis la classe GameActivity
                //startActivity(gameActivityIntent);

                // Permet de démarrer l'activité et récupérer le score depuis la classe GameActivity
                // Je crée l'activité de jeu et on lui associe un identifiant 22, comme ça quand elle me renverra le résultat
                //   je vérifie bien que c'est l'activité 22 qui me renvoie le résultat et que je pourrais bien le stocker
                startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("MainActivity::onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("MainActivity::onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MainActivity::onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("MainActivity::onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("MainActivity::onDestroy()");
    }
}