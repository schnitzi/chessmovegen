package org.computronium.chess.testcaseeditor

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.GridBagLayout
import javax.swing.BoxLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.border.BevelBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * Utility for browsing chess starting positions and the resulting set of move positions.
 */
internal class SidePanel(title : String, private var fens : Array<TestCase.TestCasePosition> = arrayOf()) : JPanel(BorderLayout()) {

    private var titleLabel = JLabel(title, SwingConstants.CENTER)
    private var moveLabel = JLabel("")
    private val descriptionPanel : DescriptionPanel = DescriptionPanel()
    internal val fenComboBox = JComboBox(fens)
    private val chessboardPanel = ChessboardPanel()
    private val metadataPanel = MetadataPanel()
    private val prevButton = JButton("<")
    private val nextButton = JButton(">")

    var otherSidePanel: SidePanel? = null

    init {

        descriptionPanel.description.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent?) {
                saveDescription()
            }

            override fun insertUpdate(e: DocumentEvent?) {
                saveDescription()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                saveDescription()
            }

            fun saveDescription() {
                if (fenComboBox.selectedItem != null) {
                    val position: TestCase.TestCasePosition = fenComboBox.selectedItem as TestCase.TestCasePosition
                    position.description = descriptionPanel.description.text
                }
            }
        })

        titleLabel.font = Font("Arial", Font.BOLD, 24)

        prevButton.addActionListener {
            fenComboBox.selectedIndex = fenComboBox.selectedIndex - 1
        }
        nextButton.addActionListener {
            fenComboBox.selectedIndex = fenComboBox.selectedIndex + 1
        }
        val prevPanel = JPanel(GridBagLayout())
        prevPanel.add(prevButton)
        val nextPanel = JPanel(GridBagLayout())
        nextPanel.add(nextButton)

        fenComboBox.addActionListener {
            val position = selectedFEN()
            chessboardPanel.setPosition(position, otherSidePanel?.fenComboBox?.selectedItem as TestCase.TestCasePosition?)
            metadataPanel.setPosition(position)
            moveLabel.text = if (fenComboBox.selectedItem == null) "" else (fenComboBox.selectedItem as TestCase.TestCasePosition).moveLabel()
            descriptionPanel.description.text = position?.description ?: ""
            prevButton.isEnabled = fenComboBox.selectedIndex > 0
            nextButton.isEnabled = fenComboBox.selectedIndex < fenComboBox.itemCount - 1
        }

        metadataPanel.minimumSize = Dimension(0, 300)

        titleLabel.alignmentX = Component.CENTER_ALIGNMENT
        moveLabel.alignmentX = Component.CENTER_ALIGNMENT

        val northPanel = JPanel()
        northPanel.layout = BoxLayout(northPanel, BoxLayout.PAGE_AXIS)
        northPanel.add(titleLabel)
        northPanel.add(fenComboBox)

        val southPanel = JPanel()
        southPanel.layout = BoxLayout(southPanel, BoxLayout.PAGE_AXIS)
        southPanel.add(moveLabel)
        southPanel.add(descriptionPanel)
        southPanel.add(metadataPanel)

        add(BorderLayout.NORTH, northPanel)
        add(BorderLayout.CENTER, chessboardPanel)
        add(BorderLayout.SOUTH, southPanel)
        add(BorderLayout.WEST, prevPanel)
        add(BorderLayout.EAST, nextPanel)

        border = BevelBorder(BevelBorder.LOWERED, Color.GRAY.brighter(), Color.GRAY, Color.GRAY.darker(), Color.GRAY)
    }

    internal fun selectedFEN() = if (fenComboBox.selectedIndex == -1) null else fens[fenComboBox.selectedIndex]

    fun setFenList(fens: List<TestCase.TestCasePosition>) {
        this.fens = fens.toTypedArray()
        fenComboBox.removeAllItems()
        fenComboBox.model = DefaultComboBoxModel(this.fens)
    }

    fun selectFEN(selectedIndex: Int) {
        fenComboBox.selectedIndex = selectedIndex
    }
}
