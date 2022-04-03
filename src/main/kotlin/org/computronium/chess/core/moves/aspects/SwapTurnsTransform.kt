package org.computronium.chess.core.moves.aspects

import org.computronium.chess.core.BoardState

/**
 * Transform for swapping turns.
 */
class SwapTurnsTransform : Transform {

    override fun apply(boardState: BoardState) {

        boardState.whoseTurn = 1 - boardState.whoseTurn
    }

    override fun rollback(boardState: BoardState) {

        boardState.whoseTurn = 1 - boardState.whoseTurn
    }
}
