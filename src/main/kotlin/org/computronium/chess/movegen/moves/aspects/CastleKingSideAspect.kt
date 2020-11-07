package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class CastleKingSideAspect : Aspect {

    override fun apply(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()
        boardState.move(sideData.kingPos+3, sideData.kingPos+1)    // move the rook
        boardState.move(sideData.kingPos, sideData.kingPos+2)    // move the king
    }

    override fun rollback(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()
        boardState.move(sideData.kingPos-1, sideData.kingPos+1)    // move the rook back
        boardState.move(sideData.kingPos, sideData.kingPos-2)    // move the king back
    }

    companion object {

        fun isPossible(boardState: BoardState) : Boolean {

            val sideData = boardState.whoseTurnData()
            val homeRankStart = sideData.homeRankStart
            return !sideData.isInCheck &&
                    sideData.canKingSideCastle &&
                    boardState.empty(homeRankStart+5) &&
                    boardState.empty(homeRankStart+6) &&
                    !boardState.isAttacked(homeRankStart+5, 1 - boardState.whoseTurn)
        }
    }
}