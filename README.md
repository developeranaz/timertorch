# TimerTorch (Ultra-Minimalist Android Flashlight)

TimerTorch is an ultra-minimalist, low-level, and highly optimized Android flashlight (torch) application featuring an automatic **Timer Off** controller. 

To prioritize minimal file size and zero bloat, the application:
1. Is written in **Pure Java** (no Kotlin standard library overhead).
2. Builds the **UI programmatically in Java** (no Jetpack Compose or XML layout files).
3. Declares **zero external dependencies** in Gradle (compiles directly against the native Android SDK).
4. Enables full ProGuard/R8 code minification and resource shrinking for the release build.

The resulting release APK is only **19.21 KB (19,215 bytes)**!

---

## Features
- **Flashlight Toggle**: Turn the flashlight ON and OFF using native `CameraManager.setTorchMode()` (does not require the camera permission on API 21+).
- **Time Adjusters**: Increment or decrement minutes using dedicated `[-]` and `[+]` buttons, or type in a precise decimal value directly.
- **Smart Steps**: Increments/decrements change by `0.1` minutes (6 seconds) when the value is below `1.0` minute, and by `1.0` minute when it is above `1.0`.
- **Validation**: Enforces a minimum timer limit of **5 seconds** up to unlimited.
- **Precise Countdown**: Shows remaining time in `mm:ss` format, and automatically transitions to high-precision tenths-of-a-second (e.g. `05.4 s`) once the timer drops below 1 minute.
- **Auto-Close App**: A checkbox allows configuring the app to automatically close (`finishAndRemoveTask()`) once the countdown finishes, clearing itself from the task tray.
- **Simulator Fallback**: If run on an emulator (or a device without a physical flash unit), the app enters **Simulator Mode**, allowing you to test the complete button toggling and countdown timer logic with clear Toast status messages.

---

## Privacy, Safety & Minimal Footprint
TimerTorch is engineered on a strict "least privilege" philosophy, guaranteeing complete transparency and respect for your device:
- **100% Safe & Auditable**: Zero third-party SDKs, analytics, trackers, or hidden background services. The entire application logic resides in a single, easily readable `MainActivity.java` file.
- **No Internet Connection Required**: The app never requests the `INTERNET` permission and operates entirely offline. Your data never leaves your device.
- **Ultra-Low Storage & RAM Usage**: The fully optimized release APK weighs in at just **~19 KB**. It runs efficiently with negligible memory overhead, making it ideal for older or storage-constrained devices.
- **Zero Unwanted Permissions**: Adheres strictly to the principle of least privilege. Only declares optional hardware features (`android.hardware.camera.flash`) in the manifest. No location, storage, contacts, microphone, or identity access is ever requested.
- **Clean Lifecycle Management**: Follows Android best practices. The optional auto-exit feature ensures no background tasks or services linger after the timer completes.

---

## Project Structure
```text
timertorch/
├── build.gradle            # Root build configuration
├── settings.gradle         # Project module definition
├── gradle.properties       # Gradle JVM variables
├── gradlew / gradlew.bat   # Gradle wrappers for building
├── build.bat               # Windows automatic build shortcut
├── app/
│   ├── build.gradle        # App configuration (Minifies & shrinks code)
│   ├── proguard-rules.pro  # Proguard entry-point preservation rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml  # Uses lightweight native platform theme
│           └── java/
│               └── com/developeranaz/timertorch/
│                   └── MainActivity.java   # Contains layout and controller logic
```

---

## Build Instructions

Ensure you have a JDK (preferably JDK 17) and the Android SDK path configured in your environment.

### 1. Build Debug APK (Signed with default debug key)
Ideal for testing on emulators or developer devices:
```powershell
# Windows
.\gradlew.bat assembleDebug

# Linux/macOS
./gradlew assembleDebug
```
The compiled debug APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### 2. Build Release APK (Unsigned)
Optimized and minified release build:
```powershell
# Windows
.\gradlew.bat assembleRelease

# Linux/macOS
./gradlew assembleRelease
```
The optimized release APK will be generated at:
`app/build/outputs/apk/release/app-release-unsigned.apk`

---

## Install and Run

Make sure your emulator or physical Android device is connected to the computer.

1. **Verify Connection**:
   ```bash
   adb devices
   ```

2. **Install to Connected Device**:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

3. **Launch the Application**:
   ```bash
   adb shell am start -S -n com.developeranaz.timertorch/com.developeranaz.timertorch.MainActivity
   ```
