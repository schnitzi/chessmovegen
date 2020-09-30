package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

/**
 * Interface representing a move that can be applied and rolled back.
 */
class MoveAspect : Aspect {

    var resultsInCheck = false

    private var enPassantCapturePos : Int? = null

    private var whoseTurnIsInCheck : Boolean = false

    override fun apply(boardState: BoardState) {

        enPassantCapturePos = boardState.enPassantCapturePos

        boardState.enPassantCapturePos = null

        whoseTurnIsInCheck = boardState.whoseTurnConfig().isInCheck

        if (boardState.whoseTurn == BoardState.WHITE) {
            boardState.moveNumber++
        }

        boardState.whoseTurn = 1 - boardState.whoseTurn
    }

    override fun rollback(boardState: BoardState) {

        boardState.whoseTurn = 1 - boardState.whoseTurn

        if (boardState.whoseTurn == BoardState.WHITE) {
            boardState.moveNumber--
        }

        boardState.whoseTurnConfig().isInCheck = whoseTurnIsInCheck

        boardState.enPassantCapturePos = enPassantCapturePos
    }
}
