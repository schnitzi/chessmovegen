package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

interface Aspect {

    fun apply(boardState: BoardState)

    fun rollback(boardState: BoardState)
}