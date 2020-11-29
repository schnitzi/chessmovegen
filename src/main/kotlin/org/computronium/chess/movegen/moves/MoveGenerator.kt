package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.PieceType
import org.computronium.chess.movegen.moves.aspects.Transform
import org.computronium.chess.movegen.moves.aspects.BaseMoveTransform
import org.computronium.chess.movegen.moves.aspects.CaptureTransform
import org.computronium.chess.movegen.moves.aspects.CastleKingSideTransform
import org.computronium.chess.movegen.moves.aspects.CastleQueenSideTransform
import org.computronium.chess.movegen.moves.aspects.KingMoveTransform
import org.computronium.chess.movegen.moves.aspects.MoveTransform
import org.computronium.chess.movegen.moves.aspects.PawnInitialMoveTransform
import org.computronium.chess.movegen.moves.aspects.PawnMoveTransform
import org.computronium.chess.movegen.moves.aspects.PawnPromotionTransform
import org.computronium.chess.movegen.moves.aspects.RookMoveTransform
import org.computronium.chess.movegen.moves.aspects.SwapTurnsTransform
import java.util.stream.Collectors

/**
 * The move generator.
 */
class MoveGenerator(val boardState : BoardState) {

    fun getMoves() : List<Move> {

        val piecePositions = boardState.piecePositions(boardState.whoseTurn)

        val unfilteredMoves = mutableListOf<Move>()

        for (piecePosition in piecePositions) {
            unfilteredMoves.addAll(findMoves(piecePosition))
        }

        // Get rid of moves into check.
        val moves = unfilteredMoves.filter { !intoCheck(it) }

        // Set whether the move results in checking the opposing king, so we can put a "+" after the move name.
        for (move in moves) {
            setResultsInCheck(move)
        }

        // Remove any ambiguities involving the move names.
        var done = false
        while (!done) {
            done = true
            for (move in moves) {
                while (moves.stream().filter {it.moveNames.contains(move.getBaseMoveName())}.count() > 1) {
                    move.nameIndex += 1
                    done = false
                }
            }
        }

        return moves
    }

    private fun setResultsInCheck(move: Move) {
        try {
            move.apply(boardState)
            move.resultsInCheck = boardState.isKingInCheck(boardState.whoseTurn)
        } finally {
            move.rollback(boardState)
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
            return boardState.isKingInCheck(1 - boardState.whoseTurn)
        } finally {
            move.rollback(boardState)
        }
    }

    private fun findKnightMoves(from: Int) : List<Move> {

        val moves = mutableListOf<Move>()
        for (offset in BoardState.KNIGHT_MOVE_OFFSETS) {
            val newIndex = from + offset

            if (BoardState.onBoard(newIndex)) {
                if (boardState.empty(newIndex)) {
                    moves.add(standardMove(from, newIndex))
                }
                // see if capture
                if (boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                    moves.add(standardCapture(from, newIndex))
                }
            }
        }
        return moves
    }

    private fun findMovesViaOffsets(offsets: Array<Int>, from: Int) : List<Move> {

        val moves = mutableListOf<Move>()
        for (offset in offsets) {
            var newIndex = from + offset

            while (BoardState.onBoard(newIndex) && boardState.empty(newIndex)) {
                moves.add(standardMove(from, newIndex))
                newIndex += offset
            }

            // see if capture
            if (BoardState.onBoard(newIndex) && boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                moves.add(standardCapture(from, newIndex))
            }
        }
        return moves
    }

    private fun findKingMoves(from: Int) : List<Move> {

        val moves = mutableListOf<Move>()
        for (offset in BoardState.KING_MOVE_OFFSETS) {
            val newIndex = from + offset

            if (BoardState.onBoard(newIndex)) {
                if (boardState.empty(newIndex)) {
                    moves.add(kingMove(from, newIndex))
                }
                if (boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                    moves.add(kingCapture(from, newIndex))
                }
            }
        }

        if (CastleQueenSideTransform.isPossible(boardState)) {
            moves.add(queenSideCastle())
        }

        if (CastleKingSideTransform.isPossible(boardState)) {
            moves.add(kingSideCastle())
        }

        return moves
    }

    private fun findBishopMoves(from: Int) : List<Move> {

        return findMovesViaOffsets(BoardState.BISHOP_MOVE_OFFSETS, from)
    }

    private fun findRookMoves(from: Int) : List<Move> {

        val moves = mutableListOf<Move>()
        for (offset in BoardState.ROOK_MOVE_OFFSETS) {
            var newIndex = from + offset

            while (BoardState.onBoard(newIndex) && boardState.empty(newIndex)) {
                moves.add(rookMove(from, newIndex))
                newIndex += offset
            }

            // see if capture
            if (BoardState.onBoard(newIndex) && boardState[newIndex]?.color == 1 - boardState.whoseTurn) {
                moves.add(rookCapture(from, newIndex))
            }
        }
        return moves
    }

    private fun findQueenMoves(from: Int) : List<Move> {

        return findMovesViaOffsets(BoardState.QUEEN_MOVE_OFFSETS, from)
    }

    private fun findPawnMoves(from: Int) : List<Move> {

        val moves = mutableListOf<Move>()
        val dir = boardState.whoseTurnData().pawnMoveDirection

        // Can the pawn move forward one?
        val forwardOnePosition = from + dir

        if (boardState.empty(forwardOnePosition)) {

            if (boardState.whoseTurnData().isAboutToPromote(from)) {
                moves.add(pawnPromotion(from, forwardOnePosition, PieceType.QUEEN))
                moves.add(pawnPromotion(from, forwardOnePosition, PieceType.ROOK))
                moves.add(pawnPromotion(from, forwardOnePosition, PieceType.KNIGHT))
                moves.add(pawnPromotion(from, forwardOnePosition, PieceType.BISHOP))
            } else {
                moves.add(standardMove(from, forwardOnePosition))
            }

            // Can the pawn move forward two?
            if (boardState.whoseTurnData().isPawnHomeRank(from)) {

                val forwardTwoPosition = from + dir + dir

                if (boardState.empty(forwardTwoPosition)) {

                    // We want to also note the special coordinates for handling en passant.
                    moves.add(pawnInitialMove(from, forwardTwoPosition, forwardOnePosition))
                }
            }
        }

        // Pawn captures.
        for (dx in listOf(-1, 1)) {
            val to = from + boardState.whoseTurnData().pawnMoveDirection + dx
            if (BoardState.onBoard(to)) {
                if (boardState[to]?.color == 1 - boardState.whoseTurn) {

                    if (boardState.whoseTurnData().isAboutToPromote(from)) {
                        // Capture with promotion.
                        moves.add(pawnCaptureWithPromotion(from, to, PieceType.QUEEN))
                        moves.add(pawnCaptureWithPromotion(from, to, PieceType.ROOK))
                        moves.add(pawnCaptureWithPromotion(from, to, PieceType.KNIGHT))
                        moves.add(pawnCaptureWithPromotion(from, to, PieceType.BISHOP))
                    } else {
                        // Ordinary capture.
                        moves.add(standardCapture(from, to))
                    }
                } else if (to == boardState.enPassantCapturePos) {
                    // En passant capture.
                    moves.add(pawnEnPassantCapture(from, to, to - boardState.whoseTurnData().pawnMoveDirection))
                }
            }
        }

        return moves
    }

    private fun appendAll(buffers: List<StringBuffer>, s: String) {
        for (buffer in buffers) {
            buffer.append(s)
        }
    }

    private fun standardMove(from: Int, to: Int) : Move {
        return MoveBuilder(from, to)
                .add(MoveTransform(from, to))
                .build()
    }

    private fun standardCapture(from: Int, to: Int) : Move {
        return MoveBuilder(from, to, true)
                .add(CaptureTransform(to))
                .add(MoveTransform(from, to))
                .build()
    }

    private fun kingMove(from: Int, to: Int) : Move {
        return MoveBuilder(from, to)
                .add(KingMoveTransform())
                .add(MoveTransform(from, to))
                .build()
    }

    private fun kingSideCastle() : Move {
        return MoveBuilder("O-O")
                .add(KingMoveTransform())
                .add(CastleKingSideTransform())
                .build()
    }

    private fun queenSideCastle() : Move {
        return MoveBuilder("O-O-O")
                .add(KingMoveTransform())
                .add(CastleQueenSideTransform())
                .build()
    }

    private fun kingCapture(from: Int, to: Int) : Move {
        return MoveBuilder(from, to)
                .add(CaptureTransform(to))
                .add(KingMoveTransform())
                .add(MoveTransform(from, to))
                .build()
    }

    private fun rookMove(from: Int, to: Int) : Move {
        return MoveBuilder(from, to)
                .add(RookMoveTransform(from))
                .add(MoveTransform(from, to))
                .build()
    }

    private fun rookCapture(from: Int, to: Int) : Move {
        return MoveBuilder(from, to)
                .add(RookMoveTransform(from))
                .add(CaptureTransform(to))
                .add(MoveTransform(from, to))
                .build()
    }

    private fun pawnInitialMove(from: Int, to: Int, over: Int) : Move {
        return MoveBuilder(from, to)
                .add(PawnMoveTransform())
                .add(PawnInitialMoveTransform(over))
                .add(MoveTransform(from, to))
                .build()
    }

    private fun pawnPromotion(from: Int, to: Int, promoteTo: PieceType) : Move {
        return MoveBuilder(from, to, false, promoteTo)
                .add(PawnMoveTransform())
                .add(MoveTransform(from, to))
                .add(PawnPromotionTransform(to, promoteTo))
                .build()
    }

    private fun pawnCaptureWithPromotion(from: Int, to: Int, promoteTo: PieceType) : Move {
        return MoveBuilder(from, to, true, promoteTo)
                .add(CaptureTransform(to))
                .add(MoveTransform(from, to))
                .add(PawnPromotionTransform(to, promoteTo))
                .build()
    }

    private fun pawnEnPassantCapture(from: Int, to: Int, enPassantCapturedPiecePos: Int) : Move {

        return MoveBuilder(from, to, true)
                .add(CaptureTransform(enPassantCapturedPiecePos))
                .add(MoveTransform(from, to))
                .build()
    }

    inner class MoveBuilder {

        private val moveNames: List<String>
        private val transforms = mutableListOf<Transform>(BaseMoveTransform())
        private var metadata = MoveMetadata(false, false, false, false, false)

        constructor(from: Int, to: Int, capture: Boolean = false, promoteTo: PieceType? = null) {
            this.moveNames = generateMoveNames(from, to, capture, promoteTo)
        }

        constructor(moveName: String) {
            this.moveNames = listOf(moveName)
        }


        fun add(transform: Transform) : MoveBuilder {
            transforms.add(transform)
            if (transform is CaptureTransform) {
                metadata.capture = true
            } else if (transform is CastleQueenSideTransform || transform is CastleKingSideTransform) {
                metadata.castle = true
            } else if (transform is PawnPromotionTransform) {
                metadata.promotion = true
            }
            return this
        }

        private fun generateMoveNames(from: Int, to: Int, capture: Boolean = false, promoteTo: PieceType? = null): List<String> {
            val plain = StringBuffer()
            val withFile = StringBuffer()
            val withRank = StringBuffer()
            val withBoth = StringBuffer()
            val buffers = listOf(plain, withFile, withRank, withBoth)

            val piece = boardState[from]

            if (piece?.type != PieceType.PAWN) {
                appendAll(buffers, "${piece?.type?.letter}")
            } else if (capture) {
                plain.append("${BoardState.fileChar(from)}")
            }

            appendAll(listOf(withFile, withBoth), "${BoardState.fileChar(from)}")
            appendAll(listOf(withRank, withBoth), "${BoardState.rankChar(from)}")

            if (capture) {
                appendAll(buffers, "x")
            }

            appendAll(buffers, "${BoardState.fileChar(to)}")
            appendAll(buffers, "${BoardState.rankChar(to)}")
            if (promoteTo != null) {
                appendAll(buffers, "=${promoteTo.letter}")
            }

            return buffers.stream().map { sb -> sb.toString() }.collect(Collectors.toList())
        }


        fun build() : Move {
            transforms.add(SwapTurnsTransform())
            return Move(moveNames, transforms, metadata)
        }
    }

    data class MoveMetadata(var capture: Boolean, var enPassant: Boolean, var castle: Boolean, var promotion: Boolean, var check: Boolean)

}
