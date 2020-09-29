package org.computronium.chess.testcaseeditor

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.GridBagLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.SwingConstants
import javax.swing.border.BevelBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * Utility for browsing chess starting positions and the resulting set of move positions.
 */
internal class SidePanel(private var fens : Array<TestCase.TestCasePosition> = arrayOf()) : JPanel(BorderLayout()) {

    private var titleLabel = JLabel("")
    private val description = JTextArea()
    internal val fenComboBox = JComboBox<TestCase.TestCasePosition>(fens)
    private val chessboardPanel = ChessboardPanel()
    private val metadataPanel = DescriptionPanel()
    private val prevButton = JButton("<")
    private val nextButton = JButton(">")

    var otherSidePanel: SidePanel? = null

    init {

        description.document.addDocumentListener(object : DocumentListener {
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
                val position: TestCase.TestCasePosition = fenComboBox.selectedItem as TestCase.TestCasePosition
                position.description = description.text
            }
        })

        titleLabel = JLabel("", SwingConstants.CENTER)
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

        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(BorderLayout.NORTH, titleLabel)
        mainPanel.add(BorderLayout.CENTER, chessboardPanel)

        fenComboBox.addActionListener {
            val position = if (fenComboBox.selectedIndex == -1) null else fens[fenComboBox.selectedIndex]
            chessboardPanel.setPosition(position, otherSidePanel?.fenComboBox?.selectedItem as TestCase.TestCasePosition?)
            metadataPanel.setPosition(position)
            titleLabel.text = if (fenComboBox.selectedItem == null) "" else (fenComboBox.selectedItem as TestCase.TestCasePosition).moveLabel()
            prevButton.isEnabled = fenComboBox.selectedIndex > 0
            nextButton.isEnabled = fenComboBox.selectedIndex < fenComboBox.itemCount - 1
        }

        val southPanel = JPanel(BorderLayout())
        southPanel.add(BorderLayout.NORTH, fenComboBox)
        southPanel.add(BorderLayout.CENTER, metadataPanel)

        add(BorderLayout.NORTH, description)
        add(BorderLayout.CENTER, mainPanel)
        add(BorderLayout.SOUTH, southPanel)
        add(BorderLayout.WEST, prevPanel)
        add(BorderLayout.EAST, nextPanel)

        border = BevelBorder(BevelBorder.LOWERED, Color.GRAY.brighter(), Color.GRAY, Color.GRAY.darker(), Color.GRAY)
    }

    fun setFenList(fens: List<TestCase.TestCasePosition>) {
        this.fens = fens.toTypedArray()
        fenComboBox.removeAllItems()
        fenComboBox.model = DefaultComboBoxModel(this.fens)
    }

    fun selectFEN(selectedIndex: Int) {
        fenComboBox.selectedIndex = selectedIndex
    }
}
