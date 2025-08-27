package screen.preference

import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.io.File
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JRadioButton
import javax.swing.JSeparator
import javax.swing.JSpinner
import javax.swing.JTextField
import javax.swing.SpinnerNumberModel
import javax.swing.SwingConstants
import preference.GAME_SPEED_FAST
import preference.GAME_SPEED_MEDIUM
import preference.GAME_SPEED_SLOW
import preference.PreferenceSetting
import preference.getPreference
import util.ClientGlobals.preferenceStore
import util.DialogUtilNew
import util.ReplayFileUtil
import utils.CoreGlobals.logger

class PreferencesPanelMisc(parent: PreferencesDialog) :
    AbstractPreferencesPanel(parent), ItemListener {
    private var autosave = false
    private var gameSpeed: Int = GAME_SPEED_MEDIUM
    private var saveReplays = false
    private var replayDirectory = ""
    private var openReplayOnFirstRound = false
    private var autoStartNextRound = false
    private var autoStartSeconds = 2 // 2 seconds
    private var popUpRoomsOnline = true
    private var checkForUpdates = true

    private val lblOtherOptions = JLabel("Other Options")
    private val separator_3 = JSeparator()
    private val fc = JFileChooser()
    private val chosenDirectory = JTextField()
    private val chckbxSaveReplays = JCheckBox("Save replays")
    private val btnSelectDirectory = JButton("...")
    private val chckbxAutosave = JCheckBox("Autosave on exit")
    private val bgSpeed = ButtonGroup()
    private val rdbtnSlow = JRadioButton("Slow")
    private val rdbtnMedium = JRadioButton("Medium")
    private val rdbtnFast = JRadioButton("Fast")
    private val lblGameSpeed = JLabel("Game speed")
    private val chckbxAutoStartNextRound = JCheckBox("Automatically start next round after")
    private val chckbxPopUpRooms = JCheckBox("Pop up online rooms on my turn")
    private val spinnerAutoStartSeconds = JSpinner()
    private val lblSeconds = JLabel("seconds")
    private val lblReplayViewerDefaults = JLabel("Replay viewer default")
    private val bgReplayRound = ButtonGroup()
    private val rdbtnFirstRound = JRadioButton("First Round")
    private val rdbtnLastRound = JRadioButton("Last Round")
    private val chckbxCheckForUpdates = JCheckBox("Automatically check for updates")

    init {
        setLayout(null)

        bgReplayRound.add(rdbtnFirstRound)
        bgReplayRound.add(rdbtnLastRound)
        bgSpeed.add(rdbtnSlow)
        bgSpeed.add(rdbtnMedium)
        bgSpeed.add(rdbtnFast)
        chckbxSaveReplays.setFont(Font("Tahoma", Font.PLAIN, 11))
        chckbxSaveReplays.setBounds(20, 67, 91, 22)
        add(chckbxSaveReplays)
        lblOtherOptions.setFont(Font("Tahoma", Font.PLAIN, 14))
        lblOtherOptions.setBounds(171, 12, 86, 17)
        add(lblOtherOptions)
        separator_3.setBounds(0, 38, 429, 2)
        add(separator_3)
        chosenDirectory.setBounds(116, 67, 180, 22)
        chosenDirectory.setEditable(false)
        add(chosenDirectory)
        chosenDirectory.setColumns(10)
        btnSelectDirectory.setBounds(296, 67, 20, 22)
        add(btnSelectDirectory)
        chckbxAutosave.setBounds(20, 180, 127, 22)
        add(chckbxAutosave)
        chckbxAutosave.setFont(Font("Tahoma", Font.PLAIN, 11))
        lblReplayViewerDefaults.setFont(Font("Tahoma", Font.PLAIN, 11))
        lblReplayViewerDefaults.setBounds(20, 112, 127, 14)
        add(lblReplayViewerDefaults)
        rdbtnFirstRound.setFont(Font("Tahoma", Font.PLAIN, 11))
        rdbtnFirstRound.setBounds(144, 109, 91, 20)
        add(rdbtnFirstRound)
        rdbtnLastRound.setFont(Font("Tahoma", Font.PLAIN, 11))
        rdbtnLastRound.setBounds(237, 109, 91, 20)
        add(rdbtnLastRound)
        lblGameSpeed.setBounds(20, 148, 86, 14)
        add(lblGameSpeed)
        lblGameSpeed.setHorizontalAlignment(SwingConstants.LEFT)
        lblGameSpeed.setFont(Font("Tahoma", Font.PLAIN, 11))
        rdbtnSlow.setBounds(144, 144, 60, 20)
        add(rdbtnSlow)
        rdbtnSlow.setFont(Font("Tahoma", Font.PLAIN, 11))
        rdbtnMedium.setBounds(206, 144, 70, 20)
        add(rdbtnMedium)
        rdbtnMedium.setFont(Font("Tahoma", Font.PLAIN, 11))
        rdbtnFast.setBounds(274, 144, 54, 20)
        add(rdbtnFast)
        rdbtnFast.setFont(Font("Tahoma", Font.PLAIN, 11))
        chckbxAutoStartNextRound.setFont(Font("Tahoma", Font.PLAIN, 11))
        chckbxAutoStartNextRound.setBounds(20, 205, 208, 23)
        add(chckbxAutoStartNextRound)
        chckbxPopUpRooms.setFont(Font("Tahoma", Font.PLAIN, 11))
        chckbxPopUpRooms.setBounds(20, 230, 208, 23)
        add(chckbxPopUpRooms)
        spinnerAutoStartSeconds.setBounds(228, 205, 38, 22)
        add(spinnerAutoStartSeconds)
        lblSeconds.setFont(Font("Tahoma", Font.PLAIN, 11))
        lblSeconds.setBounds(275, 206, 55, 20)
        add(lblSeconds)
        chckbxCheckForUpdates.setFont(Font("Tahoma", Font.PLAIN, 11))
        chckbxCheckForUpdates.setBounds(20, 256, 237, 23)
        add(chckbxCheckForUpdates)
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)

        rdbtnFast.addActionListener(this)
        rdbtnMedium.addActionListener(this)
        rdbtnSlow.addActionListener(this)
        btnSelectDirectory.addActionListener(this)
        chckbxSaveReplays.addItemListener(this)
        chckbxAutoStartNextRound.addItemListener(this)
    }

    /** Abstract methods */
    override fun initVariables() {
        getVariablesFromPrefs()

        spinnerAutoStartSeconds.setModel(SpinnerNumberModel(autoStartSeconds, 1, 5, 1))
        chosenDirectory.text = "$replayDirectory\\Replays"
        chckbxAutosave.setSelected(autosave)
        rdbtnSlow.setSelected(gameSpeed == GAME_SPEED_SLOW)
        rdbtnMedium.setSelected(gameSpeed == GAME_SPEED_MEDIUM)
        rdbtnFast.setSelected(gameSpeed == GAME_SPEED_FAST)
        chckbxSaveReplays.setSelected(saveReplays)
        chosenDirectory.text = "$replayDirectory\\Replays"
        chosenDirectory.setEnabled(saveReplays)
        btnSelectDirectory.setEnabled(saveReplays)
        chckbxAutoStartNextRound.setSelected(autoStartNextRound)
        chckbxPopUpRooms.setSelected(popUpRoomsOnline)
        chckbxCheckForUpdates.setSelected(checkForUpdates)
        spinnerAutoStartSeconds.setEnabled(autoStartNextRound)
        lblSeconds.setEnabled(autoStartNextRound)
        spinnerAutoStartSeconds.setValue(autoStartSeconds)
        rdbtnFirstRound.setSelected(openReplayOnFirstRound)
        rdbtnLastRound.setSelected(!openReplayOnFirstRound)
    }

    override fun valid(): Boolean {
        return confirmChangeOfDirectory()
    }

    override fun savePreferences() {
        autosave = chckbxAutosave.isSelected
        openReplayOnFirstRound = rdbtnFirstRound.isSelected
        autoStartSeconds = spinnerAutoStartSeconds.value as Int
        popUpRoomsOnline = chckbxPopUpRooms.isSelected
        checkForUpdates = chckbxCheckForUpdates.isSelected

        preferenceStore.save(PreferenceSetting.SaveReplays, saveReplays)
        preferenceStore.save(PreferenceSetting.AutoSave, autosave)
        preferenceStore.save(PreferenceSetting.ReplayDirectory, replayDirectory)
        preferenceStore.save(PreferenceSetting.OpenReplayOnFirstRound, openReplayOnFirstRound)
        preferenceStore.save(PreferenceSetting.AutoStartNextRound, autoStartNextRound)
        preferenceStore.save(PreferenceSetting.AutoStartSeconds, autoStartSeconds)
        preferenceStore.save(PreferenceSetting.PopUpRooms, popUpRoomsOnline)
        preferenceStore.save(PreferenceSetting.CheckForUpdates, checkForUpdates)
        preferenceStore.save(PreferenceSetting.GameSpeed, gameSpeed)
    }

    private fun getVariablesFromPrefs() {
        autosave = getPreference(PreferenceSetting.AutoSave)
        gameSpeed = getPreference(PreferenceSetting.GameSpeed)
        saveReplays = getPreference(PreferenceSetting.SaveReplays)
        replayDirectory = getPreference(PreferenceSetting.ReplayDirectory)!!
        openReplayOnFirstRound = getPreference(PreferenceSetting.OpenReplayOnFirstRound)
        autoStartNextRound = getPreference(PreferenceSetting.AutoStartNextRound)
        autoStartSeconds = getPreference(PreferenceSetting.AutoStartSeconds)
        popUpRoomsOnline = getPreference(PreferenceSetting.PopUpRooms)
        checkForUpdates = getPreference(PreferenceSetting.CheckForUpdates)
    }

    private fun confirmChangeOfDirectory(): Boolean {
        val originalReplayDirectory: String = preferenceStore.get(PreferenceSetting.ReplayDirectory)

        val myExistingFiles =
            File(originalReplayDirectory + "//Replays//" + ReplayFileUtil.FOLDER_PERSONAL_REPLAYS)
                .listFiles()
        var myExistingFilesLength = 0
        if (myExistingFiles != null) {
            myExistingFilesLength = myExistingFiles.size
        }

        val importedExistingFiles =
            File(originalReplayDirectory + "//Replays//" + ReplayFileUtil.FOLDER_IMPORTED_REPLAYS)
                .listFiles()
        var importedExistingFilesLength = 0
        if (importedExistingFiles != null) {
            importedExistingFilesLength = importedExistingFiles.size
        }

        if (
            replayDirectory != originalReplayDirectory &&
                (myExistingFilesLength != 0 || importedExistingFilesLength != 0)
        ) {
            val choice =
                DialogUtilNew.showQuestion(
                    "You have changed your replay directory but there are still files in the old one. " +
                        "\nWould you like to move these files to the new location?",
                    true,
                )

            if (choice == JOptionPane.YES_OPTION) {
                ReplayFileUtil.moveReplays(
                    myExistingFiles,
                    importedExistingFiles,
                    originalReplayDirectory,
                    replayDirectory,
                )
            } else if (choice == JOptionPane.CANCEL_OPTION) {
                return false
            } else {
                DialogUtilNew.showInfo(
                    "Existing replay files were left in the old directory and will have to be deleted or moved manually."
                )
            }
        }

        return true
    }

    private fun selectReplayDirectory() {
        val returnVal = fc.showOpenDialog(this)
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            val file = fc.getSelectedFile()

            replayDirectory = file.getPath()
            chosenDirectory.setText("$replayDirectory\\Replays")

            logger.info("replay.dir", "Selected ${file.path} as replay directory")
        } else {
            logger.info("replay.dir", "Cancelled directory selection")
        }
    }

    override fun actionPerformed(arg0: ActionEvent) {
        val source = arg0.getSource()
        if (source === rdbtnFast) {
            gameSpeed = GAME_SPEED_FAST
        } else if (source === rdbtnMedium) {
            gameSpeed = GAME_SPEED_MEDIUM
        } else if (source === rdbtnSlow) {
            gameSpeed = GAME_SPEED_SLOW
        } else if (source === btnSelectDirectory) {
            selectReplayDirectory()
        }
    }

    override fun itemStateChanged(arg0: ItemEvent) {
        val source = arg0.getSource()
        if (source === chckbxSaveReplays) {
            saveReplays = chckbxSaveReplays.isSelected()
            chosenDirectory.setEnabled(saveReplays)
            btnSelectDirectory.setEnabled(saveReplays)
        } else if (source === chckbxAutoStartNextRound) {
            autoStartNextRound = chckbxAutoStartNextRound.isSelected()
            lblSeconds.setEnabled(autoStartNextRound)
            spinnerAutoStartSeconds.setEnabled(autoStartNextRound)
        }
    }
}
