package org.computronium.chess.core

import org.computronium.chess.core.moves.Move

class SearchNode(val boardState: BoardState, val moves: List<Move>) {

    fun isCheckmate(): Boolean {
        val inCheck = boardState.whoseTurnData().isInCheck
        if (inCheck) {
            println("moves = $moves")
        }
        return inCheck && moves.isEmpty()
    }

    fun isStalemate(): Boolean {
        return !boardState.whoseTurnData().isInCheck && moves.isEmpty()
    }


    override fun toString(): String {
        return "$boardState\nmoves=${moves.map{ move -> move.toString() }}"
    }
}
