package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class CastleQueenSideAspect : Aspect {

    private var canKingSideCastle = false

    override fun apply(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()
        val homeRankStart = config.homeRankStart
        boardState.move(homeRankStart+4, homeRankStart+2)    // move the king
        config.kingPos = homeRankStart+2
        boardState.move(homeRankStart, homeRankStart+3)    // move the rook
        canKingSideCastle = config.canKingSideCastle
        config.canQueenSideCastle = false
        config.canKingSideCastle = false
    }

    override fun rollback(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()
        val homeRankStart = config.homeRankStart
        boardState.move(homeRankStart+2, homeRankStart+4)    // move the king back
        config.kingPos = homeRankStart+4
        boardState.move(homeRankStart+3, homeRankStart)    // move the rook back
        config.canQueenSideCastle = true
        config.canKingSideCastle = canKingSideCastle
    }

    companion object {

        fun isPossible(boardState: BoardState) : Boolean {

            val config = boardState.whoseTurnConfig()
            val homeRankStart = config.homeRankStart
            return !config.isInCheck &&
                    config.canQueenSideCastle &&
                    boardState.empty(homeRankStart+5) &&
                    boardState.empty(homeRankStart+6) &&
                    !boardState.isAttacked(homeRankStart+5, 1 - boardState.whoseTurn)
        }
    }
}