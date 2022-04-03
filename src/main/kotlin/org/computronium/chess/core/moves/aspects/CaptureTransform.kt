package org.computronium.chess.core.moves.aspects

import org.computronium.chess.core.BoardState
import org.computronium.chess.core.Piece
import org.computronium.chess.core.PieceType
import kotlin.system.exitProcess

open class CaptureTransform(private val captureIndex : Int) : Transform {

    private var capturedPiece: Piece? = null

    private var halfMovesSinceCaptureOrPawnAdvance: Int = 0

    override fun apply(boardState: BoardState) {

        capturedPiece = boardState[captureIndex]

        if (capturedPiece!!.type == PieceType.KING) {
            System.err.println("King is captured.  Something has gone awry.")
            exitProcess(-1)
        }

        boardState[captureIndex] = null

        halfMovesSinceCaptureOrPawnAdvance = boardState.halfMovesSinceCaptureOrPawnAdvance

        boardState.halfMovesSinceCaptureOrPawnAdvance = 0
    }

    override fun rollback(boardState: BoardState) {

        boardState.halfMovesSinceCaptureOrPawnAdvance = halfMovesSinceCaptureOrPawnAdvance

        boardState[captureIndex] = capturedPiece
    }
}
