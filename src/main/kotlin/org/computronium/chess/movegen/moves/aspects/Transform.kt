package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

interface Transform {

    fun apply(boardState: BoardState)

    fun rollback(boardState: BoardState)
}