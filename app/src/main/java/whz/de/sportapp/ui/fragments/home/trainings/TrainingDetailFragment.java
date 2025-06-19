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

import java.util.List;

import whz.de.sportapp.R;
import whz.de.sportapp.db.model.Exercise;
import whz.de.sportapp.db.model.Training;
import whz.de.sportapp.db.repository.SportAppRepository;
import whz.de.sportapp.ui.fragments.home.trainings.TrainingSessionFragment;

public class TrainingDetailFragment extends Fragment {

    private TextView tvTrainingTitle;
    private TextView tvTrainingDays;
    private LinearLayout exercisesContainer;
    private Button btnCancel;
    private Button btnStart;

    private SportAppRepository repository;
    private Training currentTraining;
    private List<Exercise> exercisesList;
    private int trainingId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_detail, container, false);

        // Get training ID from arguments
        if (getArguments() != null) {
            trainingId = getArguments().getInt("training_id", -1);
        }

        repository = new SportAppRepository(getActivity().getApplication());

        initViews(view);
        setupClickListeners();
        loadTrainingData();

        return view;
    }

    private void initViews(View view) {
        tvTrainingTitle = view.findViewById(R.id.tv_training_title);
        tvTrainingDays = view.findViewById(R.id.tv_training_days);
        exercisesContainer = view.findViewById(R.id.exercises_container);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnStart = view.findViewById(R.id.btn_start);
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTraining();
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
                    updateTrainingUI(training);
                }
            }
        });

        // Load exercises for this training
        repository.getExercisesForTraining(trainingId).observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                exercisesList = exercises;
                updateExercisesUI(exercises);
            }
        });
    }

    private void updateTrainingUI(Training training) {
        tvTrainingTitle.setText(training.name);
        tvTrainingDays.setText(training.days != null ? training.days : "Not specified");
    }

    private void updateExercisesUI(List<Exercise> exercises) {
        exercisesContainer.removeAllViews();

        if (exercises == null || exercises.isEmpty()) {
            TextView noExercisesText = new TextView(getActivity());
            noExercisesText.setText("No exercises in this training");
            noExercisesText.setTextSize(14);
            noExercisesText.setPadding(16, 16, 16, 16);
            exercisesContainer.addView(noExercisesText);

            btnStart.setEnabled(false);
            btnStart.setText("No exercises to start");
            return;
        }

        for (int i = 0; i < exercises.size(); i++) {
            Exercise exercise = exercises.get(i);
            addExerciseDetailItem(exercise, i + 1);
        }

        btnStart.setEnabled(true);
        btnStart.setText("START");
    }

    private void addExerciseDetailItem(Exercise exercise, int exerciseNumber) {
        View exerciseView = getLayoutInflater().inflate(R.layout.item_exercise_detail, exercisesContainer, false);

        TextView tvExerciseNumber = exerciseView.findViewById(R.id.tv_exercise_number);
        TextView tvExerciseLastResult = exerciseView.findViewById(R.id.tv_exercise_last_result);

        tvExerciseNumber.setText(exerciseNumber + ") " + exercise.name);

        // Show target based on exercise unit
        String target = "";
        if ("REPS".equals(exercise.unit) && exercise.defaultReps > 0) {
            target = "Target: " + exercise.defaultReps + " reps";
        } else if ("SECONDS".equals(exercise.unit) && exercise.defaultDuration > 0) {
            target = "Target: " + exercise.defaultDuration + " seconds";
        } else {
            target = "No target set";
        }

        tvExerciseLastResult.setText("(" + target + ")");

        exercisesContainer.addView(exerciseView);
    }

    private void startTraining() {
        if (currentTraining == null || exercisesList == null || exercisesList.isEmpty()) {
            Toast.makeText(getActivity(), "Cannot start training: missing data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to training session
        TrainingSessionFragment sessionFragment = new TrainingSessionFragment();

        Bundle args = new Bundle();
        args.putInt("training_id", trainingId);
        sessionFragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, sessionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}