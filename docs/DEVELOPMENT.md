# Development


Use **JDK 21**, **Android SDK platform 37.0**, and **Build Tools 36.0.0**, then run:

```sh
git clone https://github.com/faridbtw/DSU-Sideloader.git
cd DSU-Sideloader
./gradlew testDebugUnitTest assembleDebug lintDebug
```

The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

Android Studio previews for light mode, dark mode, and larger text are in [`WorkspacePreview.kt`](app/src/main/java/vegabobo/dsusideloader/ui/screen/home/WorkspacePreview.kt). New labels cover all six existing translated locales. The lint baseline records only 67 pre-existing translation gaps in upstream resources; new code and resources are still checked.


[Back to the project](../README.md)
