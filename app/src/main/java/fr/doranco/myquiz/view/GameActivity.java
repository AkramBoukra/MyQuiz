package fr.doranco.myquiz.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import fr.doranco.myquiz.R;
import fr.doranco.myquiz.model.Question;
import fr.doranco.myquiz.model.QuestionBank;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mQuestionTextView;
    private Button mAnswer1Btn;
    private Button mAnswer2Btn;
    private Button mAnswer3Btn;
    private Button mAnswer4Btn;

    private QuestionBank mQuestionBank;
    private Question mCurrentQuestion;

    private int mScore;
    private int mMaxNumberOfQuestions;
    private int mNumberOfQuestions;

    // sert à mémoriser le score qui est envoyé à l'activité principale MainActivity lorsque cette GameActivity est détruite
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";

    // permettent de mémoriser le score et la question courante lorsqu'on effectue une rotation du téléphone
    // (Rotation qui a pour conséquence : Android détruit l'activité courante)
    public static final String BUNDLE_STATE_SCORE = "currentScore";
    public static final String BUNDLE_STATE_QUESTION = "currentQuestion";

    boolean mEnableTouchEvents; // sert à activer ou pas les différents évènements de Touch c'est à dire
    // le fait que l'utilisateur touche l'écran.

    // Un léger problème technique avec l'API 26 : Problème de rotation et de destruction d'activité
    // lorsqu'on est sur l'écran des questions du quiz et
    // on tourne le téléphone alors la question change, ceci n'est pas normal.
    // En effet, lorsque l'équipement tourne alors Android va détruire l'activité courante puis
    // il va la recréer, du coup il va relancer les questions et tombe sur une question quelconque.
    // On doit donc résoudre ce prblème.
    // Pour ce faire, on doit d'abord sauvegarder les infos importantes pour nous
    // (mémoriser l'état dans lequel se trouvait l'activité avant sa destruction) à savoir ici
    // le score de l'utilisateur et le nombre de questions qui lui ont été déjà posées.
    // On aura besoin donc de surcharger la méthode d'Android suivante
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // On va déposer dans le outState les infos à sauvegarder car Android utilisera ce paramètre
        // pour récupérer l'état d'avant la destruction de l'activité lorsque de nouveau l'activité
        // concernée est créée (ici c'est GameActivity)
//        outState.putInt(BUNDLE_STATE_SCORE, mScore);
//        outState.putInt(BUNDLE_STATE_QUESTION, mNumberOfQuestions);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        System.out.println("GameActivity::onCreate()");

        mQuestionBank = this.generateQuestions();

        // (suite du code réalisé dans la méthode : onSaveInstanceState(...)
//        if (savedInstanceState != null) {
        // si l' "état d'avant" de l'activité à créer existe alors on récupère les données
        // mais avec cette solution on peut retomber sur la même question
//            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
//            mNumberOfQuestions = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);
//        } else {
        mScore = 0;
        mNumberOfQuestions = mMaxNumberOfQuestions = 4;
//        }

        mEnableTouchEvents = true;

        // wire widgets
        mQuestionTextView = findViewById(R.id.activity_game_question_text);
        mAnswer1Btn = findViewById(R.id.activity_game_answer1_btn);
        mAnswer2Btn = findViewById(R.id.activity_game_answer2_btn);
        mAnswer3Btn = findViewById(R.id.activity_game_answer3_btn);
        mAnswer4Btn = findViewById(R.id.activity_game_answer4_btn);

        // Use the tag property to 'name' the buttons
        mAnswer1Btn.setTag(0);
        mAnswer2Btn.setTag(1);
        mAnswer3Btn.setTag(2);
        mAnswer4Btn.setTag(3);

        mAnswer1Btn.setOnClickListener(this);
        mAnswer2Btn.setOnClickListener(this);
        mAnswer3Btn.setOnClickListener(this);
        mAnswer4Btn.setOnClickListener(this);

        mCurrentQuestion = mQuestionBank.getQuestion();
        this.displayQuestion(mCurrentQuestion);
    }

    // click droit souris -> Generate... -> override methods -- dispatchTouchEvent
    // Rôle de cette méthode : à chaque fois que l'utilisateur touche l'écran, Android va exécuter cette méthode
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        int responseIndex = (int) view.getTag();
        if (responseIndex == mCurrentQuestion.getAnswerIndex()) {
            // Notifier alors l'utilisateur qu'il a bien répondu :
            Toast.makeText(this, "Bonne réponse", Toast.LENGTH_SHORT)
                    .show();
            mScore++;
        } else {
            // Notifier alors l'utilisateur qu'il n'a pas bien répondu :
            Toast.makeText(this, "Mauvaise réponse", Toast.LENGTH_SHORT)
                    .show();


        }
        try {
            Thread.sleep(2000); //pause de 1000ms (1s)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mEnableTouchEvents = false;

        // On souhaite laisser le temps à l'utilisateur de voir la notification s'il a bien répondu ou non
        //   avant d'aller à la question suivante sinon il n'aura pas le temps de voir la notification qui disparaît vite.
        // Pour cela, on va attendre un certain temps avant d'effectuer quelque chose
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEnableTouchEvents = true;
                // Si c'est la dernière question alors arrêter le Quiz,
                // Sinon, afficher la prochaine question.
                if (--mNumberOfQuestions == 0) {
                    // fin du quiz
                    endQuiz();
                } else {
                    mCurrentQuestion = mQuestionBank.getQuestion();
                    displayQuestion(mCurrentQuestion);
                }

            }
        }, 2000);  // LENGTH_SHORT dure généralement 2 secondes
/*
        if (--mNumberOfQuestions == 0) {
            // fin du quiz
            endQuiz();
        } else {
            mCurrentQuestion = mQuestionBank.getQuestion();
            this.displayQuestion(mCurrentQuestion);
        }
 */
    }

    private void endQuiz() {
        // on affiche une boite de dialogue pour indiquer le score et la fin du Quiz
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz bien effectué")
                .setMessage("Votre score est : " + mScore + "/" + mMaxNumberOfQuestions)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Fin de l'activité

                        // Demander au système Android de communiquer à l'activité appelante le score qu'on souhaite
                        Intent intent = new Intent();
                        intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void displayQuestion(final Question question) {
        mQuestionTextView.setText(question.getQuestion());
        mAnswer1Btn.setText(question.getChoiceList().get(0));
        mAnswer2Btn.setText(question.getChoiceList().get(1));
        mAnswer3Btn.setText(question.getChoiceList().get(2));
        mAnswer4Btn.setText(question.getChoiceList().get(3));
    }

    private QuestionBank generateQuestions() {
        Question question1 = new Question("Quel est le pays le plus peuplé du monde ?",
                Arrays.asList("USA", "Chine", "Inde", "Indonésie"), 1);

        Question question2 = new Question("Combien existe-t-il de pays dans l'Union Européenne ?",
                Arrays.asList("15", "24", "27", "32"), 2);

        Question question3 = new Question("Quel est le créateur du système d'exploitation Android ?",
                Arrays.asList("Jake Wharton", "Steve Wozmiak", "Paul Smith", "Andy Rubbin"), 3);

        Question question4 = new Question("Quel est le premier Président de la quatrième République ?",
                Arrays.asList("Vincent AURIOL", "René COTY", "Albert LEBRUN", "Paul Doumer"), 0);

        Question question5 = new Question("Quelle est la plus petite république du monde en nombre d'habitants ?",
                Arrays.asList("Les Tuvalu", "Nauru", "Monaco", "Les Palaos"), 1);

        Question question6 = new Question("Quelle est la langue la moins parlée au monde ?",
                Arrays.asList("L'artchi", "Le silbo", "rotokas", "Le piraha"), 0);

        return new QuestionBank(Arrays.asList(question1, question2, question3, question4, question5, question6));
    }

    // permet de voir le cycle de vie d'une activité, c'est à dire quelles sont les méthodes appelées dans l'ordre :


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("GameActivity::onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("GameActivity::onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("GameActivity::onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("GameActivity::onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("GameActivity::onDestroy()");
    }
}