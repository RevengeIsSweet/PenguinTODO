package corp.penguin.penguintodo.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import corp.penguin.penguintodo.DataBase.DBHelper;
import corp.penguin.penguintodo.R;
import corp.penguin.penguintodo.adapter.CurrentTasksAdapter;
import corp.penguin.penguintodo.model.ModelSeparator;
import corp.penguin.penguintodo.model.ModelTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentTaskFragment extends TaskFragment {

    public CurrentTaskFragment() {
        // Required empty public constructor
    }

    OnTaskDoneListener onTaskDoneListener;

    public interface OnTaskDoneListener {
        void onTaskDone(ModelTask task);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            Activity a = (Activity) context;
            try {
                onTaskDoneListener = (OnTaskDoneListener) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString() + "Must implement OnTaskDoneListener");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_current_task, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvCurrentTasks);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        adapter = new CurrentTasksAdapter(this);
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(
                DBHelper.SELECTION_LINE_TITLE + " AND "
                        + DBHelper.SELECTION_STATUS + " OR "
                        + DBHelper.SELECTION_STATUS,
                new String[]{"%" + title + "%", Integer.toString(ModelTask.STATUS_CURRENT),
                        Integer.toString(ModelTask.STATUS_OVERDUE)},
                DBHelper.TASK_DATE_COLUMN
        ));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }

    }

    @Override
    public void addTaskFromDB() {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(
                DBHelper.SELECTION_STATUS + " OR " + DBHelper.SELECTION_STATUS,
                new String[]{Integer.toString(ModelTask.STATUS_CURRENT),
                        Integer.toString(ModelTask.STATUS_OVERDUE)},
                DBHelper.TASK_DATE_COLUMN
        ));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDB) {
        int pos = -1;
        ModelSeparator separator = null;

        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isTask()) {

                ModelTask task = (ModelTask) adapter.getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    pos = i;
                    break;
                }
            }
        }

        if (newTask.getDate() != 0) {
            Calendar newTaskCalendar = Calendar.getInstance();
            newTaskCalendar.setTimeInMillis(newTask.getDate());

            if (newTaskCalendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_OVERDUE);
                if (!adapter.containSeparatorOverdue) {
                    adapter.containSeparatorOverdue = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_OVERDUE);
                }
            } else if (newTaskCalendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_TODAY);
                if (!adapter.containSeparatorToday) {
                    adapter.containSeparatorToday = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TODAY);
                }
            } else if (newTaskCalendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(ModelSeparator.TYPE_TOMORROW);
                if (!adapter.containSeparatorTomorrow) {
                    adapter.containSeparatorTomorrow = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TOMORROW);
                }
            } else if (newTaskCalendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(ModelSeparator.TYPE_FUTURE);
                if (!adapter.containSeparatorFuture) {
                    adapter.containSeparatorFuture = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_FUTURE);
                }
            }
        }

        if (pos != -1) {
            if (!adapter.getItem(pos - 1).isTask()) {
                if (pos - 2 >= 0 && adapter.getItem(pos - 2).isTask()) {
                    ModelTask task = (ModelTask) adapter.getItem(pos - 2);
                    if (task.getDateStatus() == newTask.getDateStatus()) {
                        pos -= 1;
                    }
                } else if (pos - 2 < 0 && newTask.getDate() == 0) {
                    pos -= 1;
                }
            }

            if (separator != null) {
                adapter.addItem(pos - 1, separator);
            }

            adapter.addItem(pos, newTask);

        } else {
            if (separator != null) {
                adapter.addItem(separator);
            }
            adapter.addItem(newTask);

        }

        if (saveToDB) {
            activity.dbHelper.saveTask(newTask);
        }

    }

    @Override
    public void moveTask(ModelTask task) {
        alarmHelper.removeAlarm(task.getTimeStamp());
        onTaskDoneListener.onTaskDone(task);

    }
}
