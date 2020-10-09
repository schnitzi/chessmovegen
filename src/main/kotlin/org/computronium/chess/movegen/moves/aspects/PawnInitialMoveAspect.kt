package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class PawnInitialMoveAspect(private val over: Int) : Aspect {

    override fun apply(boardState: BoardState) {

        boardState.enPassantCapturePos = over
    }

    override fun rollback(boardState: BoardState) {
        // Surprisingly, no need to override rollback here.  En passant capture position is
        // entirely handled in MoveAspect.
    }
}
