package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.Piece
import org.computronium.chess.movegen.PieceType

open class CaptureAspect(val captureIndex : Int) : Aspect {

    private var capturedPiece: Piece? = null

    private var halfMovesSinceCaptureOrPawnAdvance: Int = 0

    override fun apply(boardState: BoardState) {

        capturedPiece = boardState[captureIndex]

        halfMovesSinceCaptureOrPawnAdvance = boardState.halfMovesSinceCaptureOrPawnAdvance

        boardState.halfMovesSinceCaptureOrPawnAdvance = 0
    }

    override fun rollback(boardState: BoardState) {

        boardState.halfMovesSinceCaptureOrPawnAdvance = halfMovesSinceCaptureOrPawnAdvance

        boardState[captureIndex] = capturedPiece
    }
}
