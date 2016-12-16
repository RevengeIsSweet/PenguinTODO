package corp.penguin.penguintodo.model;

import corp.penguin.penguintodo.R;

/**
 * Created by vladislav on 02.03.16.
 */
public class ModelSeparator implements Item {

    public static final int TYPE_OVERDUE = R.string.separator_overdue;
    public static final int TYPE_TODAY = R.string.separator_today;
    public static final int TYPE_TOMORROW = R.string.separator_tomorrow;
    public static final int TYPE_FUTURE = R.string.separator_future;

    private int type;


    public ModelSeparator(int type) {
        this.type = type;
    }

    @Override
    public boolean isTask() {
        return false;
    }

    public int getType() {
        return type;
    }

    public void setType (int type) {
        this.type = type;
    }
}