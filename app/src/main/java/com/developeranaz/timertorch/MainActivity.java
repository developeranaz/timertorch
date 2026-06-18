package com.developeranaz.timertorch;

/*
 * HOW TO BUILD THE RELEASE APK:
 * 
 * 1. Open a terminal in the root directory of this project (timertorch/).
 * 2. Run the Gradle wrapper build command for release:
 *      On Windows:
 *          .\gradlew.bat assembleRelease
 *      On Linux/macOS:
 *          ./gradlew assembleRelease
 * 
 * 3. The optimized release APK will be generated at:
 *      app/build/outputs/apk/release/app-release-unsigned.apk
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

public class MainActivity extends Activity {

    private CameraManager cameraManager;
    private String cameraId;
    private boolean isTorchOn = false;
    private CountDownTimer countDownTimer;

    private ToggleButton toggleButton;
    private EditText editTimer;
    private CheckBox chkCloseApp;
    private Button btnSetTimer;
    private TextView txtCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide Title Bar for clean minimal look
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        // Customize system status bar and navigation bar to match dark theme (API 21+)
        getWindow().setStatusBarColor(Color.parseColor("#121212"));
        getWindow().setNavigationBarColor(Color.parseColor("#121212"));

        // Initialize CameraManager and find rear camera with a flash unit
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] list = cameraManager.getCameraIdList();
            for (String id : list) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (hasFlash != null && hasFlash) {
                    cameraId = id;
                    break;
                }
            }
            if (cameraId == null && list.length > 0) {
                cameraId = list[0];
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // --- UI Construction (Minimal Dark Design) ---
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER);
        rootLayout.setPadding(dpToPx(32), dpToPx(32), dpToPx(32), dpToPx(32));
        rootLayout.setBackgroundColor(Color.parseColor("#121212"));

        // Margin parameters
        LinearLayout.LayoutParams standardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        standardParams.setMargins(0, dpToPx(14), 0, dpToPx(14));

        // 0. Logo display (ImageView)
        int logoId = getResources().getIdentifier("icon", "drawable", getPackageName());
        if (logoId != 0) {
            ImageView imgLogo = new ImageView(this);
            imgLogo.setImageResource(logoId);
            LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(dpToPx(72), dpToPx(72));
            logoParams.setMargins(0, 0, 0, dpToPx(20));
            imgLogo.setLayoutParams(logoParams);
            rootLayout.addView(imgLogo);
        }

        // 1. ToggleButton (Flashlight Toggle)
        toggleButton = new ToggleButton(this);
        toggleButton.setTextOn("FLASHLIGHT ON");
        toggleButton.setTextOff("FLASHLIGHT OFF");
        toggleButton.setChecked(false);
        toggleButton.setText("FLASHLIGHT OFF"); // Initial state
        toggleButton.setTextSize(16f);
        toggleButton.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        toggleButton.setLayoutParams(standardParams);
        toggleButton.setPadding(0, dpToPx(18), 0, dpToPx(18));
        toggleButton.setBackground(createRoundedDrawable("#2C2C2C", "#3F3F3F", dpToPx(12)));
        toggleButton.setTextColor(Color.WHITE);
        rootLayout.addView(toggleButton);

        // 2. Horizontal adjusters layout for typing and using +/- buttons
        LinearLayout adjusterLayout = new LinearLayout(this);
        adjusterLayout.setOrientation(LinearLayout.HORIZONTAL);
        adjusterLayout.setGravity(Gravity.CENTER_VERTICAL);
        adjusterLayout.setLayoutParams(standardParams);

        // Decrement Button [-]
        Button btnMinus = new Button(this);
        btnMinus.setText("-");
        btnMinus.setTextColor(Color.WHITE);
        btnMinus.setTextSize(22f);
        btnMinus.setTypeface(Typeface.DEFAULT_BOLD);
        btnMinus.setBackground(createRoundedDrawable("#2C2C2C", "#3F3F3F", dpToPx(8)));
        LinearLayout.LayoutParams minusParams = new LinearLayout.LayoutParams(dpToPx(52), dpToPx(52));
        btnMinus.setLayoutParams(minusParams);

        // EditText for manual input
        editTimer = new EditText(this);
        editTimer.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editTimer.setHint("Minutes");
        editTimer.setTextColor(Color.WHITE);
        editTimer.setHintTextColor(Color.parseColor("#666666"));
        editTimer.setGravity(Gravity.CENTER);
        editTimer.setTextSize(16f);
        editTimer.setBackground(createRoundedDrawable("#1E1E1E", "#2C2C2C", dpToPx(8)));
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(0, dpToPx(52), 1.0f);
        inputParams.setMargins(dpToPx(12), 0, dpToPx(12), 0);
        editTimer.setLayoutParams(inputParams);

        // Increment Button [+]
        Button btnPlus = new Button(this);
        btnPlus.setText("+");
        btnPlus.setTextColor(Color.WHITE);
        btnPlus.setTextSize(22f);
        btnPlus.setTypeface(Typeface.DEFAULT_BOLD);
        btnPlus.setBackground(createRoundedDrawable("#2C2C2C", "#3F3F3F", dpToPx(8)));
        btnPlus.setLayoutParams(minusParams);

        adjusterLayout.addView(btnMinus);
        adjusterLayout.addView(editTimer);
        adjusterLayout.addView(btnPlus);
        rootLayout.addView(adjusterLayout);

        // 3. CheckBox ("Automatically close app after timer ends")
        chkCloseApp = new CheckBox(this);
        chkCloseApp.setText("Close app when timer ends");
        chkCloseApp.setTextColor(Color.parseColor("#E0E0E0"));
        chkCloseApp.setTextSize(14f);
        chkCloseApp.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        
        // Programmatic Button Tint List to fix CheckBox visibility in Dark Background
        int[][] states = new int[][] {
            new int[] { android.R.attr.state_checked },
            new int[] { -android.R.attr.state_checked }
        };
        int[] colors = new int[] {
            Color.parseColor("#00E676"),
            Color.parseColor("#888888")
        };
        chkCloseApp.setButtonTintList(new ColorStateList(states, colors));

        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        checkParams.gravity = Gravity.START;
        checkParams.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(12));
        chkCloseApp.setLayoutParams(checkParams);
        rootLayout.addView(chkCloseApp);

        // 4. Set Timer Button
        btnSetTimer = new Button(this);
        btnSetTimer.setText("SET TIMER");
        btnSetTimer.setTextColor(Color.WHITE);
        btnSetTimer.setTextSize(16f);
        btnSetTimer.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        btnSetTimer.setBackground(createRoundedDrawable("#2979FF", null, dpToPx(12)));
        btnSetTimer.setPadding(0, dpToPx(16), 0, dpToPx(16));
        btnSetTimer.setLayoutParams(standardParams);
        rootLayout.addView(btnSetTimer);

        // 5. TextView for countdown display
        txtCountdown = new TextView(this);
        txtCountdown.setText("No active timer");
        txtCountdown.setGravity(Gravity.CENTER);
        txtCountdown.setTextColor(Color.parseColor("#888888"));
        txtCountdown.setTextSize(15f);
        txtCountdown.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        
        LinearLayout.LayoutParams countdownParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        countdownParams.setMargins(0, dpToPx(8), 0, dpToPx(8));
        txtCountdown.setLayoutParams(countdownParams);
        rootLayout.addView(txtCountdown);

        setContentView(rootLayout);

        // --- Action Listeners ---

        // Decrement button handler (smart increments: 0.1 below 1 min, 1.0 above)
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentVal = 1.0;
                String text = editTimer.getText().toString().trim();
                if (!text.isEmpty()) {
                    try {
                        currentVal = Double.parseDouble(text);
                    } catch (NumberFormatException e) {
                        // fallback
                    }
                }
                double step = (currentVal > 1.0) ? 1.0 : 0.1;
                currentVal -= step;
                if (currentVal < 0.0833) {
                    currentVal = 0.0833; // Clamp to 5s minimum (0.0833 mins)
                }
                updateTimeInputText(currentVal);
            }
        });

        // Increment button handler
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentVal = 0.0;
                String text = editTimer.getText().toString().trim();
                if (!text.isEmpty()) {
                    try {
                        currentVal = Double.parseDouble(text);
                    } catch (NumberFormatException e) {
                        // fallback
                    }
                }
                double step = (currentVal >= 1.0) ? 1.0 : 0.1;
                currentVal += step;
                updateTimeInputText(currentVal);
            }
        });

        // Toggle button handler
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toggleButton.isChecked()) {
                    cancelActiveTimer();
                }
                setTorch(toggleButton.isChecked());
            }
        });

        // Set timer button handler
        btnSetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimerFromInput();
            }
        });
    }

    /**
     * Updates the time text field with proper decimal formatting.
     */
    private void updateTimeInputText(double val) {
        if (val == (long) val) {
            editTimer.setText(String.valueOf((long) val));
        } else {
            editTimer.setText(String.format(java.util.Locale.US, "%.2f", val));
        }
        editTimer.setSelection(editTimer.getText().length());
    }

    /**
     * Toggles physical camera flash or falls back to simulation mode.
     */
    private void setTorch(boolean on) {
        if (cameraId == null) {
            // Simulator Mode (For Emulators without flash units)
            isTorchOn = on;
            toggleButton.setChecked(on);
            toggleButton.setText(on ? "FLASHLIGHT ON" : "FLASHLIGHT OFF");
            toggleButton.setBackground(createRoundedDrawable(on ? "#00E676" : "#2C2C2C", on ? null : "#3F3F3F", dpToPx(12)));
            toggleButton.setTextColor(on ? Color.parseColor("#121212") : Color.WHITE);
            Toast.makeText(this, "[Simulated] Flashlight " + (on ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            cameraManager.setTorchMode(cameraId, on);
            isTorchOn = on;
            toggleButton.setChecked(on);
            toggleButton.setText(on ? "FLASHLIGHT ON" : "FLASHLIGHT OFF");
            toggleButton.setBackground(createRoundedDrawable(on ? "#00E676" : "#2C2C2C", on ? null : "#3F3F3F", dpToPx(12)));
            toggleButton.setTextColor(on ? Color.parseColor("#121212") : Color.WHITE);
        } catch (CameraAccessException | IllegalArgumentException e) {
            e.printStackTrace();
            // Fallback to simulator mode
            isTorchOn = on;
            toggleButton.setChecked(on);
            toggleButton.setText(on ? "FLASHLIGHT ON" : "FLASHLIGHT OFF");
            toggleButton.setBackground(createRoundedDrawable(on ? "#00E676" : "#2C2C2C", on ? null : "#3F3F3F", dpToPx(12)));
            toggleButton.setTextColor(on ? Color.parseColor("#121212") : Color.WHITE);
            Toast.makeText(this, "[Simulated] Flashlight " + (on ? "ON" : "OFF") + " (Native Failed)", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Parses input and starts CountDownTimer with validation.
     */
    private void startTimerFromInput() {
        cancelActiveTimer();

        String inputText = editTimer.getText().toString().trim();
        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double minutes = Double.parseDouble(inputText);
            long durationMs = (long) (minutes * 60.0 * 1000.0);

            // Constraint: 5 seconds (5000 ms) minimum limit
            if (durationMs < 5000) {
                Toast.makeText(this, "Minimum timer limit is 5 seconds", Toast.LENGTH_SHORT).show();
                return;
            }

            // Automatically turn flashlight ON if it is currently off
            if (!isTorchOn) {
                setTorch(true);
            }

            startCountdown(durationMs);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Instantiates and starts the CountDownTimer.
     */
    private void startCountdown(long durationMs) {
        txtCountdown.setTextColor(Color.parseColor("#00E676")); // Green when active
        countDownTimer = new CountDownTimer(durationMs, 100) { // Check status every 100ms for responsiveness
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;
                long tenths = (millisUntilFinished % 1000) / 100;

                // If less than a minute, show tenths of a second for precision
                if (minutes == 0) {
                    txtCountdown.setText(String.format(java.util.Locale.US, "Time remaining: %02d.%d s", seconds, tenths));
                } else {
                    txtCountdown.setText(String.format(java.util.Locale.US, "Time remaining: %02d:%02d", minutes, seconds));
                }
            }

            @Override
            public void onFinish() {
                txtCountdown.setText("Timer finished");
                txtCountdown.setTextColor(Color.parseColor("#888888"));
                setTorch(false);

                // Option: Automatically close app after timer ends
                if (chkCloseApp.isChecked()) {
                    finishAndRemoveTask();
                }
            }
        }.start();
    }

    /**
     * Cancels active countdown timer.
     */
    private void cancelActiveTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        txtCountdown.setText("No active timer");
        txtCountdown.setTextColor(Color.parseColor("#888888"));
    }

    /**
     * Helper to create programmatically styled Vector Drawables (rounded corners, stroke, and color).
     */
    private GradientDrawable createRoundedDrawable(String bgColorHex, String strokeColorHex, float cornerRadius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColorHex));
        gd.setCornerRadius(cornerRadius);
        if (strokeColorHex != null) {
            gd.setStroke(dpToPx(1), Color.parseColor(strokeColorHex));
        }
        return gd;
    }

    /**
     * Density-independent pixels to physical pixels converter.
     */
    private int dpToPx(int dp) {
        return Math.round((float) dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelActiveTimer();
        if (isTorchOn) {
            setTorch(false);
        }
    }
}
