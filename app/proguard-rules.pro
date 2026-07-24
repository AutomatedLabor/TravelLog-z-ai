# TravelLog ProGuard Rules
-keepattributes *Annotation*
-keep class com.example.travellog.data.db.entity.** { *; }
-keep class com.example.travellog.data.model.** { *; }
-dontwarn org.osmdroid.**
