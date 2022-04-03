package org.computronium.chess.core.moves.aspects

import org.computronium.chess.core.BoardState

interface Transform {

    fun apply(boardState: BoardState)

    fun rollback(boardState: BoardState)
}