package org.computronium.chess

import com.tngtech.java.junit.dataprovider.DataProviderRunner
import org.computronium.chess.core.GameRunner
import org.computronium.chess.core.SearchNode
import org.junit.runner.RunWith

@RunWith(DataProviderRunner::class)
internal class PerftWalker(maxDepth: Int) {

    private val childDatum = PerftDatum()

    fun walk(
        runner: GameRunner,
        node: SearchNode,
        maxDepth: Int,
        currentDepth: Int,
        collector: PerftDatum,
        topLevel : Boolean) {

        if (currentDepth == maxDepth - 1) {
            collector.moves += node.moves.size

            for (move in node.moves) {

                if (move.metadata.capture) {
                    collector.captures += 1
                }

                if (move.metadata.castle) {
                    collector.castles += 1
                }

                if (move.metadata.promotion) {
                    collector.promotions += 1
                }
            }
        } else {
            for (move in node.moves) {
                move.apply(runner.boardState)
                val newNode = runner.generateSearchNode()

                if (!newNode.isCheckmate() && !newNode.isStalemate()) {
                    if (topLevel) {
                        childDatum.reset()
                        walk(runner, newNode, maxDepth, currentDepth + 1, childDatum, false)
                        println("$move\t${childDatum.moves}")
//                        println("childDatum = $childDatum")
                        collector.add(childDatum)
                    } else {
                        walk(runner, newNode, maxDepth, currentDepth + 1, collector, false)
                    }
                }
                move.rollback(runner.boardState)
            }
        }
    }

    data class PerftDatum(var moves: Int = 0, var captures: Int = 0, var enPassants: Int = 0, var castles: Int = 0, var promotions: Int = 0) {
        override fun toString(): String {
            return "moves=$moves captures=$captures enPassants=$enPassants castles=$castles promotions=$promotions"
        }

        fun reset() {
            moves = 0
            captures = 0
            enPassants = 0
            castles = 0
            promotions = 0
        }

        fun add(childDatum: PerftDatum) {
            moves += childDatum.moves
            captures += childDatum.captures
            enPassants += childDatum.enPassants
            castles += childDatum.castles
            promotions += childDatum.promotions
        }
    }
}