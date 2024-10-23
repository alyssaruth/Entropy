package preference

import java.util.prefs.PreferenceChangeListener

abstract class AbstractSettingStore {
    abstract fun addPreferenceChangeListener(listener: PreferenceChangeListener)

    abstract fun <T : Any> delete(setting: Setting<T>)

    protected abstract fun <T> findRaw(setting: Setting<T>): String?

    protected abstract fun <T : Any> saveRaw(setting: Setting<T>, value: String)

    fun <T : Any> save(setting: Setting<T>, value: T) {
        saveRaw(setting, toRawValue(value))
    }

    fun <T : Any> find(setting: Setting<T>): T? =
        findRaw(setting)?.let { convertFromRaw(setting, it) }

    @JvmOverloads
    fun <T : Any> get(setting: Setting<T>, useDefault: Boolean = false): T {
        if (useDefault) {
            return setting.default
        }

        val raw = findRaw(setting) ?: return setting.default
        return convertFromRaw(setting, raw)
    }

    private fun <T> toRawValue(raw: T) = raw.toString()

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> convertFromRaw(setting: Setting<T>, raw: String) =
        when (val desiredType = setting.default::class) {
            Boolean::class -> raw.toBoolean() as T
            Double::class -> raw.toDouble() as T
            Long::class -> raw.toLong() as T
            Int::class -> raw.toInt() as T
            String::class -> raw as T
            else ->
                throw TypeCastException(
                    "Unhandled type [${desiredType}] for preference ${setting.name}"
                )
        }
}
