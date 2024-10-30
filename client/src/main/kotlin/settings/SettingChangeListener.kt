package settings

interface SettingChangeListener {
    fun <T> settingChanged(setting: Setting<T>, newValue: T?)
}
