package org.computronium.chess

import com.tngtech.java.junit.dataprovider.DataProvider
import com.tngtech.java.junit.dataprovider.DataProviderRunner
import com.tngtech.java.junit.dataprovider.DataProviders
import com.tngtech.java.junit.dataprovider.UseDataProvider
import org.computronium.chess.movegen.SearchNode
import org.junit.Test
import org.junit.runner.RunWith
import com.tngtech.java.junit.dataprovider.DataProviders.`$$`
import com.tngtech.java.junit.dataprovider.DataProviders.`$`
import kotlin.test.assertEquals

@RunWith(DataProviderRunner::class)
internal class PerftTest {

    companion object {

        @DataProvider
        @JvmStatic
        fun perfData(): Array<Array<Any>> = `$$`(

//                `$`("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 6, 119_060_324, 2_812_008),
                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 5, 193_690_690, 35_043_416),
        )
    }


    @Test
    @UseDataProvider(value = "perfData")
    fun findMoves(startFEN: String, maxDepth: Int, expectedNodes: Int, expectedCaptures: Int) {
        val startBoard = SearchNode.fromFEN(startFEN)
        val moves = IntArray(maxDepth)
        val captures = IntArray(maxDepth)
        calculate(startBoard, maxDepth, 0, moves, captures)
        assertEquals(expectedNodes, moves[maxDepth-1], "Unexpected move count")
        assertEquals(expectedCaptures, captures[maxDepth-1], "Unexpected capture count")
        for ((index, count) in moves.withIndex()) {
            println("${index + 1} -- count=$count, captures=${captures[index]}")
        }
    }


    private fun calculate(node: SearchNode, maxDepth: Int, currentDepth: Int, moves: IntArray, captures: IntArray) {

        moves[currentDepth] += node.moves.size

        for (move in node.moves) {
            if (move.capture) {
                captures[currentDepth] += 1
            }
            if (currentDepth < maxDepth - 1) {
                move.apply(node.boardState)
                node.generateMoves()
                if (!node.isCheckmate() && !node.isStalemate()) {
                    calculate(node, maxDepth, currentDepth + 1, moves, captures)
                }
                move.rollback(node.boardState)
            }
        }
    }
}