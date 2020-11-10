package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class PawnInitialMoveTransform(private val over: Int) : Transform {

    override fun apply(boardState: BoardState) {

        boardState.enPassantCapturePos = over
    }

    override fun rollback(boardState: BoardState) {
        // Surprisingly, no need to override rollback here.  En passant capture position is
        // entirely handled in BaseMoveAspect.
    }
}
