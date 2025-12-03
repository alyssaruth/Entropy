package strategy

import com.fasterxml.jackson.module.kotlin.readValue
import preference.PreferenceSetting
import preference.getPreference
import settings.Setting
import util.ClientGlobals.preferenceStore
import util.CpuStrategies
import utils.CoreGlobals

fun getApiStrategiesFromPreferences(): List<ApiStrategy> {
    val apiStrategyJson = getPreference(PreferenceSetting.ApiStrategies)
    if (apiStrategyJson.isEmpty()) {
        return emptyList()
    }

    return CoreGlobals.jsonMapper.readValue<List<ApiStrategy>>(apiStrategyJson)
}

fun saveApiStrategiesToPreference(strategies: List<ApiStrategy>) {
    preferenceStore.save(
        PreferenceSetting.ApiStrategies,
        CoreGlobals.jsonMapper.writeValueAsString(strategies),
    )
}

fun saveStrategyErrorAndUnsetStrategies(strategy: ApiStrategy, error: String?) {
    // Something has gone wrong, so save the API strategy as disabled
    val name = strategy.name
    val apiStrategies = getApiStrategiesFromPreferences()
    val updated =
        apiStrategies.map { prefStrategy ->
            if (strategy.name == prefStrategy.name) {
                prefStrategy.copy(lastError = error)
            } else {
                prefStrategy
            }
        }

    saveApiStrategiesToPreference(updated)

    resetCpuStrategy(PreferenceSetting.OpponentOneStrategy, name)
    resetCpuStrategy(PreferenceSetting.OpponentTwoStrategy, name)
    resetCpuStrategy(PreferenceSetting.OpponentThreeStrategy, name)
}

private fun resetCpuStrategy(strategySetting: Setting<String>, apiName: String) {
    val strategy = getPreference(strategySetting)
    if (strategy == "API: $apiName") {
        preferenceStore.save(strategySetting, CpuStrategies.STRATEGY_BASIC)
    }
}
