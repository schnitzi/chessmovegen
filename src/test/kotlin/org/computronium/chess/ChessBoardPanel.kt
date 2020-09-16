package org.computronium.chess

import java.awt.*
import javax.swing.*

/**
 * Utility for browsing chess starting positions and the resulting set of move positions.
 */
internal class ChessBoardPanel(private var fens: Array<String>) : JPanel() {


    fun setFenList(expected: Set<String>) {
        fens = expected.toTypedArray()
        fenComboBox.removeAllItems()
        fenComboBox.selectedIndex = -1
        fenComboBox.model = DefaultComboBoxModel(fens)
        fenComboBox.selectedIndex = 0
    }

    internal val fenComboBox = JComboBox<String>(fens)
    private val chessboard = ChessBoardViewPanel()

    init {

        fenComboBox.addActionListener {
            chessboard.setBoardState(if (fenComboBox.selectedIndex == -1) null else fens[fenComboBox.selectedIndex])
        }

        layout = BorderLayout()
        add(BorderLayout.NORTH, fenComboBox)
        add(BorderLayout.CENTER, chessboard)
    }
}
