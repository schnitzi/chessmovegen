package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState

@Deprecated("Use an Aspect class")
class PawnInitialMove(from: Int, to: Int, private val over: Int) : PawnMove(from, to) {

    override fun apply(boardState: BoardState): BoardState {

        super.apply(boardState)

        boardState.enPassantCapturePos = over
        return boardState
    }

    // Surprisingly, no need to override rollback here.  En passant capture position is
    // entirely handled in Move.

    override fun toString(boardState: BoardState): String {
        return BoardState.squareName(to)
    }
}
