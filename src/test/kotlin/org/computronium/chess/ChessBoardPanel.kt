package org.computronium.chess

import java.awt.*
import javax.swing.*
import kotlin.math.exp

/**
 * Utility for browsing chess starting positions and the resulting set of move positions.
 */
internal class ChessBoardPanel(var fens: Array<String>) : JPanel() {


    fun setFenList(expected: List<String>) {
        setFenList(expected, if (expected.isEmpty()) -1 else 0)
    }

    fun setFenList(expected: List<String>, selectedIndex: Int) {
        fens = expected.toTypedArray()
        fenComboBox.removeAllItems()
        fenComboBox.model = DefaultComboBoxModel(fens)
        fenComboBox.selectedIndex = selectedIndex
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
