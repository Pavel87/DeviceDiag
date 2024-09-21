# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\tqm837\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class com.tutelatechnologies.** { *; }
-keep class com.tutelatechnologies.**$** { *; }
-keep interface com.tutelatechnologies.** { *; }
-keep enum com.tutelatechnologies.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-keep class gatewayprotocol.** { *; }
-keep class com.google.protobuf.* { *; }

-keep class org.slf4j.impl.StaticLoggerBinder { *; }
-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep class com.facebook.infer.annotation.** { *; }
-dontwarn com.facebook.infer.annotation.**
