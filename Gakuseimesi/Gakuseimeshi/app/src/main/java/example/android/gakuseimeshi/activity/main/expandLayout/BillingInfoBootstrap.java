package example.android.gakuseimeshi.activity.main.expandLayout;

import android.app.Application;
import com.beardedhen.androidbootstrap.TypefaceProvider;

public class BillingInfoBootstrap extends Application {
    @Override public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }
}