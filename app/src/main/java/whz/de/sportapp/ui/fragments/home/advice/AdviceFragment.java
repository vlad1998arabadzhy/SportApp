package whz.de.sportapp.ui.fragments.home.advice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Random;

import whz.de.sportapp.R;
import whz.de.sportapp.utils.SmallTips;

public class AdviceFragment extends Fragment {
    private Button first;
    private Button second;
    private  Random random;
    private TextView tipps;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_advice, container, false);
        random = new Random();



        initView(view);
        setTippsText();
        setupClicksListeners();
        return view;
    }

    private void initView(View view) {
        first = view.findViewById(R.id.btn_first_advice);
        second = view.findViewById(R.id.btn_second_advice);
        tipps = view.findViewById(R.id.small_tips);
    }

    private void setupClicksListeners() {
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to warm-up  advice fragment
                navigateToFragment(new WarmingUpAdviceFragment());
            }
        });

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to nutrition advice fragment
                navigateToFragment(new RationAdviceFragment());
            }
        });
    }

    private void navigateToFragment(Fragment fragment) {

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);

        transaction.commit();
    }

    private void setTippsText(){
        int tippNum = random.nextInt(5);
        tipps.setText(SmallTips.TIPS[tippNum]);
    }
}