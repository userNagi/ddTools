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
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep,allowshrinking class androidx.recyclerview.widget.RecyclerView
-keep,allowshrinking class com.nagi.ddtools.ui.adapter.IdolGroupListAdapter
-keep,allowshrinking class com.nagi.ddtools.DdTools
-keep,allowshrinking class com.nagi.ddtools.data.*
-keep,allowshrinking class com.nagi.ddtools.database.*
-keep,allowshrinking class com.nagi.ddtools.ui.toolpage.tools.*
-keep,allowshrinking class com.nagi.ddtools.resourceGet.*
-keep,allowshrinking class com.nagi.ddtools.utils.*
# Room
-keep class * extends androidx.room.RoomDatabase {
    <methods>;
}

-keep @androidx.room.Entity class * {
    *;
}

-keep @androidx.room.Dao class * {
    *;
}
-keep class com.nagi.ddtools.data.UpdateInfo {
     <fields>;
     <init>(...);
 }
 -keep class com.nagi.ddtools.data.TagsList {
      <fields>;
      <init>(...);
  }
 -keep class com.nagi.ddtools.data.MediaList {
      <fields>;
      <init>(...);
  }