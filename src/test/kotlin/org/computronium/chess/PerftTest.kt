package org.computronium.chess

import com.tngtech.java.junit.dataprovider.DataProvider
import com.tngtech.java.junit.dataprovider.DataProviderRunner
import com.tngtech.java.junit.dataprovider.DataProviders.`$$`
import com.tngtech.java.junit.dataprovider.DataProviders.`$`
import com.tngtech.java.junit.dataprovider.UseDataProvider
import org.computronium.chess.core.GameRunner
import org.computronium.chess.core.SearchNode
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(DataProviderRunner::class)
internal class PerftTest {

    companion object {

        @DataProvider
        @JvmStatic
        fun perfData(): Array<Array<Any>> = `$$`(

//                `$`("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 1, 20, 0),
//                `$`("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 6, 119_060_324, 2_812_008),
                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 3, 97862, 17102),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 4, 4_085_603, 757_163),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 5, 193_690_690, 35_043_416),
        )
    }


    @Test
    @UseDataProvider(value = "perfData")
    fun findMoves(startFEN: String, maxDepth: Int, expectedNodes: Int, expectedCaptures: Int) {

        println("Testing $startFEN")
        val runner = GameRunner.fromFEN(startFEN)
        val startBoard = runner.generateSearchNode()
        val perftWalker = PerftWalker(maxDepth)
        val actualDatum = PerftWalker.PerftDatum()

        perftWalker.walk(runner, startBoard, maxDepth, 0, actualDatum, true)

        println("actualDatum = $actualDatum")

        assertEquals(expectedNodes, actualDatum.moves, "Unexpected move count")
        assertEquals(expectedCaptures, actualDatum.captures, "Unexpected capture count")
    }
}