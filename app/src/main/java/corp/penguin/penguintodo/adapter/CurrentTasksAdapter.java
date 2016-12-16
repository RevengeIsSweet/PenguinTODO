package corp.penguin.penguintodo.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import corp.penguin.penguintodo.CalendarHelper;
import corp.penguin.penguintodo.R;
import corp.penguin.penguintodo.fragment.TaskFragment;
import corp.penguin.penguintodo.model.Item;
import corp.penguin.penguintodo.model.ModelSeparator;
import corp.penguin.penguintodo.model.ModelTask;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by vladislav on 25.02.16.
 */
public class CurrentTasksAdapter extends TaskAdapter {

    private static final int TYPE_TASK = 0;
    private static final int TYPE_SEPARATOR = 1;
    public CurrentTasksAdapter(TaskFragment taskFragment) {
        super(taskFragment);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case TYPE_TASK:
                View v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.model_task, viewGroup, false);
                TextView title = (TextView) v.findViewById(R.id.tvTaskTitle);
                TextView date = (TextView) v.findViewById(R.id.tvTaskDate);
                CircleImageView priority = (CircleImageView) v.findViewById(R.id.cvTaskPriority);

                return new TaskViewHolder(v, title, date, priority);

            case TYPE_SEPARATOR:
                View separator = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.model_separator, viewGroup, false);
                TextView type = (TextView) separator.findViewById(R.id.tvSeparatorName);

                return new SeparatorViewHolder(separator, type);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        Item item = items.get(position);
        final Resources resources = viewHolder.itemView.getResources();

        if (item.isTask()) {
            viewHolder.itemView.setEnabled(true);
            final ModelTask task = (ModelTask) item;
            final TaskViewHolder taskViewHolder = (TaskViewHolder) viewHolder;
            final View itemView = taskViewHolder.itemView;
            final Context itemViewContext = itemView.getContext();

            taskViewHolder.title.setText(task.getTitle());

            if (task.getDate() != 0) {
                taskViewHolder.date.setText(CalendarHelper.getStringDate(task.getDate()));
            } else
                taskViewHolder.date.setText(null);

            itemView.setVisibility(View.VISIBLE);
            taskViewHolder.priority.setEnabled(true);

            if (task.getDateStatus() == ModelSeparator.TYPE_OVERDUE) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemViewContext, R.color.gray_200));
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(itemViewContext, R.color.gray_50));
            }

            taskViewHolder.title.setTextColor(ContextCompat.getColor(
                    taskViewHolder.title.getContext(),
                    R.color.primary_text_default

            ));
            taskViewHolder.date.setTextColor(ContextCompat.getColor(
                    taskViewHolder.date.getContext(),
                    R.color.secondary_text_disabled
            ));

            taskViewHolder.priority.setImageResource(R.drawable.ic_checkbox_blank_circle_white_48dp);
            taskViewHolder.priority.setColorFilter(ContextCompat.getColor(
                    taskViewHolder.priority.getContext(),
                    task.getPriorityColor()
            ));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTaskFragment().showTaskEditDialog(task);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getTaskFragment().removeTaskDialog(taskViewHolder.getLayoutPosition());
                        }
                    }, 1000);

                    return true;
                }
            });

            taskViewHolder.priority.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    taskViewHolder.priority.setEnabled(false);
                    task.setStatus(ModelTask.STATUS_DONE);
                    getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(), ModelTask.STATUS_DONE);

                    taskViewHolder.title.setTextColor(ContextCompat.getColor(
                            taskViewHolder.title.getContext(),
                            R.color.primary_text_disabled
                    ));
                    taskViewHolder.date.setTextColor(ContextCompat.getColor(
                            taskViewHolder.date.getContext(),
                            R.color.secondary_text_disabled
                    ));
                    taskViewHolder.priority.setColorFilter(task.getPriorityColor());

                    ObjectAnimator flipIn = ObjectAnimator.ofFloat(taskViewHolder.priority, "rotationY", -180f, 0f);
                    flipIn.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (task.getStatus() == ModelTask.STATUS_DONE) {
                                taskViewHolder.priority.setImageResource(R.drawable.ic_check_circle_white_48dp);

                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,
                                        "translationX", 0f, itemView.getWidth());

                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,
                                        "translationX", itemView.getWidth(), 0f);

                                translationX.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        itemView.setVisibility(View.GONE);
                                        TaskFragment taskFragment1 = getTaskFragment();
                                        taskFragment1.moveTask(task);
                                        removeItem(taskViewHolder.getLayoutPosition());

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {
                                    }
                                });

                                AnimatorSet translationSet = new AnimatorSet();
                                translationSet.play(translationX).before(translationXBack);
                                translationSet.start();
                            }

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    flipIn.start();
                }
            });
        } else {
            ModelSeparator separator = (ModelSeparator) item;
            SeparatorViewHolder separatorViewHolder = (SeparatorViewHolder) viewHolder;
            separatorViewHolder.type.setText(resources.getString(separator.getType()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) {
            return TYPE_TASK;
        } else return TYPE_SEPARATOR;
    }

}
