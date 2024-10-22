package preference

import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

class InMemorySettingStore : AbstractSettingStore() {
    private val dummyNode = Preferences.userRoot().node("dummyNode")
    private val hmPreferences = mutableMapOf<String, String>()
    private val listeners = mutableListOf<PreferenceChangeListener>()

    override fun addPreferenceChangeListener(listener: PreferenceChangeListener) {
        listeners.add(listener)
    }

    override fun <T : Any> delete(setting: Setting<T>) {
        hmPreferences.remove(setting.name)
        listeners.forEach {
            it.preferenceChange(PreferenceChangeEvent(dummyNode, setting.name, null))
        }
    }

    override fun <T> findRaw(setting: Setting<T>) = hmPreferences[setting.name]

    override fun <T : Any> saveRaw(setting: Setting<T>, value: String) {
        hmPreferences[setting.name] = value

        listeners.forEach {
            it.preferenceChange(PreferenceChangeEvent(dummyNode, setting.name, value))
        }
    }
}
