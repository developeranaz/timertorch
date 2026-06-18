# ProGuard rules for TimerTorch.
# The default rules from proguard-android-optimize.txt are already applied.

# Ensure our MainActivity is preserved since it is entry point from manifest
-keep public class com.example.timertorch.MainActivity {
    public *;
}
