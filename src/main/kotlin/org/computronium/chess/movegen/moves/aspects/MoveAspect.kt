package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

/**
 * That aspect that's just a piece moving from one place to another.
 */
class MoveAspect(val from: Int, val to: Int) : Aspect {

    override fun apply(boardState: BoardState) {

        boardState.move(from, to)
    }

    override fun rollback(boardState: BoardState) {

        boardState.move(to, from)
    }
}
