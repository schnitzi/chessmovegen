package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

/**
 * Aspect that does all the things that are common to every kind of move.
 */
class SwapTurnsAspect : Aspect {

    override fun apply(boardState: BoardState) {

        boardState.whoseTurn = 1 - boardState.whoseTurn
    }

    override fun rollback(boardState: BoardState) {

        boardState.whoseTurn = 1 - boardState.whoseTurn
    }
}
