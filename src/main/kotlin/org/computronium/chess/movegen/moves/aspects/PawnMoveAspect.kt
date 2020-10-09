package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

open class PawnMoveAspect : Aspect {

    private var halfMovesSinceCaptureOrPawnAdvance = 0

    override fun apply(boardState: BoardState) {

        halfMovesSinceCaptureOrPawnAdvance = boardState.halfMovesSinceCaptureOrPawnAdvance
        boardState.halfMovesSinceCaptureOrPawnAdvance = 0
    }

    override fun rollback(boardState: BoardState) {

        boardState.halfMovesSinceCaptureOrPawnAdvance = halfMovesSinceCaptureOrPawnAdvance
    }
}
