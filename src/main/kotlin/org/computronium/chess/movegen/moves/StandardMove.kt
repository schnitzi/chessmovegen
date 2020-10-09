package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.PieceType

@Deprecated("Use an Aspect class")
open class StandardMove(var from : Int, var to : Int) : MoveBase() {

    private var halfMovesSinceCaptureOrPawnAdvance = 0

    /**
     * This will generate a simplified version of the last move in Algebraic notation that
     * suffices in most cases.  However, sometimes this version is ambiguous, and needs more
     * information, which can't be determined without comparison to other moves.  Figuring
     * out how to do this is something I still need TODO.
     */
    override fun toString(boardState: BoardState): String {
        val sb = StringBuilder()
        val piece = boardState[from]
        if (piece!!.type != PieceType.PAWN) {
            sb.append(piece.type.letter)
        }
        sb.append(BoardState.squareName(to))
        if (resultsInCheck) {
            sb.append("+")
        }
        return sb.toString()
    }

    override fun apply(boardState: BoardState): BoardState {

        boardState.move(from, to)

        halfMovesSinceCaptureOrPawnAdvance = boardState.halfMovesSinceCaptureOrPawnAdvance
        boardState.halfMovesSinceCaptureOrPawnAdvance++

        super.apply(boardState)

        if (boardState[to]!!.type == PieceType.PAWN) {

            boardState.halfMovesSinceCaptureOrPawnAdvance = 0
        }

        return boardState
    }

    override fun rollback(boardState: BoardState) {

        super.rollback(boardState)

        boardState.halfMovesSinceCaptureOrPawnAdvance = halfMovesSinceCaptureOrPawnAdvance

        boardState.move(to, from)
    }
}
