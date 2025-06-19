package whz.de.sportapp.ui.fragments.home.trainings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import whz.de.sportapp.R;
import whz.de.sportapp.db.model.Exercise;
import whz.de.sportapp.db.model.Training;
import whz.de.sportapp.db.repository.SportAppRepository;

public class TrainingSessionFragment extends Fragment {

    private TextView tvTrainingTitle;
    private LinearLayout exercisesContainer;
    private Button btnCancel;
    private Button btnFinish;

    private SportAppRepository repository;
    private int trainingId;
    private Training currentTraining;
    private List<Exercise> exercisesList;

    // Exercise counters (index -> count)
    private Map<Integer, Integer> exerciseCounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_session, container, false);

        // Get training ID from arguments
        if (getArguments() != null) {
            trainingId = getArguments().getInt("training_id", -1);
        }

        repository = new SportAppRepository(getActivity().getApplication());
        exerciseCounts = new HashMap<>();

        initViews(view);
        setupClickListeners();
        loadTrainingData();

        return view;
    }

    private void initViews(View view) {
        tvTrainingTitle = view.findViewById(R.id.tv_training_title);
        exercisesContainer = view.findViewById(R.id.exercises_container);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnFinish = view.findViewById(R.id.btn_finish);
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTraining();
            }
        });
    }

    private void loadTrainingData() {
        if (trainingId == -1) {
            Toast.makeText(getActivity(), "Training not found", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return;
        }

        // Load training details
        repository.getTrainingById(trainingId).observe(getViewLifecycleOwner(), new Observer<Training>() {
            @Override
            public void onChanged(Training training) {
                if (training != null) {
                    currentTraining = training;
                    tvTrainingTitle.setText(training.name);
                    loadExercises();
                }
            }
        });
    }

    private void loadExercises() {
        repository.getExercisesForTraining(trainingId).observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                if (exercises != null && !exercises.isEmpty()) {
                    exercisesList = exercises;
                    createExerciseViews();
                } else {
                    Toast.makeText(getActivity(), "No exercises found", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                }
            }
        });
    }

    private void createExerciseViews() {
        exercisesContainer.removeAllViews();
        exerciseCounts.clear();

        for (int i = 0; i < exercisesList.size(); i++) {
            Exercise exercise = exercisesList.get(i);
            exerciseCounts.put(i, 0); // Initialize counter

            View exerciseView = createExerciseCounterView(exercise, i);
            exercisesContainer.addView(exerciseView);
        }
    }

    private View createExerciseCounterView(Exercise exercise, int exerciseIndex) {
        View exerciseView = getLayoutInflater().inflate(R.layout.item_exercise_counter, exercisesContainer, false);

        TextView tvExerciseName = exerciseView.findViewById(R.id.tv_exercise_name);
        TextView tvExerciseGoal = exerciseView.findViewById(R.id.tv_exercise_goal);
        TextView tvCounter = exerciseView.findViewById(R.id.tv_counter);
        Button btnMinus = exerciseView.findViewById(R.id.btn_minus);
        Button btnPlus = exerciseView.findViewById(R.id.btn_plus);

        tvExerciseName.setText(exercise.name);

        String goalText = "";
        if ("REPS".equals(exercise.unit) && exercise.defaultReps > 0) {
            goalText = "Goal: " + exercise.defaultReps + " reps";
        } else if ("SECONDS".equals(exercise.unit) && exercise.defaultDuration > 0) {
            goalText = "Goal: " + exercise.defaultDuration + " seconds";
        } else {
            goalText = "Goal: Not set";
        }
        tvExerciseGoal.setText(goalText);

        tvCounter.setText("00");

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentCount = exerciseCounts.get(exerciseIndex);
                currentCount++;
                exerciseCounts.put(exerciseIndex, currentCount);
                tvCounter.setText(String.format("%02d", currentCount));
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentCount = exerciseCounts.get(exerciseIndex);
                if (currentCount > 0) {
                    currentCount--;
                    exerciseCounts.put(exerciseIndex, currentCount);
                    tvCounter.setText(String.format("%02d", currentCount));
                }
            }
        });

        return exerciseView;
    }

    private void finishTraining() {
        // Collect results and create JSON string
        String exercisesData = createExercisesDataJson();

        // Save completed training session to database
        repository.saveCompletedTrainingSession(trainingId, currentTraining.name, exercisesData);

        // Navigate to results fragment to show the results
        TrainingResultFragment resultFragment = new TrainingResultFragment();

        Bundle args = new Bundle();
        args.putInt("training_id", trainingId);
        args.putString("training_name", currentTraining.name);
        args.putString("results_data", exercisesData);
        resultFragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, resultFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        Toast.makeText(getActivity(), "Training completed and saved!", Toast.LENGTH_SHORT).show();
    }

    private String createExercisesDataJson() {
        StringBuilder json = new StringBuilder("{");
        for (int i = 0; i < exercisesList.size(); i++) {
            if (i > 0) json.append(",");
            Exercise exercise = exercisesList.get(i);
            int count = exerciseCounts.get(i);
            json.append("\"").append(exercise.name).append("\":").append(count);
        }
        json.append("}");
        return json.toString();
    }
}