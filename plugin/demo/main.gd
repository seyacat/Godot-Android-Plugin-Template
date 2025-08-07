extends Node2D

# TODO: Update to match your plugin's name
var _plugin_name = "GodotAndroidPluginTemplate"
var _android_plugin

func _ready():
	get_viewport().transparent_bg = true
	RenderingServer.set_default_clear_color(Color(0, 0, 0, 0))
	DisplayServer.window_set_flag(DisplayServer.WINDOW_FLAG_TRANSPARENT, true)
	if Engine.has_singleton(_plugin_name):
		_android_plugin = Engine.get_singleton(_plugin_name)
		await get_tree().create_timer(2.0).timeout # Esperar inicialización
		_android_plugin.createOverlayView()
	else:
		printerr("Couldn't find plugin " + _plugin_name)

func _on_Button_pressed():
	if _android_plugin:
		# TODO: Update to match your plugin's API
		_android_plugin.helloWorld()
