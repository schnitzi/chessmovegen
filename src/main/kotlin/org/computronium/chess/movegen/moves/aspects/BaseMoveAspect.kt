package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

/**
 * Aspect that does all the things that are common to every kind of move.
 */
class BaseMoveAspect : Aspect {

    private var enPassantCapturePos : Int? = null

    private var whoseTurnIsInCheck : Boolean = false

    override fun apply(boardState: BoardState) {

        enPassantCapturePos = boardState.enPassantCapturePos
        boardState.enPassantCapturePos = null

        whoseTurnIsInCheck = boardState.whoseTurnConfig().isInCheck

        if (boardState.whoseTurn == BoardState.WHITE) {
            boardState.moveNumber++
        }
    }

    override fun rollback(boardState: BoardState) {

        if (boardState.whoseTurn == BoardState.WHITE) {
            boardState.moveNumber--
        }

        boardState.whoseTurnConfig().isInCheck = whoseTurnIsInCheck

        boardState.enPassantCapturePos = enPassantCapturePos
    }
}
