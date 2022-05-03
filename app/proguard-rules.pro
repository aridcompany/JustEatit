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
-keep class androidx.appcompat.widget.**{ *; }
-keep class com.ari_d.justeatit.data.entities.Account_Items { *; }
-keep class com.ari_d.justeatit.data.entities.Address { *; }
-keep class com.ari_d.justeatit.data.entities.Comment { *; }
-keep class com.ari_d.justeatit.data.entities.Contact_Info { *; }
-keep class com.ari_d.justeatit.data.entities.Favorite { *; }
-keep class com.ari_d.justeatit.data.entities.Feedback { *; }
-keep class com.ari_d.justeatit.data.entities.Orders { *; }
-keep class com.ari_d.justeatit.data.entities.Product { *; }
-keep class com.ari_d.justeatit.data.entities.SupportedLocations { *; }
-keep class com.ari_d.justeatit.data.entities.User { *; }
-keep class com.ari_d.justeatit.data.entities.Wallet { *; }
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations