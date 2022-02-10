package org.computronium.chess

import com.tngtech.java.junit.dataprovider.DataProvider
import com.tngtech.java.junit.dataprovider.DataProviderRunner
import com.tngtech.java.junit.dataprovider.DataProviders.`$$`
import com.tngtech.java.junit.dataprovider.DataProviders.`$`
import com.tngtech.java.junit.dataprovider.UseDataProvider
import org.computronium.chess.movegen.SearchNode
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(DataProviderRunner::class)
internal class PerftTest {

    companion object {

        @DataProvider
        @JvmStatic
        fun perfData(): Array<Array<Any>> = `$$`(

//                `$`("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 6, 119_060_324, 2_812_008),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 3, 97862, 17102),
                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 4, 4_085_603, 757_163),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 5, 193_690_690, 35_043_416),
        )
    }


    @Test
    @UseDataProvider(value = "perfData")
    fun findMoves(startFEN: String, maxDepth: Int, expectedNodes: Int, expectedCaptures: Int) {

        println("Testing $startFEN");
        val startBoard = SearchNode.fromFEN(startFEN)
        val perfData : Array<PerfData> = Array(maxDepth) { PerfData(0, 0, 0, 0, 0) }

        calculate(startBoard, maxDepth, 0, perfData)

        for ((index, perfDatum) in perfData.withIndex()) {
            println("${index + 1} -- $perfDatum")
        }

        val actualData = perfData[maxDepth-1]

        assertEquals(expectedNodes, actualData.moves, "Unexpected move count")
        assertEquals(expectedCaptures, actualData.captures, "Unexpected capture count")
    }


    private fun calculate(node: SearchNode, maxDepth: Int, currentDepth: Int, perfData: Array<PerfData>) {

        val perfDatum = perfData[currentDepth]
        perfDatum.moves += node.moves.size

        for (move in node.moves) {

            if (move.metadata.capture) {
                perfDatum.captures += 1
            }

            if (move.metadata.castle) {
                perfDatum.castles += 1
            }

            if (move.metadata.promotion) {
                perfDatum.promotions += 1
            }

            if (currentDepth < maxDepth - 1) {
                move.apply(node.boardState)
                node.generateMoves()
                if (!node.isCheckmate() && !node.isStalemate()) {
                    calculate(node, maxDepth, currentDepth + 1, perfData)
                }
                move.rollback(node.boardState)
            }
        }
    }

    private data class PerfData(var moves: Int, var captures: Int, var enPassants: Int, var castles: Int, var promotions: Int) {
        override fun toString(): String {
            return "moves=$moves captures=$captures enPassants=$enPassants castles=$castles promotions=$promotions"
        }
    }
}