package org.computronium.chess.moves.aspects

import org.computronium.chess.BoardState

interface Aspect {

    fun apply(boardState: BoardState)

    fun rollback(boardState: BoardState)
}