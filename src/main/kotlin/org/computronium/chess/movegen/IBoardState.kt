package org.computronium.chess.movegen

interface IBoardState {
    var whoseTurn: Int
    var moveNumber: Int
    var enPassantCapturePos: Int?
    var halfMovesSinceCaptureOrPawnAdvance: Int

    fun pieceAt(file: Int, rank: Int) : Piece?
}
