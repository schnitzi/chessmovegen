package org.computronium.chess.movegen

import org.computronium.chess.movegen.BoardState.Companion.QUEEN_MOVE_OFFSETS
import org.computronium.chess.movegen.BoardState.Companion.ROOK_MOVE_OFFSETS
import org.computronium.chess.movegen.BoardState.Companion.onBoard
import org.computronium.chess.movegen.moves.*
import org.computronium.chess.movegen.moves.aspects.CaptureAspect
import java.util.Arrays
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream

class SearchNode(val boardState: BoardState) {

    val moves : List<Move>

    init {

        moves = boardState.piecePositions(boardState.whoseTurn).stream()
                .flatMap { position -> findMoves(position) }
                .filter { move -> isntMovingIntoCheck(move) }
                .collect(Collectors.toList())
    }

    private fun isntMovingIntoCheck(move: Move) : Boolean {

        move.apply(boardState)

        try {
            // Don't add the move if we put ourselves in check.
            if (!boardState.isKingInCheck(1 - boardState.whoseTurn)) {

                // See if the opponent is now in check.
                move.resultsInCheck = boardState.isKingInCheck(boardState.whoseTurn)

                // TODO check for mate and stalemate.
                return true
            }
        } finally {
            move.rollback(boardState)
        }
        return false
    }

    private fun findMoves(index: Int) : Stream<Move> {

        when (boardState[index]?.type) {
            PieceType.PAWN -> return findPawnMoves(index)
            PieceType.ROOK -> return findRookMoves(index)
            PieceType.BISHOP -> return findBishopMoves(index)
            PieceType.KNIGHT -> return findKnightMoves(index)
            PieceType.QUEEN -> return findQueenMoves(index)
            PieceType.KING -> return findKingMoves(index)
        }
    }

    private fun checkForCapture(builder: Move.Builder) {
        if (boardState[builder.to]?.color == 1 - boardState.whoseTurn) {
            builder.add(CaptureAspect(builder.to))
        }
    }

    private fun findKnightMoves(from: Int) : Stream<Move> {

        return Arrays.stream(BoardState.KNIGHT_MOVE_OFFSETS)
                .map { it + from }
                .filter { onBoard(it) }
                .map { Move.Builder(from, it) }
                .map { builder ->
                    checkForCapture(builder)
                    builder
                }
                .map { it.build() }
    }

    private fun findMovesViaOffsets(offsets: Array<Int>, from: Int) : Stream<Move> {

        for (offset in offsets) {
            var newIndex = from + offset

            // Check as far in every direction, as far as there are blank squares.
            while (onBoard(newIndex) && boardState.empty(newIndex)) {
                val builder = Move.Builder(from, newIndex)
                maybeAddMove(builder.build())
                newIndex += offset
            }

            // See if there's a capture at the end.
            if (onBoard(newIndex)) {
                if (boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                    val builder = Move.Builder(from, newIndex)
                    builder.add(CaptureAspect(newIndex))
                    maybeAddMove(builder.build())
                }
            }
        }
    }

    private fun findKingMoves(from: Int) : Stream<Move>{

        for (offset in BoardState.KING_MOVE_OFFSETS) {
            var newIndex = from + offset

            if (onBoard(newIndex) && boardState.empty(newIndex)) {
                maybeAddMove(KingMove(from, newIndex))
                newIndex += offset
            }

            if (onBoard(newIndex) && boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                // see if capture
                maybeAddMove(KingCapture(from, newIndex))
            }
        }

        if (CastleQueenSide.isPossible(boardState)) {
            maybeAddMove(CastleQueenSide())
        }

        if (CastleKingSide.isPossible(boardState)) {
            maybeAddMove(CastleKingSide())
        }
    }

    private fun findBishopMoves(from: Int) : Stream<Move> {

        findMovesViaOffsets(BoardState.BISHOP_MOVE_OFFSETS, from)
    }

    private fun findRookMoves(from: Int) : Stream<Move> {

        for (offset in ROOK_MOVE_OFFSETS) {
            var newIndex = from + offset

            while (onBoard(newIndex) && boardState.empty(newIndex)) {
                maybeAddMove(RookMove(from, newIndex))
                newIndex += offset
            }

            // see if capture
            if (onBoard(newIndex) && boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                maybeAddMove(RookCapture(from, newIndex))
            }
        }
    }

    private fun findQueenMoves(from: Int) : Stream<Move> {

        findMovesViaOffsets(QUEEN_MOVE_OFFSETS, from)
    }


    private fun findPawnMoves(from: Int) : Stream<Move> {

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
        }
    }
}
