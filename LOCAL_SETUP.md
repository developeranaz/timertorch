# Local Environment & Development Tools Setup

This guide documents the exact paths of the development and build tools available on this PC, along with instructions on how to use them to build, sign, and debug the TimerTorch application.

---

## 1. Local Tool Paths

### Java Development Kit (JDK 17)
- **JDK Home**: `C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot\`
- **Java Compiler (`javac`)**: `C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot\bin\javac.exe`
- **Certificate Utility (`keytool`)**: `C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot\bin\keytool.exe`

### Android SDK & Debugging Tools
- **Android SDK Root**: `C:\Android\android-sdk\`
- **Android Debug Bridge (`adb`)**: `C:\Android\android-sdk\platform-tools\adb.exe`

### Android Build Tools (Version 36.0.0)
- **Build Tools Root**: `C:\Android\android-sdk\build-tools\36.0.0\`
- **Alignment Tool (`zipalign`)**: `C:\Android\android-sdk\build-tools\36.0.0\zipalign.exe`
- **Signing Tool (`apksigner`)**: `C:\Android\android-sdk\build-tools\36.0.0\apksigner.bat`
- **Packaging Inspector (`aapt`)**: `C:\Android\android-sdk\build-tools\36.0.0\aapt.exe`

### Standalone Gradle Build System
- **Gradle executable**: `C:\Android\gradle\gradle-8.4\bin\gradle.bat`

---

## 2. Walkthrough: Aligning & Signing the Release APK

Since Gradle builds an unsigned release APK (`app-release-unsigned.apk`) by default, you must align and sign it before installing it on a physical device. Follow these steps:

### Step A: Generate a Private Signing Key (One-time setup)
Run the Java `keytool` to create a keystore file named `my-release-key.jks`:
```cmd
"C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot\bin\keytool.exe" -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```
*Follow the terminal prompts to set a password and input certificate details.*

### Step B: Align the Compiled APK
Zip-alignment optimizes the APK structure for memory efficiency. Navigate to your project folder and run:
```cmd
"C:\Android\android-sdk\build-tools\36.0.0\zipalign.exe" -v -p 4 app\build\outputs\apk\release\app-release-unsigned.apk app-release-aligned.apk
```

### Step C: Sign the Aligned APK
Use `apksigner` and the keystore generated in Step A to sign the aligned APK:
```cmd
"C:\Android\android-sdk\build-tools\36.0.0\apksigner.bat" sign --keystore my-release-key.jks --out app-release-signed.apk app-release-aligned.apk
```
*Input the keystore password when prompted. The resulting `app-release-signed.apk` is ready for deployment.*

---

## 3. ADB Deployment Cheat Sheet

Use these commands to manage the app on a connected emulator or USB-debugging-enabled device:

- **List Connected Devices**:
  ```cmd
  "C:\Android\android-sdk\platform-tools\adb.exe" devices
  ```
- **Install APK**:
  ```cmd
  "C:\Android\android-sdk\platform-tools\adb.exe" install -r app-release-signed.apk
  ```
- **Launch MainActivity**:
  ```cmd
  "C:\Android\android-sdk\platform-tools\adb.exe" shell am start -S -n com.developeranaz.timertorch/com.developeranaz.timertorch.MainActivity
  ```
- **View App Error Log**:
  ```cmd
  "C:\Android\android-sdk\platform-tools\adb.exe" logcat -d *:E
  ```
