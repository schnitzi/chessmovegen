package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState

/**
 * Interface representing a move that can be applied and rolled back.
 */
abstract class Move {

    var resultsInCheck = false

    private var enPassantCapturePos : Int? = null

    private var whoseTurnIsInCheck : Boolean = false

    open fun apply(boardState: BoardState): BoardState {

        enPassantCapturePos = boardState.enPassantCapturePos

        boardState.enPassantCapturePos = null

        whoseTurnIsInCheck = boardState.whoseTurnConfig().isInCheck

        if (boardState.whoseTurn == BoardState.BLACK) {
            boardState.moveNumber++
        }

        boardState.whoseTurn = 1 - boardState.whoseTurn

        return boardState
    }

    open fun rollback(boardState: BoardState) {

        boardState.whoseTurn = 1 - boardState.whoseTurn

        if (boardState.whoseTurn == BoardState.BLACK) {
            boardState.moveNumber--
        }

        boardState.whoseTurnConfig().isInCheck = whoseTurnIsInCheck

        boardState.enPassantCapturePos = enPassantCapturePos
    }

    abstract fun toString(boardState: BoardState): String
}
