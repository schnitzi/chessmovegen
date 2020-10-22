package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.moves.aspects.Aspect

class Move(val moveNames: List<String>, val aspects: List<Aspect>, var nameIndex: Int = 0) {

    var resultsInCheck: Boolean = false

    fun apply(boardState: BoardState) {

        for (aspect in aspects) {
            aspect.apply(boardState)
        }
    }

    fun rollback(boardState: BoardState) {

        for (aspect in aspects.reversed()) {
            aspect.rollback(boardState)
        }
    }

    override fun toString(): String {
        return moveNames[nameIndex]
    }
}