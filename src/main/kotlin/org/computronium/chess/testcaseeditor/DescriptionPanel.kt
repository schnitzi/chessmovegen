package org.computronium.chess.testcaseeditor

import org.computronium.chess.movegen.BoardState
import java.awt.Font
import javax.swing.JPanel
import javax.swing.JTextArea

/**
 * Panel which has the textual description of the board.
 */
internal class DescriptionPanel : JPanel() {

    private val textArea = JTextArea()

    init {
        font = Font("Sans-Serif", Font.PLAIN, 46)
//        border = BevelBorder(BevelBorder.LOWERED, Color.GRAY.brighter(), Color.GRAY, Color.GRAY.darker(), Color.GRAY)

//        layout = GridLayout(0, 0, 0, 0)
//        preferredSize = Dimension(300, 300)

        textArea.isEditable = false

        add(textArea)
    }

    fun setPosition(position: TestCase.TestCasePosition?) {

        textArea.text = ""

        if (position != null) {

            val boardState = position.getBoardState()

            textArea.text =
                "FEN: $position\n" +
                "${boardState.halfMovesSinceCaptureOrPawnAdvance} half moves since capture or pawn move.\n" +
                "${if (boardState.whoseTurn == BoardState.WHITE) "White" else "Black"}'s turn.\n" +
                "White ${if (boardState.sideConfig[BoardState.WHITE].canKingSideCastle) "can still" else "can no longer"} castle kingside.\n" +
                "White ${if (boardState.sideConfig[BoardState.WHITE].canQueenSideCastle) "can still" else "can no longer"} castle queenside.\n" +
                "Black ${if (boardState.sideConfig[BoardState.BLACK].canKingSideCastle) "can still" else "can no longer"} castle kingside.\n" +
                "Black ${if (boardState.sideConfig[BoardState.BLACK].canQueenSideCastle) "can still" else "can no longer"} castle queenside.\n" +
                "${if (boardState.enPassantCapturePos == null) "No en passant capture possible" else "En passant capture possible at " + BoardState.squareName(boardState.enPassantCapturePos!!)}.\n"
        }

        revalidate()
        repaint()
    }
}
