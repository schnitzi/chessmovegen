package org.computronium.chess

enum class Piece(val color: Int, val type: PieceType, val char: Char) {

        WHITE_KING(BoardState.WHITE, PieceType.KING, 'K'),
        WHITE_QUEEN(BoardState.WHITE, PieceType.QUEEN, 'Q'),
        WHITE_BISHOP(BoardState.WHITE, PieceType.BISHOP, 'B'),
        WHITE_KNIGHT(BoardState.WHITE, PieceType.KNIGHT, 'N'),
        WHITE_ROOK(BoardState.WHITE, PieceType.ROOK, 'R'),
        WHITE_PAWN(BoardState.WHITE, PieceType.PAWN, 'P'),
        BLACK_KING(BoardState.BLACK, PieceType.KING, 'k'),
        BLACK_QUEEN(BoardState.BLACK, PieceType.QUEEN, 'q'),
        BLACK_BISHOP(BoardState.BLACK, PieceType.BISHOP, 'b'),
        BLACK_KNIGHT(BoardState.BLACK, PieceType.KNIGHT, 'n'),
        BLACK_ROOK(BoardState.BLACK, PieceType.ROOK, 'r'),
        BLACK_PAWN(BoardState.BLACK, PieceType.PAWN, 'p');


    companion object {
        fun ofChar(char: Char) : Piece? {
            for (piece in values()) {
                if (piece.char == char) {
                    return piece
                }
            }
            return null
        }

        fun forTypeAndColor(type: PieceType, color: Int): Piece? {
            for (piece in values()) {
                if (piece.type == type && piece.color == color) {
                    return piece
                }
            }
            return null
        }
    }

    override fun toString(): String {
        return "$char"
    }
}
