package org.computronium.chess

import org.computronium.chess.core.GameRunner
import org.computronium.chess.core.SearchNode

/**
 * Spits out moves for a given FEN, as required to run move generator against
 * <a href="https://github.com/agausmann/perftree">perftree</a>.
 */

class MoveSpitter(private val maxDepth: Int, startFEN: String) {

    private val runner = GameRunner.fromFEN(startFEN)

    private fun run() {

        val startBoard = runner.generateSearchNode()

        val total = countMoves(startBoard, 0)

        println()
        println(total)
    }

    private fun countMoves(node: SearchNode, currentDepth: Int) : Int {

        if (currentDepth == maxDepth) {
            return 1
        }

        var moves = 0
        var count = 0
        for (move in node.moves) {

            move.apply(node.boardState)
            if (!node.isCheckmate() && !node.isStalemate()) {
                count = countMoves(runner.generateSearchNode(), currentDepth + 1)
            }
            move.rollback(node.boardState)

            if (currentDepth == 0) {
                println("${move.moveNames.first()} $count")
            }

            moves += count
        }

        return moves
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            val maxDepth = args[0].toInt()
            val startFEN = args[1]

            val moveSpitter = MoveSpitter(maxDepth, startFEN)
            moveSpitter.run()
        }
    }
}
