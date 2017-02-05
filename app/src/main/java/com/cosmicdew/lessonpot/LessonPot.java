package com.cosmicdew.lessonpot;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.orm.SugarContext;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by S.K. Pissay on 6/10/16.
 */

public class LessonPot extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        Fabric.with(this, new Crashlytics());
        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
