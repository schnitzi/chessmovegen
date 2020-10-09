package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.moves.aspects.Aspect
import org.computronium.chess.movegen.moves.aspects.MoveAspect

class Move(val aspects: List<Aspect>) {


    var resultsInCheck: Boolean = false

    fun apply(boardState: BoardState) {

        for (aspect in aspects) {
            aspect.apply(boardState)
        }
    }

    fun rollback(boardState: BoardState) {

        for (aspect in aspects) {
            aspect.rollback(boardState)
        }
    }


    class Builder(from: Int, val to: Int) {

        val aspects = mutableListOf<Aspect>()

        init {
            aspects.add(MoveAspect(from, to))
        }

        fun add(aspect: Aspect) {
            aspects += aspect
        }

        fun build() : Move {
            return Move(aspects.toList())
        }
    }
}