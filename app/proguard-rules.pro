-keep class com.k2fsa.sherpa.onnx.** { *; }
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Bullet's JNI bridge accesses these math fields by name.
-keepclassmembers class com.badlogic.gdx.math.Vector3 { float x; float y; float z; }
-keepclassmembers class com.badlogic.gdx.math.Quaternion { float x; float y; float z; float w; }
-keepclassmembers class com.badlogic.gdx.math.Matrix3 { float[] val; }
-keepclassmembers class com.badlogic.gdx.math.Matrix4 { float[] val; }
