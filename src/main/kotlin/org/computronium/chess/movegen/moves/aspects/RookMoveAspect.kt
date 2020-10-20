package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class RookMoveAspect(private val from : Int) : Aspect {

    private var canQueenSideCastle = false
    private var canKingSideCastle = false

    override fun apply(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()
        val homeRank = config.homeRankStart
        if (from == homeRank) {     // queenside rook
            canQueenSideCastle = config.canQueenSideCastle
            config.canQueenSideCastle = false
        } else if (from == homeRank+7) {    // kingside rook
            canKingSideCastle = config.canKingSideCastle
            config.canKingSideCastle = false
        }
    }

    override fun rollback(boardState: BoardState) {
        val config = boardState.whoseTurnConfig()
        val homeRank = config.homeRankStart
        if (from == homeRank) {
            config.canQueenSideCastle = canQueenSideCastle
        } else if (from == homeRank+7) {
            config.canKingSideCastle = canKingSideCastle
        }
    }
}