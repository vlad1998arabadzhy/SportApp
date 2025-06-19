package whz.de.sportapp.ui.fragments.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import whz.de.sportapp.R;
import whz.de.sportapp.db.model.User;
import whz.de.sportapp.db.repository.SportAppRepository;

public class ProfileFragment extends Fragment {

    private TextView tvUserName;
    private TextView tvAge;
    private TextView tvWeight;
    private TextView tvHeight;
    private TextView tvGender;
    private Button btnChangeInfo;


    private SportAppRepository repository;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        repository = new SportAppRepository(getActivity().getApplication());

        initViews(view);
        setupObservers();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvAge = view.findViewById(R.id.tv_age);
        tvWeight = view.findViewById(R.id.tv_weight);
        tvHeight = view.findViewById(R.id.tv_height);
        tvGender = view.findViewById(R.id.tv_gender);
        btnChangeInfo = view.findViewById(R.id.btn_change_info);

    }

    private void setupObservers() {
        // Observe user data changes from database
        repository.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                android.util.Log.d("ProfileFragment", "User changed: " + (user != null ? user.name : "null"));
                currentUser = user;
                if (user != null) {
                    updateUserDisplay(user);
                    btnChangeInfo.setText("Edit Profile");

                } else {
                    displayEmptyUserData();
                    btnChangeInfo.setText("Create Profile");

                }
            }
        });
    }

    private void setupClickListeners() {
        btnChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    showEditProfileDialog();
                } else {
                    showCreateProfileDialog();
                }
            }
        });


    }

    private void updateUserDisplay(User user) {
        tvUserName.setText(user.name != null ? user.name : "Unknown");
        tvAge.setText(user.age > 0 ? user.age + " years" : "Not set");
        tvWeight.setText(user.weight > 0 ? String.format("%.1f kg", user.weight) : "Not set");
        tvHeight.setText(user.height > 0 ? String.format("%.1f cm", user.height) : "Not set");
        tvGender.setText(user.gender != null ? user.gender : "Not set");
    }

    private void displayEmptyUserData() {
        tvUserName.setText("No user profile");
        tvAge.setText("Not set");
        tvWeight.setText("Not set");
        tvHeight.setText("Not set");
        tvGender.setText("Not set");
    }

    private void showCreateProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_profile, null);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etAge = dialogView.findViewById(R.id.et_age);
        EditText etWeight = dialogView.findViewById(R.id.et_weight);
        EditText etHeight = dialogView.findViewById(R.id.et_height);
        EditText etGender = dialogView.findViewById(R.id.et_gender);

        builder.setView(dialogView)
                .setTitle("Create Profile")
                .setPositiveButton("Create", (dialog, which) -> {
                    try {
                        String name = etName.getText().toString().trim();
                        int age = Integer.parseInt(etAge.getText().toString().trim());
                        double weight = Double.parseDouble(etWeight.getText().toString().trim());
                        double height = Double.parseDouble(etHeight.getText().toString().trim());
                        String gender = etGender.getText().toString().trim();

                        if (validateUserInput(name, age, weight, height, gender)) {
                            User newUser = new User(name, age, weight, height, gender);

                            // Use background thread for database operation
                            new Thread(() -> {
                                try {
                                    repository.updateUser(newUser);

                                    // Show success message on main thread
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            Toast.makeText(getActivity(), "Profile created successfully!", Toast.LENGTH_SHORT).show();
                                            // Force refresh after creation
                                            refreshUserData();
                                        });
                                    }
                                } catch (Exception e) {
                                    // Show error message on main thread
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            Toast.makeText(getActivity(), "Error creating profile", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }
                            }).start();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Please enter valid numbers for age, weight, and height", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_profile, null);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etAge = dialogView.findViewById(R.id.et_age);
        EditText etWeight = dialogView.findViewById(R.id.et_weight);
        EditText etHeight = dialogView.findViewById(R.id.et_height);
        EditText etGender = dialogView.findViewById(R.id.et_gender);

        // Pre-fill with current user data
        if (currentUser != null) {
            etName.setText(currentUser.name);
            etAge.setText(String.valueOf(currentUser.age));
            etWeight.setText(String.valueOf(currentUser.weight));
            etHeight.setText(String.valueOf(currentUser.height));
            etGender.setText(currentUser.gender);
        }

        builder.setView(dialogView)
                .setTitle("Edit Profile")
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        String name = etName.getText().toString().trim();
                        int age = Integer.parseInt(etAge.getText().toString().trim());
                        double weight = Double.parseDouble(etWeight.getText().toString().trim());
                        double height = Double.parseDouble(etHeight.getText().toString().trim());
                        String gender = etGender.getText().toString().trim();

                        if (validateUserInput(name, age, weight, height, gender)) {
                            // Create new user object with updated data
                            User updatedUser = new User(name, age, weight, height, gender);
                            updatedUser.uid = currentUser.uid; // Keep the same uid
                            updatedUser.createdAt = currentUser.createdAt; // Keep creation time

                            // Use background thread for database operation
                            new Thread(() -> {
                                try {
                                    repository.updateUser(updatedUser);

                                    // Show success message on main thread
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                            // Force UI update by manually updating the displayed data
                                            updateUserDisplay(updatedUser);
                                            currentUser = updatedUser;
                                        });
                                    }
                                } catch (Exception e) {
                                    // Show error message on main thread
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            Toast.makeText(getActivity(), "Error updating profile", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }
                            }).start();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Please enter valid numbers for age, weight, and height", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Quick Weight Update", (dialog, which) -> {
                    showQuickWeightUpdateDialog();
                })
                .create()
                .show();
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

    private boolean validateUserInput(String name, int age, double weight, double height, String gender) {
        if (name.isEmpty()) {
            Toast.makeText(getActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (age <= 0 || age > 150) {
            Toast.makeText(getActivity(), "Please enter a valid age (1-150)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (weight <= 0 || weight > 500) {
            Toast.makeText(getActivity(), "Please enter a valid weight (1-500 kg)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (height <= 0 || height > 300) {
            Toast.makeText(getActivity(), "Please enter a valid height (1-300 cm)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (gender.isEmpty()) {
            Toast.makeText(getActivity(), "Gender cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    // Separate methods for cleaner code
    private void createUser(EditText etName, EditText etAge, EditText etWeight, EditText etHeight, EditText etGender) {
        try {
            String name = etName.getText().toString().trim();
            int age = Integer.parseInt(etAge.getText().toString().trim());
            double weight = Double.parseDouble(etWeight.getText().toString().trim());
            double height = Double.parseDouble(etHeight.getText().toString().trim());
            String gender = etGender.getText().toString().trim();

            if (validateUserInput(name, age, weight, height, gender)) {
                User newUser = new User(name, age, weight, height, gender);

                new Thread(() -> {
                    try {
                        repository.updateUser(newUser);
                        showToast("Profile created successfully!");
                        refreshUserData();
                    } catch (Exception e) {
                        showToast("Error creating profile");
                    }
                }).start();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter valid numbers for age, weight, and height", Toast.LENGTH_SHORT).show();
        }
    }




    private void showToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Method to force refresh user data from database
    private void refreshUserData() {
        new Thread(() -> {
            try {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        repository.getUser().removeObservers(getViewLifecycleOwner());
                        setupObservers();
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("ProfileFragment", "Error refreshing user data", e);
            }
        }).start();
    }
}