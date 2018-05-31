# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/hjsoft/Android/Sdk/tools/proguard/proguard-android.txt
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
-dontwarn okio.**
-dontwarn android.support.**
-dontwarn retrofit2.**
-dontwarn sun.misc.Unsafe
-dontwarn rx.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

-keep class retrofit2.** { *; }

-keepclasseswithmembers class * {
    @retrofit.** *;
}
-keepclassmembers class * {
    @retrofit.** *;
}

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclassmembers,allowobfuscation interface * {
    @retrofit.http.** <methods>;
}

-keepclassmembers class * {
  public void *(android.view.View);
}

-renamesourcefileattribute SourceFile
#-keepattributes  Signature,SourceFile,LineNumberTable
#-keepattributes *Annotation*
#-keepattributes Exceptions
-keep public class * extends android.app.Application


-keepattributes Exceptions, InnerClasses,*Annotation*, Signature, Deprecated, SourceFile, LineNumberTable,EnclosingMethod

-dontwarn com.pubnub.**
-keep class com.pubnub.** { *; }

-dontwarn org.slf4j.**

