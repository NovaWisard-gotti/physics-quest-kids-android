# Física Quest - reglas ProGuard/R8
# Room
-keep class com.kidslab.physicsquest.data.local.entity.** { *; }
-dontwarn androidx.room.paging.**

# Compose y Kotlin metadata
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
