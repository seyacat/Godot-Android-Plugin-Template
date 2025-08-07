package org.godotengine.plugin.android.template

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot
import android.graphics.PixelFormat
import android.os.Build

class GodotAndroidPlugin(godot: Godot): GodotPlugin(godot) {

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME

    @UsedByGodot
    fun createOverlayView() {
        Log.d(pluginName, "Intentando crear overlay view")
        val activity = activity ?: run {
            Log.e(pluginName, "Activity es null")
            return
        }

        if (!Settings.canDrawOverlays(activity)) {
            Log.d(pluginName, "Solicitando permiso overlay")
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.packageName)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            activity.startActivity(intent)
            return
        }

        Log.d(pluginName, "Creando overlay view")
        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // Configurar la ventana principal primero
        activity?.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
            setFormat(PixelFormat.TRANSPARENT)
        }

        // Configurar el overlay
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSPARENT
        ).apply {
            format = PixelFormat.TRANSPARENT
            gravity = android.view.Gravity.TOP or android.view.Gravity.LEFT
            alpha = 0.0f
            x = 0
            y = 0
        }

        try {
            val overlayView = android.view.View(activity).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
            activity.windowManager?.addView(overlayView, params)
        } catch (e: Exception) {
            Log.e(pluginName, "Error setting up overlay window", e)
        }
    }

    @UsedByGodot
    fun helloWorld() {
        activity?.let { act ->
            runOnUiThread {
                Toast.makeText(act, "Hello World", Toast.LENGTH_LONG).show()
                Log.v(pluginName, "Hello World")
            }
        }
    }
}
