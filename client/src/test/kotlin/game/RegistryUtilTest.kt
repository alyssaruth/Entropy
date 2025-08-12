package game

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.util.prefs.Preferences
import javax.swing.DefaultListModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import util.AbstractClientTest

class RegistryUtilTest : AbstractClientTest() {
    private val testNode = Preferences.userRoot().node("RegistryUtilTest")

    @BeforeEach
    fun setup() {
        testNode.clear()
    }

    @Test
    fun `Should clear existing entries when populating`() {
        val listmodel = DefaultListModel<PlayerAction>()
        listmodel.add(0, EntropyBidAction("Alyssa", false, 1, Suit.Spades))

        populateActions(testNode, listmodel, null)

        listmodel.elements().toList().shouldBeEmpty()
    }

    @Test
    fun `Write and read actions - no round number`() {
        val listmodel = DefaultListModel<PlayerAction>()
        listmodel.add(0, EntropyBidAction("Alyssa", false, 2, Suit.Clubs))
        listmodel.add(0, EntropyBidAction("David", false, 2, Suit.Spades))
        listmodel.add(0, ChallengeAction("Alyssa", false))

        writeActions(testNode, listmodel, null)

        val newModel = DefaultListModel<PlayerAction>()
        populateActions(testNode, newModel, null)

        newModel.shouldMatch(listmodel)
    }

    @Test
    fun `Write and read actions - with round number`() {
        val roundOne = DefaultListModel<PlayerAction>()
        roundOne.add(0, EntropyBidAction("David", false, 5, Suit.Spades))
        roundOne.add(0, IllegalAction("Alyssa", false))

        val roundTwo = DefaultListModel<PlayerAction>()
        roundTwo.add(0, EntropyBidAction("David", false, 2, Suit.Clubs))
        roundTwo.add(0, EntropyBidAction("Alyssa", false, 3, Suit.Diamonds))
        roundTwo.add(0, ChallengeAction("David", false))

        writeActions(testNode, roundOne, 1)
        writeActions(testNode, roundTwo, 2)

        val newModel = DefaultListModel<PlayerAction>()
        populateActions(testNode, newModel, 1)
        newModel.shouldMatch(roundOne)

        populateActions(testNode, newModel, 2)
        newModel.shouldMatch(roundTwo)
    }

    private fun DefaultListModel<PlayerAction>.shouldMatch(other: DefaultListModel<PlayerAction>) {
        elements().toList() shouldBe other.elements().toList()
    }
}
