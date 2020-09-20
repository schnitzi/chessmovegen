package org.computronium.chess.testcaseeditor

import com.sun.corba.se.spi.activation.TCPPortHelper
import java.awt.*
import javax.swing.*

/**
 * Utility for browsing chess starting positions and the resulting set of move positions.
 */
internal class ChessBoardPanel(private var fens : Array<TestCase.TestCasePosition> = arrayOf()) : JPanel() {


    fun setFenList(fens: List<TestCase.TestCasePosition>) {
        this.fens = fens.toTypedArray()
        fenComboBox.removeAllItems()
        fenComboBox.model = DefaultComboBoxModel(this.fens)
    }

    fun selectFEN(selectedIndex: Int) {
        fenComboBox.selectedIndex = selectedIndex
    }

    private var titleLabel = JLabel("")
    internal val fenComboBox = JComboBox<TestCase.TestCasePosition>(fens)
    private val chessboard = ChessBoardViewPanel()

    init {

        fenComboBox.addActionListener {
            chessboard.setBoardState(if (fenComboBox.selectedIndex == -1) null else fens[fenComboBox.selectedIndex])
            titleLabel.text = (fenComboBox.selectedItem as TestCase.TestCasePosition).moveLabel()
        }

        titleLabel = JLabel()

        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(BorderLayout.NORTH, titleLabel)
        mainPanel.add(BorderLayout.CENTER, chessboard)

        layout = BorderLayout()
        add(BorderLayout.NORTH, fenComboBox)
        add(BorderLayout.CENTER, mainPanel)
    }
}
