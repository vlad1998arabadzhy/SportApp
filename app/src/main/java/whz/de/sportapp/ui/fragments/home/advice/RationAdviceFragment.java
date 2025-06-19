package whz.de.sportapp.ui.fragments.home.advice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import whz.de.sportapp.R;

public class RationAdviceFragment extends Fragment {
    private Button back;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_ration_advice, container,false);
        initView(view);
        setupClicksListeners();

        return view;
    }

    private void  initView(View view){
        back = view.findViewById(R.id.btn_ration_advice_back);
    }

    private void setupClicksListeners(){
        back.setOnClickListener( v ->{
            navigateBack();
        });
    }

    private void navigateBack(){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, new AdviceFragment());

        transaction.commit();

    }



}
