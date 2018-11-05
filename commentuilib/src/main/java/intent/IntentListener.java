package intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public interface IntentListener
{
    void onIntentBeActivated(@NonNull String intent, @Nullable String action, @Nullable HashMap<Object, Object> extra);
}
