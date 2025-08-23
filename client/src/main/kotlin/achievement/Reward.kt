package achievement

import java.net.URL
import settings.Setting
import util.ClientGlobals

enum class Reward(val settingName: String, val threshold: Int) {
    FourColours("fourColours", 5),
    NegativeJacks("negativeJacks", 10),
    Blind("blind", 15),
    MinimalistDeck("minimalist", 20),
    Vectropy("vectropy", 25),
    CardReveal("cardReveal", 30),
    ExtraSuits("extraSuits", 35),
    Illegal("illegal", 40),
    DeveloperSet("developerSet", 45),
    Cheats("cheats", 50);

    private val setting: Setting<Boolean> = Setting(settingName, false)

    fun isUnlocked() = ClientGlobals.rewardStore.get(setting)

    fun unlock() {
        ClientGlobals.rewardStore.save(setting, true)
    }

    fun getResource(): URL? = javaClass.getResource("/rewards/$settingName.png")
}
