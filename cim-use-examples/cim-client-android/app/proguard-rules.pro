-keep class **.R$* {   *;  }
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
 -dontwarn
-dontskipnonpubliclibraryclassmembers
-keepattributes *Annotation*
-keepattributes Signature


-dontwarn okio.**
-dontwarn com.google.protobuf.**
-dontwarn okhttp3.**

-keep class com.google.protobuf.** { *; }
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase


-keep class com.farsunset.cim.sdk.android.model.** {*;}

-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.AppCompatActivity
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver


-keepclassmembers class * extends android.webkit.WebChromeClient{
    public void openFileChooser(...);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
 
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
 
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
 
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
 
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}