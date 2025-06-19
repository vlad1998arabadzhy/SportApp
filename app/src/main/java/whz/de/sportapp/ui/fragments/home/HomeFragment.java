package whz.de.sportapp.ui.fragments.home;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import whz.de.sportapp.R;
import whz.de.sportapp.db.model.Goal;
import whz.de.sportapp.db.model.Training;
import whz.de.sportapp.db.model.User;
import whz.de.sportapp.db.repository.SportAppRepository;
import whz.de.sportapp.ui.fragments.home.advice.AdviceFragment;
import whz.de.sportapp.ui.fragments.home.trainings.CreateTrainingFragment;
import whz.de.sportapp.ui.fragments.home.trainings.TrainingDetailFragment;

public class HomeFragment extends Fragment {

    private Button btnSetNewGoal;
    private Button btnIDidIt;
    private Button btnCreateTraining;

    // Goal creation UI components
    private ImageButton imageBtnGetAdvice;
    private LinearLayout goalButtonsLayout;
    private LinearLayout goalSpinnersLayout;
    private Spinner spinnerGoalType;

    private Spinner spinnerGoalKilos; // New spinner from your layout
    private Spinner spinnerWeeks;
    private Button btnSaveGoal;
    private Button btnCancelGoal;
    private TextView tvGoalStatus;
    private TextView tvGoalDate;

    // Training components
    private LinearLayout trainingsContainer;

    private SportAppRepository repository;
    private User currentUser;
    private List<Training> trainingsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize repository
        repository = new SportAppRepository(getActivity().getApplication());
        trainingsList = new ArrayList<>();

        initViews(view);
        setupClickListeners();

        // Load data
        loadUser();
        loadTrainings();
        loadActiveGoal();

        return view;
    }

    private void initViews(View view) {
        btnSetNewGoal = view.findViewById(R.id.btn_set_new_goal);
        btnIDidIt = view.findViewById(R.id.btn_i_did_it);
        btnCreateTraining = view.findViewById(R.id.btn_create_training);

        // Goal creation components
        goalButtonsLayout = view.findViewById(R.id.goal_buttons_layout);
        goalSpinnersLayout = view.findViewById(R.id.goal_spinners_layout);
        spinnerGoalType = view.findViewById(R.id.spinner_goal_type);
        spinnerWeeks = view.findViewById(R.id.spinner_weeks);
        btnSaveGoal = view.findViewById(R.id.btn_save_goal);
        btnCancelGoal = view.findViewById(R.id.btn_cancel_goal);
        tvGoalStatus = view.findViewById(R.id.tv_goal_status);
        tvGoalDate = view.findViewById(R.id.tv_goal_date);
        imageBtnGetAdvice = view.findViewById(R.id.btn_get_advice);

        // Training components
        trainingsContainer = view.findViewById(R.id.trainings_container);


        try {
            spinnerGoalKilos = view.findViewById(R.id.spinner_goal_kilos);
        } catch (Exception e) {
            spinnerGoalKilos = null;
        }

        setupSpinners();
    }

    private void setupClickListeners() {
        if (imageBtnGetAdvice != null) {
            imageBtnGetAdvice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAdviceFragment();
                }
            });
        }

        btnSetNewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    showGoalSpinners();
                } else {
                    Toast.makeText(getActivity(), "Please create a user profile first in the Profile tab", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnIDidIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Goal completed! Great job!", Toast.LENGTH_SHORT).show();
                showQuickWeightUpdateDialog();
            }


        });

        btnCreateTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateTrainingFragment();
            }
        });

        btnSaveGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoal();
            }
        });

        btnCancelGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    private void setupSpinners() {
        // Setup goal type spinner
        String[] goalTypes = {"Lose weight", "Gain weight"};
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, goalTypes);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoalType.setAdapter(goalAdapter);



        // Setup goal kilos spinner (from your layout)
        if (spinnerGoalKilos != null) {
            String[] kilos = new String[50];
            for (int i = 0; i < 50; i++) {
                kilos[i] = (i + 1) + " kg";
            }
            ArrayAdapter<String> kilosAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_item, kilos);
            kilosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGoalKilos.setAdapter(kilosAdapter);
        }

        // Setup weeks spinner
        String[] weeks = new String[52]; // Up to 1 year
        for (int i = 0; i < 52; i++) {
            weeks[i] = (i + 1) + " weeks";
        }
        ArrayAdapter<String> weeksAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, weeks);
        weeksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeeks.setAdapter(weeksAdapter);
    }

    private void loadUser() {
        repository.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUser = user;
            }
        });
    }

    private void loadActiveGoal() {
        repository.getActiveGoal().observe(getViewLifecycleOwner(), new Observer<Goal>() {
            @Override
            public void onChanged(Goal goal) {
                updateGoalDisplay(goal);
            }
        });
    }

    private void loadTrainings() {
        // Observe trainings from database
        repository.getAllTrainings().observe(getViewLifecycleOwner(), new Observer<List<Training>>() {
            @Override
            public void onChanged(List<Training> trainings) {
                trainingsList.clear();
                if (trainings != null) {
                    trainingsList.addAll(trainings);
                }
                updateTrainingsDisplay();
            }
        });
    }

    private void updateTrainingsDisplay() {
        trainingsContainer.removeAllViews();

        if (trainingsList.isEmpty()) {
            // Show message when no trainings
            TextView noTrainingsText = new TextView(getActivity());
            noTrainingsText.setText("No trainings yet. Create your first training!");
            noTrainingsText.setTextSize(14);
            noTrainingsText.setPadding(16, 16, 16, 16);
            noTrainingsText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            trainingsContainer.addView(noTrainingsText);
        } else {
            // Add training items
            for (Training training : trainingsList) {
                addTrainingItem(training);
            }
        }
    }

    private void addTrainingItem(Training training) {
        View trainingView = getLayoutInflater().inflate(R.layout.item_training, trainingsContainer, false);

        TextView tvName = trainingView.findViewById(R.id.tv_training_name);
        TextView tvSchedule = trainingView.findViewById(R.id.tv_training_schedule);

        tvName.setText(training.name);
        tvSchedule.setText(training.days != null ? "(" + training.days + ")" : "");

        // Set click listener
        trainingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTrainingClicked(training);
            }
        });

        trainingsContainer.addView(trainingView);
    }

    private void onTrainingClicked(Training training) {
        // Navigate to training details
        openTrainingDetailFragment(training);
    }

    private void openCreateTrainingFragment() {
        CreateTrainingFragment createTrainingFragment = new CreateTrainingFragment();

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, createTrainingFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openAdviceFragment() {
        AdviceFragment adviceFragment = new AdviceFragment();

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, adviceFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openTrainingDetailFragment(Training training) {
        TrainingDetailFragment detailFragment = new TrainingDetailFragment();

        Bundle args = new Bundle();
        args.putInt("training_id", training.trainingId);
        detailFragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showGoalSpinners() {
        goalButtonsLayout.setVisibility(View.GONE);
        goalSpinnersLayout.setVisibility(View.VISIBLE);
    }

    private void hideGoalSpinners() {
        goalButtonsLayout.setVisibility(View.VISIBLE);
        goalSpinnersLayout.setVisibility(View.GONE);
    }

    private void saveGoal() {
        // Check if user exists before creating goal
        if (currentUser == null) {
            Toast.makeText(getActivity(), "Please create a user profile first in the Profile tab", Toast.LENGTH_LONG).show();
            hideGoalSpinners();
            return;
        }

        try {
            String goalType = spinnerGoalType.getSelectedItem().toString();
            String weeks = spinnerWeeks.getSelectedItem().toString();

            // Extract numbers
            int weeksNumber = Integer.parseInt(weeks.split(" ")[0]);
            int kilosNumber = 5; // Default amount

            // Get kilos from the spinner_goal_kilos (your layout)
            if (spinnerGoalKilos != null && spinnerGoalKilos.getSelectedItem() != null) {
                String kilos = spinnerGoalKilos.getSelectedItem().toString();
                kilosNumber = Integer.parseInt(kilos.split(" ")[0]);
            }


            // Calculate target weight based on current user weight
            double targetWeight = calculateTargetWeight(goalType, kilosNumber);
            String goalText = goalType + " " + kilosNumber + " kg in " + weeksNumber + " weeks";
             goalText =goalText +"\n" +"You have started on " +appendLocalDate();



            hideGoalSpinners();
            Toast.makeText(getActivity(), "Goal saved successfully!", Toast.LENGTH_SHORT).show();

            // Save goal to database with updated method
            createGoalWithKilos(goalText, goalType, targetWeight, weeksNumber, kilosNumber);

        } catch (Exception e) {
            android.util.Log.e("HomeFragment", "Error saving goal", e);
            Toast.makeText(getActivity(), "Error saving goal. Please try again.", Toast.LENGTH_SHORT).show();
            hideGoalSpinners();
        }
    }

    private void createGoalWithKilos(String goalText, String goalType, double targetWeight, int weeks, int kilos) {
        if (repository != null) {
            try {
                repository.createGoalWithKilos(goalText, goalType, targetWeight, weeks, kilos);
            } catch (Exception e) {
                android.util.Log.e("HomeFragment", "Repository error", e);
                Toast.makeText(getActivity(), "Database error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private double calculateTargetWeight(String goalType, int amountKg) {
        double currentWeight = 70.0; // Default weight
        if (currentUser != null && currentUser.weight > 0) {
            currentWeight = currentUser.weight;
        }

        if ("Lose weight".equals(goalType)) {
            return Math.max(currentWeight - amountKg,20); // Don't go below 30kg
        } else {
            return Math.min(currentWeight + amountKg, 200.0); // Don't go above 200kg
        }
    }

    private void updateGoalDisplay(Goal goal) {
        if (goal != null && goal.isActive && !goal.isCompleted) {
            // Show existing goal
            tvGoalStatus.setText(goal.goalText);
            btnIDidIt.setVisibility(View.VISIBLE);
            btnSetNewGoal.setText("Change goal");



            // Calculate and show progress
            if (goal.startWeight != 0 && goal.targetWeight != 0 && goal.startWeight != goal.targetWeight) {
                double progress = Math.abs(goal.currentWeight - goal.startWeight) /
                        Math.abs(goal.targetWeight - goal.startWeight) * 100;
                progress = Math.min(progress, 100); // Cap at 100%
                progress = Math.max(progress, 0);   // Don't go below 0%

                String progressText = showProgress(progress);

                tvGoalStatus.append("\n" + progressText);

                // Show current vs target weight
                String weightInfo = String.format("Current: %.1f kg → Target: %.1f kg",
                        goal.currentWeight, goal.targetWeight);
                tvGoalStatus.append("\n" + weightInfo);
            }
        } else {
            // No active goal
            tvGoalStatus.setText("You have not set a goal yet");
            btnSetNewGoal.setText("Set new goal");
            if (tvGoalDate != null) {
                tvGoalDate.setVisibility(View.GONE);
            }
            btnIDidIt.setVisibility(View.GONE);
        }
    }

    private String showProgress(double progress){
        String progressText = String.format("Progress: %.1f%%", progress);


        if (Math.abs(progress)>90){
            progressText=String.format("Progress: %.1f%%", progress)+" Final stage! Just keep going!";
        }else if (Math.abs(progress)>70) {
            progressText=String.format("Progress: %.1f%%", progress)+" You are about to achieve it. Do not give up!";
        }else if(Math.abs(progress)>50){
           progressText=String.format("Progress: %.1f%%", progress)+" You are half-way from success!";
       }else if(Math.abs(progress)>20){
            progressText=String.format("Progress: %.1f%%", progress)+" Nice beginning! Continue just like this";
        }else if(Math.abs(progress)==0){
            progressText=String.format("Progress: %.1f%%", progress)+"New goal - new journey. Be patient and hard-working!";
        }

        return progressText;
    }
    private String appendLocalDate(){

        LocalDate today       = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }
        LocalDateTime current = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            current = LocalDateTime.now();
        }





        DateTimeFormatter fmt = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fmt = DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.US);
        }
        String pretty = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pretty = today.format(fmt);
        }

        return pretty;
    }



    private void showQuickWeightUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        EditText etWeight = new EditText(getActivity());
        etWeight.setHint("Enter new weight (kg)");
        etWeight.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (currentUser != null) {
            etWeight.setText(String.valueOf(currentUser.weight));
        }

        builder.setView(etWeight)
                .setTitle("Update Weight")
                .setMessage("This will also create a weight history entry")
                .setPositiveButton("Update", (dialog, which) -> {
                    try {
                        double newWeight = Double.parseDouble(etWeight.getText().toString().trim());
                        if (newWeight > 0 && newWeight < 500) { // Basic validation

                            // Use background thread for database operation
                            new Thread(() -> {
                                try {
                                    repository.updateWeight(newWeight);

                                    // Show success message on main thread
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            Toast.makeText(getActivity(),
                                                    String.format("Weight updated to %.1f kg", newWeight),
                                                    Toast.LENGTH_SHORT).show();
                                            // Force refresh after weight update
                                            refreshUserData();
                                        });
                                    }
                                } catch (Exception e) {
                                    // Show error message on main thread
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            Toast.makeText(getActivity(), "Error updating weight", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }
                            }).start();

                        } else {
                            Toast.makeText(getActivity(), "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void refreshUserData() {
        new Thread(() -> {
            try {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        repository.getUser().removeObservers(getViewLifecycleOwner());

                    });
                }
            } catch (Exception e) {
                android.util.Log.e("ProfileFragment", "Error refreshing user data", e);
            }
        }).start();
    }



}