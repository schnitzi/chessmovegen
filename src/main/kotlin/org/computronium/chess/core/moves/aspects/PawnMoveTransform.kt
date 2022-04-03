package org.computronium.chess.core.moves.aspects

import org.computronium.chess.core.BoardState

open class PawnMoveTransform : Transform {

    private var halfMovesSinceCaptureOrPawnAdvance = 0

    override fun apply(boardState: BoardState) {

        halfMovesSinceCaptureOrPawnAdvance = boardState.halfMovesSinceCaptureOrPawnAdvance
        boardState.halfMovesSinceCaptureOrPawnAdvance = 0
    }

    override fun rollback(boardState: BoardState) {

        boardState.halfMovesSinceCaptureOrPawnAdvance = halfMovesSinceCaptureOrPawnAdvance
    }
}
