package intent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public interface IntentListener
{
    void onIntentBeActivated(@NonNull String intent, @Nullable String action, @Nullable HashMap<Object, Object> extra);
}
