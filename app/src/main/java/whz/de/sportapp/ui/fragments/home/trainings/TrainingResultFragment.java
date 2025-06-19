package whz.de.sportapp.ui.fragments.home.trainings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import whz.de.sportapp.R;
import whz.de.sportapp.ui.fragments.home.HomeFragment;

public class TrainingResultFragment extends Fragment {

    private TextView tvTrainingTitle;
    private LinearLayout resultsContainer;
    private Button btnHome;

    private String trainingName;
    private String resultsData;
    private int trainingId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_result, container, false);

        // Get data from arguments
        if (getArguments() != null) {
            trainingId = getArguments().getInt("training_id", -1);
            trainingName = getArguments().getString("training_name", "Training");
            resultsData = getArguments().getString("results_data", "{}");
        }

        initViews(view);
        setupClickListeners();
        displayResults();

        return view;
    }

    private void initViews(View view) {
        tvTrainingTitle = view.findViewById(R.id.tv_training_title);
        resultsContainer = view.findViewById(R.id.results_container);
        btnHome = view.findViewById(R.id.btn_home);
    }

    private void setupClickListeners() {
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHome();
            }
        });
    }

    private void displayResults() {
        tvTrainingTitle.setText(trainingName);

        // Parse results JSON and display
        List<ExerciseResult> results = parseResultsData(resultsData);

        if (results.isEmpty()) {
            TextView noResultsText = new TextView(getActivity());
            noResultsText.setText("No results recorded");
            noResultsText.setTextSize(16);
            noResultsText.setPadding(16, 16, 16, 16);
            resultsContainer.addView(noResultsText);
            return;
        }

        for (ExerciseResult result : results) {
            addResultItem(result);
        }
    }

    private List<ExerciseResult> parseResultsData(String jsonData) {
        List<ExerciseResult> results = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(jsonData);
            Iterator<String> keys = json.keys();

            while (keys.hasNext()) {
                String exerciseName = keys.next();
                int result = json.getInt(exerciseName);
                results.add(new ExerciseResult(exerciseName, result));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }

    private void addResultItem(ExerciseResult result) {
        View resultView = getLayoutInflater().inflate(R.layout.item_exercise_result, resultsContainer, false);

        TextView tvExerciseName = resultView.findViewById(R.id.tv_exercise_name);
        TextView tvExerciseResult = resultView.findViewById(R.id.tv_exercise_result);
        TextView tvExerciseProgress = resultView.findViewById(R.id.tv_exercise_progress);

        tvExerciseName.setText(result.exerciseName);
        tvExerciseResult.setText(result.resultValue + " completed");

        // Calculate progress (simplified - just show if more than 0)
        String progressText = "";
        if (result.resultValue > 0) {
            progressText = "(Well done!)";
        } else {
            progressText = "(Try harder next time)";
        }
        tvExerciseProgress.setText(progressText);

        resultsContainer.addView(resultView);
    }

    private void navigateToHome() {
        // Clear back stack and go to home
        getParentFragmentManager().popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);

        // Navigate to home fragment
        HomeFragment homeFragment = new HomeFragment();

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.commit();
    }

    // Helper class for exercise results
    private static class ExerciseResult {
        String exerciseName;
        int resultValue;

        ExerciseResult(String name, int value) {
            this.exerciseName = name;
            this.resultValue = value;
        }
    }
}