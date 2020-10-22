package org.computronium.chess.movegen

import org.computronium.chess.movegen.BoardState.Companion.QUEEN_MOVE_OFFSETS
import org.computronium.chess.movegen.BoardState.Companion.ROOK_MOVE_OFFSETS
import org.computronium.chess.movegen.BoardState.Companion.onBoard
import org.computronium.chess.movegen.moves.*
import org.computronium.chess.movegen.moves.aspects.CastleKingSideAspect
import org.computronium.chess.movegen.moves.aspects.CastleQueenSideAspect

class SearchNode(val boardState: BoardState) {

    val moves : List<Move>
    
    private val moveGenerator = MoveGenerator(boardState)

    init {

        val piecePositions = boardState.piecePositions(boardState.whoseTurn)

        val unfilteredMoves = mutableListOf<Move>()

        for (piecePosition in piecePositions) {
            unfilteredMoves.addAll(findMoves(piecePosition))
        }

        moves = unfilteredMoves.filter { !intoCheck(it) }

        var done = false
        while (!done) {
            done = true
            for (move in moves) {
                while (moves.stream().filter {it.moveNames.contains(move.toString())}.count() > 1) {
                    move.nameIndex += 1
                }
            }
        }
    }

    private fun findMoves(index: Int) : List<Move> {

        return when (boardState[index]!!.type) {
            PieceType.PAWN -> findPawnMoves(index)
            PieceType.ROOK -> findRookMoves(index)
            PieceType.BISHOP -> findBishopMoves(index)
            PieceType.KNIGHT -> findKnightMoves(index)
            PieceType.QUEEN -> findQueenMoves(index)
            PieceType.KING -> findKingMoves(index)
        }
    }

    private fun intoCheck(move: Move): Boolean {
        try {
            move.apply(boardState)
            return boardState.isKingInCheck(1-boardState.whoseTurn)
        } finally {
            move.rollback(boardState)
        }
    }

    private fun findKnightMoves(from: Int) : List<Move> {

        val moves = mutableListOf<Move>()
        for (offset in BoardState.KNIGHT_MOVE_OFFSETS) {
            val newIndex = from + offset

            if (onBoard(newIndex)) {
                if (boardState.empty(newIndex)) {
                    moves.add(moveGenerator.standardMove(from, newIndex))
                }
                // see if capture
                if (boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                    moves.add(moveGenerator.standardCapture(from, newIndex))
                }
            }
        }
        return moves
    }

    private fun findMovesViaOffsets(offsets: Array<Int>, from: Int) : List<Move> {

        val moves = mutableListOf<Move>()
        for (offset in offsets) {
            var newIndex = from + offset

            while (onBoard(newIndex) && boardState.empty(newIndex)) {
                moves.add(moveGenerator.standardMove(from, newIndex))
                newIndex += offset
            }

            // see if capture
            if (onBoard(newIndex) && boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                moves.add(moveGenerator.standardCapture(from, newIndex))
            }
        }
        return moves
    }

    private fun findKingMoves(from: Int) : List<Move> {

        val moves = mutableListOf<Move>()
        for (offset in BoardState.KING_MOVE_OFFSETS) {
            var newIndex = from + offset

            if (onBoard(newIndex)) {
                if (boardState.empty(newIndex)) {
                    moves.add(moveGenerator.kingMove(from, newIndex))
                    newIndex += offset
                }
                if (boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                    moves.add(moveGenerator.kingCapture(from, newIndex))
                }
            }
        }

        if (CastleQueenSideAspect.isPossible(boardState)) {
            moves.add(moveGenerator.queenSideCastle())
        }

        if (CastleKingSideAspect.isPossible(boardState)) {
            moves.add(moveGenerator.kingSideCastle())
        }

        return moves
    }

    private fun findBishopMoves(from: Int) : List<Move> {

        return findMovesViaOffsets(BoardState.BISHOP_MOVE_OFFSETS, from)
    }

    private fun findRookMoves(from: Int) : List<Move> {

        val moves = mutableListOf<Move>()
        for (offset in ROOK_MOVE_OFFSETS) {
            var newIndex = from + offset

            while (onBoard(newIndex) && boardState.empty(newIndex)) {
                moves.add(moveGenerator.rookMove(from, newIndex))
                newIndex += offset
            }

            // see if capture
            if (onBoard(newIndex) && boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                moves.add(moveGenerator.rookCapture(from, newIndex))
            }
        }
        return moves
    }

    private fun findQueenMoves(from: Int) : List<Move> {

        return findMovesViaOffsets(QUEEN_MOVE_OFFSETS, from)
    }

    private fun findPawnMoves(from: Int) : List<Move> {

        val moves = mutableListOf<Move>()
        val dir = boardState.whoseTurnConfig().pawnMoveDirection

        // Can the pawn move forward one?
        val forwardOnePosition = from + dir

        if (boardState.empty(forwardOnePosition)) {

            if (boardState.whoseTurnConfig().isAboutToPromote(from)) {
                moves.add(moveGenerator.pawnPromotion(from, forwardOnePosition, PieceType.QUEEN))
                moves.add(moveGenerator.pawnPromotion(from, forwardOnePosition, PieceType.ROOK))
                moves.add(moveGenerator.pawnPromotion(from, forwardOnePosition, PieceType.KNIGHT))
                moves.add(moveGenerator.pawnPromotion(from, forwardOnePosition, PieceType.BISHOP))
            } else {
                moves.add(moveGenerator.standardMove(from, forwardOnePosition))
            }

            // Can the pawn move forward two?
            if (boardState.whoseTurnConfig().isPawnHomeRank(from)) {

                val forwardTwoPosition = from + dir + dir

                if (boardState.empty(forwardTwoPosition)) {

                    // We want to also note the special coordinates for handling en passant.
                    moves.add(moveGenerator.pawnInitialMove(from, forwardTwoPosition, forwardOnePosition))
                }
            }
        }

        // Pawn captures.
        for (dx in listOf(-1, 1)) {
            val to = from + boardState.whoseTurnConfig().pawnMoveDirection + dx
            if (onBoard(to)) {
                if (boardState[to]?.color == 1 - boardState.whoseTurn) {

                    if (boardState.whoseTurnConfig().isAboutToPromote(from)) {
                        // Capture with promotion.
                        moves.add(moveGenerator.pawnCaptureWithPromotion(from, to, PieceType.QUEEN))
                        moves.add(moveGenerator.pawnCaptureWithPromotion(from, to, PieceType.ROOK))
                        moves.add(moveGenerator.pawnCaptureWithPromotion(from, to, PieceType.KNIGHT))
                        moves.add(moveGenerator.pawnCaptureWithPromotion(from, to, PieceType.BISHOP))
                    } else {
                        // Ordinary capture.
                        moves.add(moveGenerator.standardCapture(from, to))
                    }
                } else if (to == boardState.enPassantCapturePos) {
                    // En passant capture.
                    moves.add(moveGenerator.pawnEnPassantCapture(from, to, to - boardState.whoseTurnConfig().pawnMoveDirection))
                }
            }
        }

        return moves
    }

    fun isCheckmate(): Boolean {
        return boardState.whoseTurnConfig().isInCheck && moves.isEmpty()
    }

    fun isStalemate(): Boolean {
        return !boardState.whoseTurnConfig().isInCheck && moves.isEmpty()
    }

    override fun toString(): String {
        return "$boardState\nmoves=${moves.map{ move -> move.toString() }}"
    }

    companion object {

        fun fromFEN(s: String) : SearchNode {
            val board = BoardState.fromFEN(s)
            return SearchNode(board)
        }

        fun newGame() : SearchNode {
            return fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
        }
    }
}
