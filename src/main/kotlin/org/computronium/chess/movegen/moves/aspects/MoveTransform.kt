package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

/**
 * That transform that's just a piece moving from one place to another.
 */
class MoveTransform(private val from: Int, val to: Int) : Transform {

    override fun apply(boardState: BoardState) {

        boardState.move(from, to)
    }

    override fun rollback(boardState: BoardState) {

        boardState.move(to, from)
    }
}
