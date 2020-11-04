package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class KingMoveAspect(val to: Int) : Aspect {

    private var canQueenSideCastle : Boolean? = null
    private var canKingSideCastle : Boolean? = null
    private var kingPos : Int? = null

    override fun apply(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()

        canQueenSideCastle = config.canQueenSideCastle
        config.canQueenSideCastle = false
        canKingSideCastle = config.canKingSideCastle
        config.canKingSideCastle = false

        kingPos = config.kingPos
        config.kingPos = to
    }

    override fun rollback(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()

        config.kingPos = kingPos!!

        config.canKingSideCastle = canKingSideCastle!!
        config.canQueenSideCastle = canQueenSideCastle!!
    }
}
