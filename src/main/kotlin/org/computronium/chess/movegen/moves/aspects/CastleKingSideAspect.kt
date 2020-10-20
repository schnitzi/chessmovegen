package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class CastleKingSideAspect : Aspect {

    private var canQueenSideCastle = false

    override fun apply(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()
        val homeRankStart = config.homeRankStart
        boardState.move(homeRankStart+4, homeRankStart+6)    // move the king
        config.kingPos = homeRankStart+6
        boardState.move(homeRankStart+7, homeRankStart+5)    // move the rook
        canQueenSideCastle = config.canQueenSideCastle
        config.canQueenSideCastle = false
        config.canKingSideCastle = false
    }

    override fun rollback(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()
        val homeRankStart = config.homeRankStart
        boardState.move(homeRankStart+6, homeRankStart+4)    // move the king back
        config.kingPos = homeRankStart+4
        boardState.move(homeRankStart+5, homeRankStart+7)    // move the rook back
        config.canQueenSideCastle = canQueenSideCastle
        config.canKingSideCastle = true
    }

    companion object {

        fun isPossible(boardState: BoardState) : Boolean {

            val config = boardState.whoseTurnConfig()
            val homeRankStart = config.homeRankStart
            return !config.isInCheck &&
                    config.canKingSideCastle &&
                    boardState.empty(homeRankStart+5) &&
                    boardState.empty(homeRankStart+6) &&
                    !boardState.isAttacked(homeRankStart+5, 1 - boardState.whoseTurn)
        }
    }
}