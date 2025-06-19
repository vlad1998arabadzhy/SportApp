package whz.de.sportapp.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import whz.de.sportapp.db.model.Exercise;
import whz.de.sportapp.db.model.Training;

public class TrainingManager {

    private Training currentTraining;
    private List<Exercise> exercises;
    private Map<Integer, Integer> exerciseResults; // exercise index -> count

    public TrainingManager() {
        exerciseResults = new HashMap<>();
    }

    public void startTraining(Training training, List<Exercise> exerciseList) {
        this.currentTraining = training;
        this.exercises = exerciseList;
        this.exerciseResults.clear();

        // Initialize all exercises with 0
        for (int i = 0; i < exercises.size(); i++) {
            exerciseResults.put(i, 0);
        }
    }

    public void updateExerciseResult(int exerciseIndex, int count) {
        if (exerciseIndex >= 0 && exerciseIndex < exercises.size()) {
            exerciseResults.put(exerciseIndex, Math.max(0, count));
        }
    }

    public int getExerciseResult(int exerciseIndex) {
        return exerciseResults.getOrDefault(exerciseIndex, 0);
    }

    public String getResultsAsJson() {
        StringBuilder json = new StringBuilder("{");
        for (int i = 0; i < exercises.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(exercises.get(i).name).append("\":")
                    .append(exerciseResults.getOrDefault(i, 0));
        }
        json.append("}");
        return json.toString();
    }

    public Training getCurrentTraining() {
        return currentTraining;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }
}