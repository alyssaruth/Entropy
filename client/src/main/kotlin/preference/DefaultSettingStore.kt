package preference

import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

class DefaultSettingStore(nodeName: String) : AbstractSettingStore() {
    private val preferences = Preferences.userRoot().node(nodeName)

    override fun clear() {
        preferences.clear()
    }

    override fun addPreferenceChangeListener(listener: PreferenceChangeListener) {
        preferences.addPreferenceChangeListener(listener)
    }

    override fun <T : Any> delete(setting: Setting<T>) {
        preferences.remove(setting.name)
    }

    override fun <T : Any> saveRaw(setting: Setting<T>, value: String) {
        preferences.put(setting.name, value)
    }

    override fun <T> findRaw(setting: Setting<T>): String? = preferences.get(setting.name, null)
}
