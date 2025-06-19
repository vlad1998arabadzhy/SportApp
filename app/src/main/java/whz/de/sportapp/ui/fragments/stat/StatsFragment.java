package whz.de.sportapp.ui.fragments.stat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import whz.de.sportapp.R;
import whz.de.sportapp.db.model.TrainingSession;
import whz.de.sportapp.db.repository.SportAppRepository;

public class StatsFragment extends Fragment {

    private TextView tvTotalTrainings;
    private TextView tvTotalMet;
    private LinearLayout completedTrainingsContainer;


    private SportAppRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        repository = new SportAppRepository(getActivity().getApplication());

        initViews(view);
        loadStatistics();

        return view;
    }

    private void initViews(View view) {
        tvTotalTrainings = view.findViewById(R.id.tv_total_trainings);
        completedTrainingsContainer = view.findViewById(R.id.completed_trainings_container);
        tvTotalMet=view.findViewById(R.id.tv_stat_amount_met);
    }

    private void loadStatistics() {

        // Load total completed trainings count
        repository.getCompletedSessionsCount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                if (count != null) {
                    tvTotalTrainings.setText(String.valueOf(count));
                } else {
                    tvTotalTrainings.setText("0");
                }
            }
        });

        repository.calculatePointsOfAllTrainingsForLastWeek().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer amount) {
                if(amount!=null){
                    tvTotalMet.setText(String.valueOf(amount));

                }else{
                    tvTotalMet.setText("0");
                }
            }
        });

        // Load all completed training sessions
        repository.getAllSessions().observe(getViewLifecycleOwner(), new Observer<List<TrainingSession>>() {
            @Override
            public void onChanged(List<TrainingSession> sessions) {
                updateCompletedTrainingsList(sessions);
            }
        });
    }

    private void updateCompletedTrainingsList(List<TrainingSession> sessions) {
        completedTrainingsContainer.removeAllViews();

        if (sessions == null || sessions.isEmpty()) {
            TextView noTrainingsText = new TextView(getActivity());
            noTrainingsText.setText("No completed trainings yet");
            noTrainingsText.setTextSize(14);
            noTrainingsText.setPadding(16, 16, 16, 16);
            noTrainingsText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            completedTrainingsContainer.addView(noTrainingsText);
            return;
        }

        // Show only completed sessions
        for (TrainingSession session : sessions) {
            if (session.isCompleted) {
                addCompletedTrainingItem(session);
            }
        }
    }

    private void addCompletedTrainingItem(TrainingSession session) {
        View trainingView = getLayoutInflater().inflate(R.layout.item_training_complited, completedTrainingsContainer, false);

        TextView tvTrainingName = trainingView.findViewById(R.id.tv_training_name);
        TextView tvTrainingDate = trainingView.findViewById(R.id.tv_training_date);
        TextView tvExercisesSummary = trainingView.findViewById(R.id.tv_exercises_summary);

        // Set training name
        tvTrainingName.setText(session.trainingName);

        // Set date and duration
        String dateText = formatSessionDate(session);
        tvTrainingDate.setText(dateText);

        // Set exercises summary
        String exercisesSummary = parseExercisesData(session.exercisesData);
        tvExercisesSummary.setText(exercisesSummary);

        // Set click listener to show details
        trainingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrainingDetails(session);
            }
        });

        completedTrainingsContainer.addView(trainingView);
    }

    private String formatSessionDate(TrainingSession session) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String date = dateFormat.format(new Date(session.date));

        // Calculate duration if both start and end times are available
        if (session.startTime != null && session.endTime != null) {
            long durationMs = session.endTime - session.startTime;

            return date + " • ";
        } else {
            return date;
        }
    }

    private String parseExercisesData(String exercisesData) {
        if (exercisesData == null || exercisesData.trim().isEmpty()) {
            return "No exercise data";
        }

        try {
            JSONObject json = new JSONObject(exercisesData);
            StringBuilder summary = new StringBuilder();
            Iterator<String> keys = json.keys();

            while (keys.hasNext()) {
                String exerciseName = keys.next();
                int result = json.getInt(exerciseName);

                if (summary.length() > 0) {
                    summary.append(", ");
                }
                summary.append(exerciseName).append(": ").append(result);
            }

            return summary.toString();
        } catch (JSONException e) {
            return "Invalid exercise data";
        }
    }

    private void showTrainingDetails(TrainingSession session) {
        // Create detailed information
        StringBuilder details = new StringBuilder();
        details.append("Training: ").append(session.trainingName).append("\n\n");

        SimpleDateFormat fullDateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
        details.append("Date: ").append(fullDateFormat.format(new Date(session.date))).append("\n");

        if (session.startTime != null && session.endTime != null) {
            long durationMs = session.endTime - session.startTime;
            long durationMinutes = durationMs / (1000 * 60);
            details.append("Duration: ").append(durationMinutes).append(" minutes\n");
        }

        details.append("\nExercise Results:\n");

        // Parse and show detailed exercise results
        if (session.exercisesData != null && !session.exercisesData.trim().isEmpty()) {
            try {
                JSONObject json = new JSONObject(session.exercisesData);
                Iterator<String> keys = json.keys();

                while (keys.hasNext()) {
                    String exerciseName = keys.next();
                    int result = json.getInt(exerciseName);
                    details.append("• ").append(exerciseName).append(": ").append(result).append("\n");
                }
            } catch (JSONException e) {
                details.append("Invalid exercise data");
            }
        } else {
            details.append("No exercise data available");
        }

        // Show details in a Toast (you can replace this with a dialog or new fragment)
        Toast.makeText(getActivity(), details.toString(), Toast.LENGTH_LONG).show();
    }
}