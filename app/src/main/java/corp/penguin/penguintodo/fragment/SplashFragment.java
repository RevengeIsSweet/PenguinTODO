package corp.penguin.penguintodo.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.TimeUnit;

import corp.penguin.penguintodo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SplashTask splashTask = new SplashTask();
        splashTask.execute();

        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    class SplashTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }

            return null;
        }
    }

}