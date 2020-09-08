package org.computronium.chess

import org.computronium.chess.BoardState.Companion.QUEEN_MOVE_OFFSETS
import org.computronium.chess.BoardState.Companion.ROOK_MOVE_OFFSETS
import org.computronium.chess.BoardState.Companion.onBoard
import org.computronium.chess.moves.*

class SearchNode(val boardState: BoardState) {

    val moves = mutableListOf<Move>()

    init {

        val piecePositions = boardState.piecePositions(boardState.whoseTurn)

        for (piecePosition in piecePositions) {
            findMoves(piecePosition)
        }
    }

    private fun maybeAddMove(move: Move) {
        move.apply(boardState)
        if (!boardState.isKingInCheck(1 - boardState.whoseTurn)) {
            move.resultsInCheck = boardState.isKingInCheck(boardState.whoseTurn)
            moves.add(move)
        }
        move.rollback(boardState)
    }
    private fun findMoves(index: Int) {

        when (boardState[index]?.type) {
            PieceType.PAWN -> findPawnMoves(index)
            PieceType.ROOK -> findRookMoves(index)
            PieceType.BISHOP -> findBishopMoves(index)
            PieceType.KNIGHT -> findKnightMoves(index)
            PieceType.QUEEN -> findQueenMoves(index)
            PieceType.KING -> findKingMoves(index)
        }
    }

    private fun findKnightMoves(pos: Int) {

        for (offset in BoardState.KNIGHT_MOVE_OFFSETS) {
            val newPos = pos + offset

            if (BoardState.onBoard(newPos) && boardState[newPos]?.color != boardState.whoseTurn) {
                maybeAddMove(StandardMove(pos, newPos))
            }
        }
    }

    private fun findMovesViaOffsets(offsets: Array<Int>, from: Int) {

        for (offset in offsets) {
            var newIndex = from + offset

            while (BoardState.onBoard(newIndex) && boardState.empty(newIndex)) {
                maybeAddMove(StandardMove(from, newIndex))
                newIndex += offset
            }

            // see if capture
            if (onBoard(newIndex) && boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                maybeAddMove(StandardCapture(from, newIndex))
            }
        }
    }

    private fun findKingMoves(from: Int) {

        for (offset in BoardState.KING_MOVE_OFFSETS) {
            val to = from + offset
            if (onBoard(to) && boardState[to]?.color != boardState.whoseTurn) {
                maybeAddMove(KingMove(from, to))
            }
        }

        if (CastleQueenSide.isPossible(boardState)) {
            maybeAddMove(CastleQueenSide())
        }

        if (CastleKingSide.isPossible(boardState)) {
            maybeAddMove(CastleKingSide())
        }
    }

    private fun findBishopMoves(from: Int) {

        findMovesViaOffsets(BoardState.BISHOP_MOVE_OFFSETS, from)
    }

    private fun findRookMoves(from: Int) {

        for (offset in ROOK_MOVE_OFFSETS) {
            var newIndex = from + offset

            while (BoardState.onBoard(newIndex) && boardState.empty(newIndex)) {
                maybeAddMove(RookMove(from, newIndex))
                newIndex += offset
            }

            // see if capture
            if (onBoard(newIndex) && boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                maybeAddMove(RookCapture(from, newIndex))
            }
        }
    }

    private fun findQueenMoves(from: Int) {

        findMovesViaOffsets(QUEEN_MOVE_OFFSETS, from)
    }


    private fun findPawnMoves(from: Int) {

        val dir = boardState.whoseTurnConfig().pawnMoveDirection

        // Can the pawn move forward one?
        val forwardOnePosition = from + dir

        if (boardState.empty(forwardOnePosition)) {

            if (boardState.whoseTurnConfig().isAboutToPromote(from)) {
                maybeAddMove(PawnPromotion(from, forwardOnePosition, PieceType.QUEEN))
                maybeAddMove(PawnPromotion(from, forwardOnePosition, PieceType.ROOK))
                maybeAddMove(PawnPromotion(from, forwardOnePosition, PieceType.KNIGHT))
                maybeAddMove(PawnPromotion(from, forwardOnePosition, PieceType.BISHOP))
            } else {
                maybeAddMove(StandardMove(from, forwardOnePosition))
            }

            // Can the pawn move forward two?
            if (boardState.whoseTurnConfig().isPawnHomeRank(from)) {

                val forwardTwoPosition = from + dir + dir

                if (boardState.empty(forwardTwoPosition)) {

                    // We want to also note the special coordinates for handling en passant.
                    maybeAddMove(PawnInitialMove(from, forwardTwoPosition, forwardOnePosition))
                }
            }
        }
        
        // Pawn captures.
        for (dx in listOf(-1, 1)) {
            val capturePos = from + boardState.whoseTurnConfig().pawnMoveDirection + dx
            if (onBoard(capturePos)) {
                if (boardState[capturePos]?.color == 1 - boardState.whoseTurn) {

                    if (boardState.whoseTurnConfig().isAboutToPromote(from)) {
                        // Capture with promotion.
                        maybeAddMove(PawnPromotion(from, capturePos, PieceType.QUEEN))
                        maybeAddMove(PawnPromotion(from, capturePos, PieceType.ROOK))
                        maybeAddMove(PawnPromotion(from, capturePos, PieceType.KNIGHT))
                        maybeAddMove(PawnPromotion(from, capturePos, PieceType.BISHOP))
                    } else {
                        // Ordinary capture.
                        maybeAddMove(StandardCapture(from, capturePos))
                    }
                } else if (capturePos == boardState.enPassantCapturePos) {
                    // En passant capture.
                    maybeAddMove(PawnEnPassantCapture(from))
                }
            }
        }
    }

    fun isCheckmate(): Boolean {
        return boardState.whoseTurnConfig().isInCheck && moves.isEmpty()
    }

    fun isStalemate(): Boolean {
        return !boardState.whoseTurnConfig().isInCheck && moves.isEmpty()
    }

    override fun toString(): String {
        return "$boardState\nmoves=${moves.map{ move -> move.toString(boardState) }}"
    }

    companion object {

        fun fromFEN(s: String) : SearchNode {
            val board = BoardState.fromFEN(s)
            return SearchNode(board)
        }

        fun newGame() : SearchNode {
            return fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
//            return fromFEN(arrayOf(
//                "rnbqkbnr",
//                "pppppppp",
//                "        ",
//                "        ",
//                "        ",
//                "        ",
//                "PPPPPPPP",
//                "RNBQKBNR"
//            ))
        }
    }
}
