package corp.penguin.penguintodo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vladislav on 22.02.16.
 */
public class PreferenceHelper {

    public static final String SPLASH_IS_INVISIBLE = "SPLASH_IS_INVISIBLE";
    private static PreferenceHelper instance;
    protected Context context;
    private SharedPreferences preferences;
    private PreferenceHelper() {

    }

    public static PreferenceHelper getInstance() {
        if (instance == null) {
            instance = new PreferenceHelper();
        }

        return instance;
    }

    public void initialize(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    public void putBool(String key, boolean val) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

    public boolean getBool(String key) {
        return preferences.getBoolean(key, false);
    }

}
