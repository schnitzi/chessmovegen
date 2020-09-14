package org.computronium.chess

interface IBoardState {
    var whoseTurn: Int
    var moveNumber: Int
    var enPassantCapturePos: Int?
    var halfMovesSinceCaptureOrPawnAdvance: Int

    fun pieceAt(file: Int, rank: Int) : Piece?
}