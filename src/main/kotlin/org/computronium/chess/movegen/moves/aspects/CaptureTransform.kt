package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.Piece

open class CaptureTransform(private val captureIndex : Int) : Transform {

    private var capturedPiece: Piece? = null

    private var halfMovesSinceCaptureOrPawnAdvance: Int = 0

    override fun apply(boardState: BoardState) {

        capturedPiece = boardState[captureIndex]

        boardState[captureIndex] = null

        halfMovesSinceCaptureOrPawnAdvance = boardState.halfMovesSinceCaptureOrPawnAdvance

        boardState.halfMovesSinceCaptureOrPawnAdvance = 0
    }

    override fun rollback(boardState: BoardState) {

        boardState.halfMovesSinceCaptureOrPawnAdvance = halfMovesSinceCaptureOrPawnAdvance

        boardState[captureIndex] = capturedPiece
    }
}
