package fr.doranco.myquiz.model;

import java.util.Collections;
import java.util.List;


public class                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      QuestionBank {

    private List<Question> mQuestionList;
    private int mNextQuestionIndex;

    public QuestionBank(List<Question> questionList) {
        mQuestionList = questionList;

        // mélanger la liste des questions (=> shuffle)
        Collections.shuffle(mQuestionList);

        mNextQuestionIndex = 0;
    }

    public Question getQuestion() {
        // s'assurer d'abord qu'on boucle sur la liste des questions au cas où on atteint la fin de liste
        if (mNextQuestionIndex == mQuestionList.size()) {
            mNextQuestionIndex = 0;
        }
        // renvoyer la question courante puis incrémenter l'index
        return mQuestionList.get(mNextQuestionIndex++);
    }
}
