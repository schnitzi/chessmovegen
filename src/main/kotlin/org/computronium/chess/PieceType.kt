package org.computronium.chess

enum class PieceType(val letter: Char) {

    PAWN('P'),
    ROOK('R'),
    KNIGHT('N'),
    BISHOP('B'),
    QUEEN('Q'),
    KING('K');

    companion object {
        fun from(c : Char) : PieceType? {
            for (value in values()) {
                if (value.letter == c.toUpperCase()) return value
            }
            return null
        }
    }
}
