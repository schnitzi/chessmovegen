package org.computronium.chess.movegen

import org.computronium.chess.movegen.moves.MoveGenerator

class SearchNode(val boardState: BoardState) {

    val moves = MoveGenerator(boardState).getMoves()


    fun isCheckmate(): Boolean {
        return boardState.whoseTurnData().isInCheck && moves.isEmpty()
    }

    fun isStalemate(): Boolean {
        return !boardState.whoseTurnData().isInCheck && moves.isEmpty()
    }

    override fun toString(): String {
        return "$boardState\nmoves=${moves.map{ move -> move.toString() }}"
    }

    companion object {

        fun fromFEN(s: String) : SearchNode {
            val board = BoardState.fromFEN(s)
            return SearchNode(board)
        }

        fun newGame() : SearchNode {
            return fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
        }
    }
}
