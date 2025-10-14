package screen.preference

import achievement.Reward
import bean.ComboBoxItem
import com.github.alyssaburlton.swingtest.getChild
import com.github.alyssaburlton.swingtest.shouldBeDisabled
import com.github.alyssaburlton.swingtest.shouldBeEnabled
import com.github.alyssaburlton.swingtest.shouldMatch
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import javax.swing.ImageIcon
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JRadioButton
import org.junit.jupiter.api.Test
import preference.PreferenceSetting
import util.AbstractClientTest
import util.ClientGlobals.preferenceStore
import utils.items

class PreferencesPanelAppearanceTest : AbstractClientTest() {
    @Test
    fun `Should only show unlocked card backs`() {
        Reward.FourColours.unlock()

        val panel = openPreferencePanel()

        val comboBox = panel.getChild<JComboBox<*>>("backs")
        comboBox
            .items()
            .shouldContainExactly(
                ComboBoxItem("backBlue", "Blue", true),
                ComboBoxItem("backRed", "Red", true),
                ComboBoxItem("backGreen", "Green", true),
                ComboBoxItem("", "10 achievements to unlock", false),
                ComboBoxItem("", "15 achievements to unlock", false),
                ComboBoxItem("", "20 achievements to unlock", false),
                ComboBoxItem("", "25 achievements to unlock", false),
                ComboBoxItem("", "30 achievements to unlock", false),
                ComboBoxItem("", "35 achievements to unlock", false),
                ComboBoxItem("", "40 achievements to unlock", false),
                ComboBoxItem("", "45 achievements to unlock", false),
                ComboBoxItem("", "50 achievements to unlock", false),
            )

        // Can't select a disabled entry
        comboBox.selectedIndex = 3
        comboBox.selectedIndex shouldBe 0

        // Can select an enabled entry
        comboBox.selectedIndex = 2
        comboBox.selectedIndex shouldBe 2
    }

    @Test
    fun `Backs combo box should update preview and saved preference`() {
        Reward.FourColours.unlock()
        val panel = openPreferencePanel()
        val comboBox = panel.getChild<JComboBox<*>>("backs")

        comboBox.selectedIndex = 1
        panel.backPreviewIcon().shouldMatch(ImageIcon(javaClass.getResource("/backs/backRed.png")))

        comboBox.selectedIndex = 2
        panel
            .backPreviewIcon()
            .shouldMatch(ImageIcon(javaClass.getResource("/backs/backGreen.png")))

        panel.savePreferences()
        preferenceStore.get(PreferenceSetting.CardBacks) shouldBe "backGreen"
    }

    @Test
    fun `Backs combo box should load correct selection from preference`() {
        preferenceStore.save(PreferenceSetting.CardBacks, "backRed")

        val panel = openPreferencePanel()
        val comboBox = panel.getChild<JComboBox<*>>("backs")

        comboBox.selectedItem shouldBe ComboBoxItem("backRed", "Red", true)
    }

    @Test
    fun `Backs combo box should revert to Blue if value stored in preference is no longer unlocked (eg due to wiped data)`() {
        preferenceStore.save(PreferenceSetting.CardBacks, "backGreen")

        val panel = openPreferencePanel()
        val comboBox = panel.getChild<JComboBox<*>>("backs")

        comboBox.selectedItem shouldBe ComboBoxItem("backBlue", "Blue", true)
    }

    @Test
    fun `4 colour checkbox should be locked or unlocked as appropriate`() {
        val panel = openPreferencePanel()

        val checkbox = panel.getChild<JCheckBox>("FourColourCheckbox")
        checkbox.shouldBeDisabled()
        checkbox.isSelected shouldBe false
        checkbox.text shouldBe "Locked"
        checkbox.toolTipText shouldBe "Unlock at 5 achievements"

        Reward.FourColours.unlock()
        val panel2 = openPreferencePanel()
        val checkbox2 = panel2.getChild<JCheckBox>("FourColourCheckbox")

        checkbox2.shouldBeEnabled()
        checkbox2.text shouldBe "Use 4 colour deck"
        checkbox2.toolTipText shouldBe null
    }

    @Test
    fun `Minimalist deck design radio button should be locked or unlocked as appropriate`() {
        val panel = openPreferencePanel()

        val rdbtn = panel.getChild<JRadioButton>("MinimalistDesignRadio")
        rdbtn.shouldBeDisabled()
        rdbtn.isSelected shouldBe false
        rdbtn.text shouldBe "Locked"
        rdbtn.toolTipText shouldBe "Unlock at 20 achievements"

        Reward.MinimalistDeck.unlock()
        val panel2 = openPreferencePanel()
        val rdbtn2 = panel2.getChild<JRadioButton>("MinimalistDesignRadio")

        rdbtn2.shouldBeEnabled()
        rdbtn2.text shouldBe "Minimalist"
        rdbtn2.toolTipText shouldBe null
    }

    @Test
    fun `Developer jokers design radio button should be locked or unlocked as appropriate`() {
        val panel = openPreferencePanel()

        val rdbtn = panel.getChild<JRadioButton>("DeveloperJokersRadio")
        rdbtn.shouldBeDisabled()
        rdbtn.isSelected shouldBe false
        rdbtn.text shouldBe "Locked"
        rdbtn.toolTipText shouldBe "Unlock at 45 achievements"

        Reward.DeveloperSet.unlock()
        val panel2 = openPreferencePanel()
        val rdbtn2 = panel2.getChild<JRadioButton>("DeveloperJokersRadio")

        rdbtn2.shouldBeEnabled()
        rdbtn2.text shouldBe "Developers"
        rdbtn2.toolTipText shouldBe null
    }

    private fun PreferencesPanelAppearance.backPreviewIcon(): ImageIcon =
        getChild<JLabel>("BackPreview").icon as ImageIcon

    private fun openPreferencePanel(): PreferencesPanelAppearance {
        return PreferencesPanelAppearance(mockk(relaxed = true)).also { it.initVariables() }
    }
}
