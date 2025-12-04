package strategy

import com.fasterxml.jackson.module.kotlin.readValue
import game.GameMode
import java.util.UUID
import preference.PreferenceSetting
import preference.getPreference
import settings.Setting
import util.ClientGlobals.preferenceStore
import util.CpuStrategies
import util.EntCpuStrategies
import util.VectCpuStrategies
import utils.CoreGlobals

fun getAllStrategies(gameType: GameMode, apiStrategies: List<ApiStrategy>): List<IStrategy> {
    val inBuilt =
        if (gameType == GameMode.Entropy) EntCpuStrategies.getAllStrategies()
        else VectCpuStrategies.getAllStrategies()

    return apiStrategies + inBuilt.map(::InBuiltStrategy)
}

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

fun saveStrategyErrorAndUnsetStrategies(id: UUID, error: String?) {
    // Something has gone wrong, so save the API strategy as disabled
    val strategies = getApiStrategiesFromPreferences()
    val strategy = strategies.first { it.id == id }.copy(lastError = error)
    val updated = strategies.update(id, strategy)

    saveApiStrategiesToPreference(updated)

    resetCpuStrategy(PreferenceSetting.OpponentOneStrategy, id)
    resetCpuStrategy(PreferenceSetting.OpponentTwoStrategy, id)
    resetCpuStrategy(PreferenceSetting.OpponentThreeStrategy, id)
}

private fun resetCpuStrategy(strategySetting: Setting<String>, id: UUID) {
    val strategy = getPreference(strategySetting)
    if (strategy.contains(id.toString())) {
        preferenceStore.save(strategySetting, CpuStrategies.STRATEGY_BASIC)
    }
}

fun List<ApiStrategy>.update(id: UUID, newStrategy: ApiStrategy) = map {
    if (it.id == id) newStrategy else it
}
