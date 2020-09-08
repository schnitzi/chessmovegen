package org.computronium.chess.moves

import org.computronium.chess.BoardState

class CastleKingSide : Move() {

    var canQueenSideCastle = false

    override fun apply(boardState: BoardState): BoardState {

        val config = boardState.whoseTurnConfig()
        val homeRankStart = config.homeRankStart
        boardState.move(homeRankStart+4, homeRankStart+6)    // move the king
        config.kingPos = homeRankStart+6
        boardState.move(homeRankStart+7, homeRankStart+5)    // move the rook
        canQueenSideCastle = config.canQueenSideCastle
        config.canQueenSideCastle = false
        config.canKingSideCastle = false

        super.apply(boardState)
        return boardState
    }

    override fun rollback(boardState: BoardState) {

        super.rollback(boardState)

        val config = boardState.whoseTurnConfig()
        val homeRankStart = config.homeRankStart
        boardState.move(homeRankStart+6, homeRankStart+4)    // move the king back
        config.kingPos = homeRankStart+4
        boardState.move(homeRankStart+5, homeRankStart+7)    // move the rook back
        config.canQueenSideCastle = canQueenSideCastle
        config.canKingSideCastle = true
    }

    override fun toString(boardState: BoardState): String {
        return "O-O"
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