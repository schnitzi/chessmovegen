package org.computronium.chess.testcaseeditor

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Font
import java.awt.GridBagLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

/**
 * Utility for browsing chess starting positions and the resulting set of move positions.
 */
internal class SidePanel(private var fens : Array<TestCase.TestCasePosition> = arrayOf()) : JPanel() {

    private var titleLabel = JLabel("")
    internal val fenComboBox = JComboBox<TestCase.TestCasePosition>(fens)
    private val chessboardPanel = ChessboardPanel()
    private val descriptionPanel = DescriptionPanel()
    private val prevButton = JButton("<")
    private val nextButton = JButton(">")

    var otherSidePanel: SidePanel? = null

    init {

        fenComboBox.addActionListener {
            val position = if (fenComboBox.selectedIndex == -1) null else fens[fenComboBox.selectedIndex]
            chessboardPanel.setPosition(position, otherSidePanel?.fenComboBox?.selectedItem as TestCase.TestCasePosition?)
            descriptionPanel.setPosition(position)
            titleLabel.text = if (fenComboBox.selectedItem == null) "" else (fenComboBox.selectedItem as TestCase.TestCasePosition).moveLabel()
            prevButton.isEnabled = fenComboBox.selectedIndex > 0
            nextButton.isEnabled = fenComboBox.selectedIndex < fenComboBox.itemCount - 1
        }

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

        layout = BorderLayout()
        add(BorderLayout.NORTH, fenComboBox)
        add(BorderLayout.CENTER, mainPanel)
        add(BorderLayout.SOUTH, descriptionPanel)
        add(BorderLayout.WEST, prevPanel)
        add(BorderLayout.EAST, nextPanel)
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
