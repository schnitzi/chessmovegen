package org.computronium.chess.core

import org.computronium.chess.core.moves.AlgebraicMoveNameGenerator
import org.computronium.chess.core.moves.MoveGenerator
import org.computronium.chess.core.moves.MoveNameGenerator

class GameRunner(val boardState: BoardState, moveNameGenerator: MoveNameGenerator) {

    private val moveGenerator = MoveGenerator(boardState, moveNameGenerator)

    fun generateSearchNode() : SearchNode {
        val moves = moveGenerator.getMoves()
        return SearchNode(boardState, moves)
    }

    companion object {

        private val DEFAULT_MOVE_NAME_GENERATOR = AlgebraicMoveNameGenerator()

        fun fromFEN(s: String) : GameRunner {
            val board = BoardState.fromFEN(s)
            return GameRunner(board, DEFAULT_MOVE_NAME_GENERATOR)
        }

        fun newGame() : GameRunner {
            return fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
        }
    }
}