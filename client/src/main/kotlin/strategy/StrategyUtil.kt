package strategy

import bean.ComboBoxItem
import com.fasterxml.jackson.core.type.TypeReference
import game.GameMode
import java.util.UUID
import javax.swing.ComboBoxModel
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import preference.PreferenceSetting
import preference.findJsonPreference
import preference.getPreference
import settings.Setting
import util.ClientGlobals.preferenceStore
import util.CpuStrategies
import util.EntCpuStrategies
import util.VectCpuStrategies
import utils.CoreGlobals
import utils.CoreGlobals.logger

fun getAllStrategies(gameType: GameMode, apiStrategies: List<ApiStrategy>): List<IStrategy> {
    val inBuilt =
        if (gameType == GameMode.Entropy) EntCpuStrategies.getAllStrategies()
        else VectCpuStrategies.getAllStrategies()

    return apiStrategies + inBuilt.map(::InBuiltStrategy)
}

@JvmOverloads
fun getStrategiesComboBoxModel(
    gameMode: GameMode,
    apiStrategies: List<ApiStrategy> = getApiStrategiesFromPreferences(),
): ComboBoxModel<ComboBoxItem<IStrategy>> {
    val strategyArray =
        getAllStrategies(gameMode, apiStrategies)
            .map { strategy -> strategy.toComboBoxItem() }
            .toTypedArray()

    return DefaultComboBoxModel(strategyArray)
}

fun getSelectedStrategy(comboBox: JComboBox<ComboBoxItem<IStrategy>>): IStrategy {
    val item = comboBox.getItemAt(comboBox.selectedIndex)
    return item.hiddenData
}

fun getApiStrategiesFromPreferences() =
    findJsonPreference<List<ApiStrategy>>(PreferenceSetting.ApiStrategies) ?: emptyList()

fun getStrategy(setting: Setting<String>): IStrategy =
    try {
        findJsonPreference<IStrategy>(setting) ?: InBuiltStrategy(CpuStrategies.STRATEGY_BASIC)
    } catch (e: Exception) {
        logger.info(
            "strategy.parseError",
            "Caught $e trying to parse saved strategy. Raw value [${getPreference(setting)}], will revert to Basic.",
        )
        InBuiltStrategy(CpuStrategies.STRATEGY_BASIC)
    }

fun saveApiStrategiesToPreference(strategies: List<ApiStrategy>) {
    val writer = CoreGlobals.jsonMapper.writerFor(object : TypeReference<List<ApiStrategy>>() {})

    preferenceStore.save(PreferenceSetting.ApiStrategies, writer.writeValueAsString(strategies))
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
