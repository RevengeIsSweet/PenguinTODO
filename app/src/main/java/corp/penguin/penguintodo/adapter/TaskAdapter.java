package corp.penguin.penguintodo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import corp.penguin.penguintodo.fragment.TaskFragment;
import corp.penguin.penguintodo.model.Item;
import corp.penguin.penguintodo.model.ModelSeparator;
import corp.penguin.penguintodo.model.ModelTask;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by vladislav on 27.02.16.
 */
public abstract class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Item> items;
    TaskFragment taskFragment;
    public boolean containSeparatorOverdue;
    public boolean containSeparatorToday;
    public boolean containSeparatorTomorrow;
    public boolean containSeparatorFuture;

    public TaskAdapter(TaskFragment taskFragment) {
        this.taskFragment = taskFragment;
        items = new ArrayList<>();
    }

    public Item getItem(int pos) {
        return items.get(pos);
    }

    public void addItem(Item item) {
        items.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addItem(int loc, Item item) {
        items.add(loc, item);
        notifyItemInserted(loc);
    }

    public void updateTask(ModelTask newTask) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).isTask()) {
                ModelTask task = (ModelTask) getItem(i);
                if (newTask.getTimeStamp() == task.getTimeStamp()) {
                    removeItem(i);
                    getTaskFragment().addTask(newTask, false);
                }
            }
        }
    }

    public void removeItem(int pos) {
        if (pos >= 0 && pos <= getItemCount() - 1) {
            items.remove(pos);
            notifyItemRemoved(pos);
            if (pos - 1 >= 0 && pos <= getItemCount() - 1) {
                if (!getItem(pos).isTask() && !getItem(pos - 1).isTask()) {
                    ModelSeparator separator = (ModelSeparator) getItem(pos - 1);
                    checkSeparators(separator.getType());
                    items.remove(pos - 1);
                    notifyItemRemoved(pos - 1);
                }

            } else if (getItemCount() - 1 >= 0 && !getItem(getItemCount() - 1).isTask()) {
                ModelSeparator separator = (ModelSeparator) getItem(getItemCount() - 1);
                checkSeparators(separator.getType());

                int posTemp = getItemCount() - 1;
                items.remove(posTemp);
                notifyItemRemoved(posTemp);
            }
        }
    }

    public void checkSeparators(int type) {
        switch (type) {
            case ModelSeparator.TYPE_OVERDUE:
                containSeparatorOverdue = false;
                break;
            case ModelSeparator.TYPE_TODAY:
                containSeparatorToday = false;
                break;
            case ModelSeparator.TYPE_TOMORROW:
                containSeparatorTomorrow = false;
                break;
            case ModelSeparator.TYPE_FUTURE:
                containSeparatorFuture = false;
                break;
        }

    }

    public void removeAllItems() {
        if (getItemCount() != 0) {
            items = new ArrayList<>();
            notifyDataSetChanged();
            containSeparatorOverdue = false;
            containSeparatorToday = false;
            containSeparatorTomorrow = false;
            containSeparatorFuture = false;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class TaskViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected TextView date;
        protected CircleImageView priority;

        public TaskViewHolder(View itemView, TextView title, TextView date, CircleImageView priority) {
            super(itemView);
            this.title = title;
            this.date = date;
            this.priority = priority;
        }
    }

    protected class SeparatorViewHolder extends RecyclerView.ViewHolder {

        protected TextView type;

        public SeparatorViewHolder(View itemView, TextView type) {
            super(itemView);
            this.type = type;
        }
    }

    public TaskFragment getTaskFragment() {
        return taskFragment;
    }
}
