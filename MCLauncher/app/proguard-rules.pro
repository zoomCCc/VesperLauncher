# Add project specific ProGuard rules here.
-keepattributes Signature
-keepattributes *Annotation*

-keep class com.mclauncher.data.models.** { *; }
-keep class com.mclauncher.network.** { *; }

-dontwarn okhttp3.**
-dontwarn retrofit2.**
