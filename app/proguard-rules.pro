-optimizationpasses 6
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# Chromium 156 — keep all core
-keep class org.chromium.** { *; }
-keepnames class org.chromium.** { *; }
-dontwarn org.chromium.**

# SoKo Browser
-keep class com.soko.browser.** { *; }
-keepnames class com.soko.browser.** { *; }

# MV3 Extensions
-keep class org.chromium.extensions.** { *; }
-dontwarn org.chromium.extensions.**
