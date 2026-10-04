# Add project specific ProGuard rules here.
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Chromium/Cronet keep rules
-keep class org.chromium.** { *; }
-dontwarn org.chromium.**

# SoKo Browser — preserve all main classes
-keep class com.soko.browser.** { *; }
