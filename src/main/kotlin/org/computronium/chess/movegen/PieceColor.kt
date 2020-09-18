package org.computronium.chess.movegen

enum class PieceColor {

    WHITE, BLACK;

    fun oppositeColor() : PieceColor {
        return if (this == WHITE) BLACK else WHITE
    }

    companion object {
        fun from(c : Char) : PieceColor {
            return if (c.isUpperCase()) WHITE else BLACK
        }
    }
}
