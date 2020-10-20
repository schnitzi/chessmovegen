package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class KingMoveAspect : Aspect {

    private var canQueenSideCastle : Boolean? = null
    private var canKingSideCastle : Boolean? = null

    override fun apply(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()
        canQueenSideCastle = config.canQueenSideCastle
        config.canQueenSideCastle = false
        canKingSideCastle = config.canKingSideCastle
        config.canKingSideCastle = false
    }

    override fun rollback(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()
        config.canQueenSideCastle = canQueenSideCastle!!
        config.canKingSideCastle = canKingSideCastle!!
    }
}