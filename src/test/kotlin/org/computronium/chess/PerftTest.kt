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


            // Test for movegen failure -- if black puts white in check, white shouldn't be able to castle out of it
//                `$`("8/8/8/8/8/3p3k/7P/4K2R b K - 0 1", 1, 4, 0),
                `$`("8/8/8/8/8/3p3k/7P/4K2R b K - 0 1", 2, 28, 1),

//                `$`("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 1, 20, 0),
//                `$`("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 2, 400, 0),
//                `$`("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 3, 8902, 34),
//                `$`("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 6, 119_060_324, 2_812_008),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 1, 48, 8),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 2, 2039, 351),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 3, 97862, 17102),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 4, 4_085_603, 757_163),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 5, 193_690_690, 35_043_416),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPB1PPP/R2BK2R b KQkq - 0 1", 4, 3_074_219, 0),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/4P3/2p2Q1p/PPPB1PPP/R2BK2R w KQkq - 0 1", 3, 69039, 16421),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3P4/4P3/2pN1Q1p/PPPB1PPP/R2BK2R b KQkq - 0 1", 2, 1707, 277),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3P4/4P3/3N1Q1p/PPPp1PPP/R2BK2R w KQkq - 0 1", 1, 3, 0),
//                `$`("n1n5/PPPk4/8/8/8/8/4Kppp/5N1N b - - 0 1", 1, 24, 11),
//                `$`("n1n5/PPPk4/8/8/8/8/4Kppp/5N1N b - - 0 1", 2, 496, 214),
//                `$`("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 1, 14, 1),
//                `$`("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 2, 191, 14),
//                `$`("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 3, 2812, 209),
//                `$`("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 4, 43238, 3348),
//                `$`("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 5, 674624, 52051),
//                `$`("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 6, 11030083, 940350),
//                `$`("k7/8/8/8/8/3p3P/8/4K2R b K - 0 1", 2, 32, 1),

//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 3, 97_862, 17_102),
//                `$`("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 4, 4_085_603, 757_163),
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

        assertEquals(expectedNodes, actualDatum.moves, "Unexpected move count $actualDatum")
        assertEquals(expectedCaptures, actualDatum.captures, "Unexpected capture count $actualDatum")
    }
}