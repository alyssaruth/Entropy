package screen.preference

import com.github.alyssaburlton.swingtest.clickChild
import com.github.alyssaburlton.swingtest.getChild
import com.github.alyssaburlton.swingtest.shouldBeDisabled
import com.github.alyssaburlton.swingtest.shouldBeEnabled
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JRadioButton
import javax.swing.JSpinner
import javax.swing.JTextField
import org.junit.jupiter.api.Test
import preference.GAME_SPEED_FAST
import preference.PreferenceSetting
import util.AbstractClientTest
import util.ClientGlobals.preferenceStore

class PreferencesPanelMiscTest : AbstractClientTest() {
    @Test
    fun `Should load existing preferences`() {
        preferenceStore.save(PreferenceSetting.SaveReplays, true)
        preferenceStore.save(PreferenceSetting.AutoSave, true)
        preferenceStore.save(PreferenceSetting.ReplayDirectory, "/foo/bar")
        preferenceStore.save(PreferenceSetting.OpenReplayOnFirstRound, true)
        preferenceStore.save(PreferenceSetting.AutoStartNextRound, true)
        preferenceStore.save(PreferenceSetting.AutoStartSeconds, 4)
        preferenceStore.save(PreferenceSetting.PopUpRooms, true)
        preferenceStore.save(PreferenceSetting.CheckForUpdates, false)
        preferenceStore.save(PreferenceSetting.GameSpeed, GAME_SPEED_FAST)

        val panel = PreferencesPanelMisc(mockk(relaxed = true))
        panel.initVariables()

        panel.getChild<JCheckBox>(text = "Save replays").isSelected shouldBe true
        panel.getChild<JCheckBox>(text = "Autosave on exit").isSelected shouldBe true
        panel.getChild<JTextField>("replayDirectory").shouldBeEnabled()
        panel.getChild<JTextField>("replayDirectory").text shouldBe "/foo/bar\\Replays"

        panel.getChild<JRadioButton>(text = "First Round").isSelected shouldBe true
        panel.getChild<JRadioButton>(text = "Last Round").isSelected shouldBe false
        panel.getChild<JCheckBox>(text = "Automatically start next round after").isSelected shouldBe
            true

        panel.getChild<JSpinner>().value shouldBe 4

        panel.getChild<JCheckBox>(text = "Pop up online rooms on my turn").isSelected shouldBe true
        panel.getChild<JCheckBox>(text = "Automatically check for updates").isSelected shouldBe
            false

        panel.getChild<JRadioButton>(text = "Slow").isSelected shouldBe false
        panel.getChild<JRadioButton>(text = "Medium").isSelected shouldBe false
        panel.getChild<JRadioButton>(text = "Fast").isSelected shouldBe true
    }

    @Test
    fun `Ticking and unticking save replays should toggle related components`() {
        val panel = PreferencesPanelMisc(mockk(relaxed = true))
        panel.initVariables()

        panel.getChild<JCheckBox>(text = "Save replays").isSelected shouldBe false
        panel.getChild<JTextField>("replayDirectory").shouldBeDisabled()
        panel.getChild<JButton>(text = "...").shouldBeDisabled()

        panel.clickChild<JCheckBox>(text = "Save replays")
        panel.getChild<JTextField>("replayDirectory").shouldBeEnabled()
        panel.getChild<JButton>(text = "...").shouldBeEnabled()

        panel.clickChild<JCheckBox>(text = "Save replays")
        panel.getChild<JTextField>("replayDirectory").shouldBeDisabled()
        panel.getChild<JButton>(text = "...").shouldBeDisabled()
    }
}
