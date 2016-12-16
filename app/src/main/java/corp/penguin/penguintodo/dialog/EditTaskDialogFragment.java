package corp.penguin.penguintodo.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;

import corp.penguin.penguintodo.Alarm.AlarmHelper;
import corp.penguin.penguintodo.CalendarHelper;
import corp.penguin.penguintodo.R;
import corp.penguin.penguintodo.model.ModelTask;

/**
 * Created by vladislav on 02.03.16.
 */
public class EditTaskDialogFragment extends DialogFragment {

    public static EditTaskDialogFragment newInstance(ModelTask task) {
        EditTaskDialogFragment editTaskDialogFragment = new EditTaskDialogFragment();

        Bundle arguments = new Bundle();
        arguments.putString("title", task.getTitle());
        arguments.putLong("date", task.getDate());
        arguments.putInt("priority", task.getPriority());
        arguments.putLong("time_stamp", task.getTimeStamp());

        editTaskDialogFragment.setArguments(arguments);
        return editTaskDialogFragment;
    }

    private EditingTaskListener editingTaskListener;

    public interface EditingTaskListener {
        void onTaskEdited(ModelTask updatedTask);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            editingTaskListener = (EditingTaskListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement EditingTaskListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String title = args.getString("title");
        long date = args.getLong("date", 0);
        int priority = args.getInt("priority", 0);
        long timeStamp = args.getLong("time_stamp", 0);

        final ModelTask task = new ModelTask(title, date, priority, 0, timeStamp);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_editing_title);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View container = inflater.inflate(R.layout.dialog_task, null);

        final TextInputLayout tilTitle = (TextInputLayout) container.findViewById(R.id.dialogTaskTitle);
        final EditText etTitle = tilTitle.getEditText();

        TextInputLayout tilDate = (TextInputLayout) container.findViewById(R.id.dialogTaskDate);
        final EditText etDate = tilDate.getEditText();

        TextInputLayout tilTime = (TextInputLayout) container.findViewById(R.id.dialogTaskTime);
        final EditText etTime = tilTime.getEditText();

        final Spinner spPriority = (Spinner) container.findViewById(R.id.spDialogTaskPrior);


        etTitle.setText(task.getTitle());
        etTitle.setSelection(etTitle.length());
        if (task.getDate() != 0) {
            etDate.setText(CalendarHelper.getDate(task.getDate()));
            etTime.setText(CalendarHelper.getTime(task.getDate()));
            etDate.setSelection(etDate.length());
            etTime.setSelection(etTime.length());
        }

        tilTitle.setHint(getResources().getString(R.string.task_title));
        tilDate.setHint(getResources().getString(R.string.task_date));
        tilTime.setHint(getResources().getString(R.string.task_time));

        builder.setView(container);


        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, ModelTask.PRIORITY_LEVELS);

        spPriority.setAdapter(priorityAdapter);

        spPriority.setSelection(task.getPriority());

        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == ModelTask.PRIORITY_LOW)
                    spPriority.setBackgroundColor(getResources().getColor(R.color.prior_low));

                if (position == ModelTask.PRIORITY_NORMAL)
                    spPriority.setBackgroundColor(getResources().getColor(R.color.prior_normal));

                if (position == ModelTask.PRIORITY_HIGH)
                    spPriority.setBackgroundColor(getResources().getColor(R.color.prior_high));

                task.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
        if (etDate.length() != 0 || etTime.length() != 0) {
            calendar.setTimeInMillis(task.getDate());
        }

            if (etDate != null) {
                etDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etDate.length() == 0) {
                            etDate.setText(" ");
                        }
                        DialogFragment datePickerFragment = new DatePickerFragment() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                etDate.setText(CalendarHelper.getDate(calendar.getTimeInMillis()));
                            }

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                etDate.setText(null);
                            }
                        };
                        datePickerFragment.show(getFragmentManager(), "DatePickerFragment");

                    }
                });
            }

        if (etTime != null) {
            etTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etTime.length() == 0) {
                        etTime.setText(" ");
                    }

                    DialogFragment timePickerFragment = new TimePickerFragment() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 0);
                            etTime.setText(CalendarHelper.getTime(calendar.getTimeInMillis()));
                        }

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            etTime.setText(null);
                        }
                    };
                    timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
                }
            });
        }

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.setTitle(etTitle.getText().toString());
                if (etDate.length() != 0 || etTime.length() != 0) {
                    task.setDate(calendar.getTimeInMillis());

                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setAlarm(task);
                }
                task.setStatus(ModelTask.STATUS_CURRENT);
                editingTaskListener.onTaskEdited(task);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                if (etTitle.length() == 0) {
                    positiveButton.setEnabled(false);
                }

                etTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            positiveButton.setEnabled(false);
                        }
                        if (s.length() >= 33) {
                            positiveButton.setEnabled(false);
                            tilTitle.setError(getResources().getString(R.string.dialog_too_long_title));
                        }
                        if (s.length() < 33 && s.length() != 0) {
                            positiveButton.setEnabled(true);
                            tilTitle.setErrorEnabled(false);
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            positiveButton.setEnabled(false);
                        }

                    }
                });
            }
        });

        return alertDialog;

    }
}
