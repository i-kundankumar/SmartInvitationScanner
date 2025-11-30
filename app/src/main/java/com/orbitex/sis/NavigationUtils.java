package com.orbitex.sis;

import android.app.Activity;
import android.content.Intent;

public class NavigationUtils {
    public static void go(Activity from, Class<?> to, boolean finishCurrent) {
        Intent intent = new Intent(from, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        from.startActivity(intent);

        from.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (finishCurrent) from.finish();
    }
}
