# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep public class com.jys.searchpot.NewStore { *; }
-keep public class com.jys.searchpot.Store { *; }
#-keep public class com.jys.searchpot.MainActivity { *; }
#-keep public class com.jys.searchpot.LoadingActivity { *; }
#-keep public class com.jys.searchpot.CustomAdapter { *; }
#-keep public class com.jys.searchpot.R { *; }
#-keep public class com.jys.searchpot.HangulUtils { *; }
#-keep public class com.jys.searchpot.MyFirebaseMessagingService { *; }
#-keep public class com.jys.searchpot.BuildConfig { *; }
#-keep public class com.google.firebase.example.fireeats.model.** { *; }