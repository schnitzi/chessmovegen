package org.computronium.chess.testcaseeditor

import java.awt.BorderLayout
import java.awt.Font
import javax.swing.DefaultComboBoxModel
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
    private val chessboard = ChessBoardViewPanel()

    init {

        fenComboBox.addActionListener {
            chessboard.setBoardState(if (fenComboBox.selectedIndex == -1) null else fens[fenComboBox.selectedIndex])
            titleLabel.text = if (fenComboBox.selectedItem == null) "" else (fenComboBox.selectedItem as TestCase.TestCasePosition).moveLabel()
        }

        titleLabel = JLabel("", SwingConstants.CENTER)
        titleLabel.font = Font("Arial", Font.BOLD, 24)

        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(BorderLayout.NORTH, titleLabel)
        mainPanel.add(BorderLayout.CENTER, chessboard)

        layout = BorderLayout()
        add(BorderLayout.NORTH, fenComboBox)
        add(BorderLayout.CENTER, mainPanel)
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
