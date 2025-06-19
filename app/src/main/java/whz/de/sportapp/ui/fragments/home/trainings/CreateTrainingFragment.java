package whz.de.sportapp.ui.fragments.home.trainings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import whz.de.sportapp.R;
import whz.de.sportapp.db.model.Exercise;
import whz.de.sportapp.db.model.Training;
import whz.de.sportapp.db.repository.SportAppRepository;

public class CreateTrainingFragment extends Fragment {

    private EditText etTrainingName;
    private EditText etDescription;
    private EditText etDays;
    private EditText etGoal;
    private LinearLayout exercisesContainer;
    private Button btnAddExercise;
    private Button btnCancel;
    private Button btnSave;

    private SportAppRepository repository;
    private List<Exercise> exercisesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_create, container, false);

        // Initialize repository
        repository = new SportAppRepository(getActivity().getApplication());

        // Initialize exercises list
        exercisesList = new ArrayList<>();

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        etTrainingName = view.findViewById(R.id.et_training_name);
        etDescription = view.findViewById(R.id.et_description);
        etDays = view.findViewById(R.id.et_days);

        exercisesContainer = view.findViewById(R.id.exercises_container);
        btnAddExercise = view.findViewById(R.id.btn_add_exercise);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSave = view.findViewById(R.id.btn_save);
    }

    private void setupClickListeners() {
        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExerciseToList();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to previous fragment
                getParentFragmentManager().popBackStack();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTraining();
            }
        });
    }

    private void addExerciseToList() {
        // Create a simple exercise input view
        View exerciseView = getLayoutInflater().inflate(R.layout.item_exercise_input, exercisesContainer, false);

        EditText etExerciseName = exerciseView.findViewById(R.id.et_exercise_name);
        EditText etReps = exerciseView.findViewById(R.id.et_reps);
        EditText etDuration = exerciseView.findViewById(R.id.et_duration);
        Button btnRemove = exerciseView.findViewById(R.id.btn_remove_exercise);

        // Set remove button listener
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercisesContainer.removeView(exerciseView);
            }
        });

        exercisesContainer.addView(exerciseView);
    }

    private void saveTraining() {
        // Validate input
        String name = etTrainingName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String days = etDays.getText().toString().trim();


        if (name.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter training name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect exercises from the container
        List<Exercise> exercises = collectExercisesFromViews();

        if (exercises.isEmpty()) {
            Toast.makeText(getActivity(), "Please add at least one exercise", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create training object
        Training newTraining = new Training(name, description, days, true);


        // Save to database
        saveTrainingToDatabase(newTraining, exercises);
    }

    private List<Exercise> collectExercisesFromViews() {
        List<Exercise> exercises = new ArrayList<>();

        for (int i = 0; i < exercisesContainer.getChildCount(); i++) {
            View exerciseView = exercisesContainer.getChildAt(i);

            EditText etExerciseName = exerciseView.findViewById(R.id.et_exercise_name);
            EditText etReps = exerciseView.findViewById(R.id.et_reps);
            EditText etDuration = exerciseView.findViewById(R.id.et_duration);

            String exerciseName = etExerciseName.getText().toString().trim();
            if (!exerciseName.isEmpty()) {
                Exercise exercise = new Exercise();
                exercise.name = exerciseName;

                try {
                    exercise.defaultReps = Integer.parseInt(etReps.getText().toString().trim());
                } catch (NumberFormatException e) {
                    exercise.defaultReps = 0;
                }

                try {
                    exercise.defaultDuration = Integer.parseInt(etDuration.getText().toString().trim());
                } catch (NumberFormatException e) {
                    exercise.defaultDuration = 0;
                }

                exercise.unit = exercise.defaultReps > 0 ? "REPS" : "SECONDS";
                exercise.orderIndex = i;

                exercises.add(exercise);
            }
        }

        return exercises;
    }

    private void showTrainingCreated(Training training, List<Exercise> exercises) {
        String message = "Training Saved Successfully!\n" +
                "Name: " + training.name + "\n" +
                "Exercises: " + exercises.size() + "\n" +
                "Saved to database";

        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

        // Go back to previous fragment
        getParentFragmentManager().popBackStack();
    }

    // Database methods for saving training and exercises
    private void saveTrainingToDatabase(Training training, List<Exercise> exercises) {
        repository.createCustomTrainingWithExercises(
                training.name,
                training.description,
                training.days,
                exercises
        );

        // Show success message
        showTrainingCreated(training, exercises);
    }
}