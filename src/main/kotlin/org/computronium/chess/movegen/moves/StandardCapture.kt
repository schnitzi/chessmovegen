package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.Piece
import org.computronium.chess.movegen.PieceType

/**
 * Class representing a move.
 */
open class StandardCapture(from : Int, to : Int) : StandardMove(from, to) {


    private var capturedPiece: Piece? = null

    private var halfMovesSinceCaptureOrPawnAdvance: Int = 0

    /**
     * This will generate a simplified version of the last move in Algebraic notation that
     * suffices in most cases.  However, sometimes this version is ambiguous, and needs more
     * information, which can't be determined without comparison to other moves.  Figuring
     * out how to do this is something I still need TODO.
     */
    override fun toString(boardState: BoardState): String {
        val sb = StringBuilder()
        val piece = boardState[from]
        val capturedPiece = boardState[to]
        if (piece!!.type != PieceType.PAWN) {
            sb.append(piece.type.letter)
        } else if (capturedPiece != null) {
            sb.append(BoardState.fileChar(from))
        }
        sb.append("x")
        sb.append(to)
        return sb.toString()
    }

    override fun apply(boardState: BoardState): BoardState {

        capturedPiece = boardState[to]

        halfMovesSinceCaptureOrPawnAdvance = boardState.halfMovesSinceCaptureOrPawnAdvance

        super.apply(boardState)

        boardState.halfMovesSinceCaptureOrPawnAdvance = 0
        return boardState
    }

    override fun rollback(boardState: BoardState) {

        super.rollback(boardState)

        boardState.halfMovesSinceCaptureOrPawnAdvance = halfMovesSinceCaptureOrPawnAdvance

        boardState[to] = capturedPiece
    }
}
