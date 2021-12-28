package org.computronium.chess.testcaseeditor

import org.computronium.chess.movegen.BoardState
import java.awt.Font
import javax.swing.JEditorPane
import javax.swing.JPanel

/**
 * Panel which has the textual description of the board.
 */
internal class MetadataPanel : JPanel() {

    private val textArea = JEditorPane()

    init {
        font = Font("Sans-Serif", Font.PLAIN, 46)

        textArea.isEditable = false
        textArea.contentType = "text/html"

        add(textArea)
    }

    fun setPosition(position: TestCase.TestCasePosition?) {

        textArea.text = ""

        if (position != null) {

            val boardState = position.getBoardState()

            textArea.text =
                "<em>${if (boardState.whoseTurn == BoardState.WHITE) "White" else "Black"}'s turn.<br/>" +
                "${boardState.halfMovesSinceCaptureOrPawnAdvance} half moves since capture or pawn move.<br/>" +
                "White ${if (boardState.sideData[BoardState.WHITE].canKingSideCastle) "can still" else "can no longer"} castle kingside.<br/>" +
                "White ${if (boardState.sideData[BoardState.WHITE].canQueenSideCastle) "can still" else "can no longer"} castle queenside.<br/>" +
                "Black ${if (boardState.sideData[BoardState.BLACK].canKingSideCastle) "can still" else "can no longer"} castle kingside.<br/>" +
                "Black ${if (boardState.sideData[BoardState.BLACK].canQueenSideCastle) "can still" else "can no longer"} castle queenside.<br/>" +
                "${if (boardState.enPassantCapturePos == null) "No en passant capture possible" else "En passant capture possible at " + BoardState.squareName(boardState.enPassantCapturePos!!)}.<br/></em>"
        }

        revalidate()
        repaint()
    }
}
