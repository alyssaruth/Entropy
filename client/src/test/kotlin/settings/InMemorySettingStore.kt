package settings

class InMemorySettingStore : AbstractSettingStore() {
    private val hmPreferences = mutableMapOf<String, String>()

    override fun clear() {
        hmPreferences.clear()
    }

    override fun <T : Any> deleteImpl(setting: Setting<T>) {
        hmPreferences.remove(setting.name)
    }

    override fun <T> findRaw(setting: Setting<T>) = hmPreferences[setting.name]

    override fun <T : Any> saveRaw(setting: Setting<T>, value: String) {
        hmPreferences[setting.name] = value
    }
}
