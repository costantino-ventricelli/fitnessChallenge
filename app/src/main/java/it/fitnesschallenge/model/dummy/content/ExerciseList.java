package it.fitnesschallenge.model.dummy.content;

import java.util.ArrayList;
import java.util.List;

import it.fitnesschallenge.model.Exercise;

// TODO: finire classe con creazione lista esercizi

public abstract class ExerciseList {

    private static List<Exercise> mList;

    private ExerciseList(){
        // necessario per sovrascrivere il metodo pubblico
    }

    private static void getContent(){
        mList = new ArrayList<>();

    }
}
