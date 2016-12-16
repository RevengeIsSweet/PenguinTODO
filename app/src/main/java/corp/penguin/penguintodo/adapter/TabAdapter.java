package corp.penguin.penguintodo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import corp.penguin.penguintodo.fragment.CurrentTaskFragment;
import corp.penguin.penguintodo.fragment.DoneTaskFragment;

/**
 * Created by vladislav on 23.02.16.
 */
public class TabAdapter extends FragmentStatePagerAdapter {

    private int numbOfTabs;

    public static final int CURRENT_TASK_FRAGMENT_POS = 0;
    public static final int DONE_TASK_FRAGMENT_POS = 1;

    private CurrentTaskFragment currentTasksFragment;
    private DoneTaskFragment doneTaskFragment;

    public TabAdapter(FragmentManager fm, int numbOfTabs) {
        super(fm);
        this.numbOfTabs = numbOfTabs;
        currentTasksFragment = new CurrentTaskFragment();
        doneTaskFragment = new DoneTaskFragment();

    }

    @Override
    public Fragment getItem(int pos) {

        switch (pos) {
            case 0:
                return currentTasksFragment;
            case 1:
                return doneTaskFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numbOfTabs;
    }
}
