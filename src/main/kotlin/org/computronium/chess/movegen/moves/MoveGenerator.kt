package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.PieceType
import org.computronium.chess.movegen.moves.aspects.Aspect
import org.computronium.chess.movegen.moves.aspects.BaseMoveAspect
import org.computronium.chess.movegen.moves.aspects.CaptureAspect
import org.computronium.chess.movegen.moves.aspects.CastleKingSideAspect
import org.computronium.chess.movegen.moves.aspects.CastleQueenSideAspect
import org.computronium.chess.movegen.moves.aspects.KingMoveAspect
import org.computronium.chess.movegen.moves.aspects.MoveAspect
import org.computronium.chess.movegen.moves.aspects.PawnInitialMoveAspect
import org.computronium.chess.movegen.moves.aspects.PawnMoveAspect
import org.computronium.chess.movegen.moves.aspects.PawnPromotionAspect
import org.computronium.chess.movegen.moves.aspects.RookMoveAspect
import org.computronium.chess.movegen.moves.aspects.SwapTurnsAspect
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

        for (move in moves) {
            setResultsInCheck(move)
        }

        var done = false
        while (!done) {
            done = true
            for (move in moves) {
                while (moves.stream().filter {it.moveNames.contains(move.moveNames[move.nameIndex])}.count() > 1) {
                    move.nameIndex += 1
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

        if (CastleQueenSideAspect.isPossible(boardState)) {
            moves.add(queenSideCastle())
        }

        if (CastleKingSideAspect.isPossible(boardState)) {
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
                .add(MoveAspect(from, to))
                .build()
    }

    private fun standardCapture(from: Int, to: Int) : Move {
        return MoveBuilder(from, to, true)
                .add(CaptureAspect(to))
                .add(MoveAspect(from, to))
                .build()
    }

    private fun kingMove(from: Int, to: Int) : Move {
        return MoveBuilder(from, to)
                .add(KingMoveAspect())
                .add(MoveAspect(from, to))
                .build()
    }

    private fun kingSideCastle() : Move {
        return MoveBuilder("O-O")
                .add(KingMoveAspect())
                .add(CastleKingSideAspect())
                .build()
    }

    private fun queenSideCastle() : Move {
        return MoveBuilder("O-O-O")
                .add(KingMoveAspect())
                .add(CastleQueenSideAspect())
                .build()
    }

    private fun kingCapture(from: Int, to: Int) : Move {
        return MoveBuilder(from, to)
                .add(CaptureAspect(to))
                .add(KingMoveAspect())
                .add(MoveAspect(from, to))
                .build()
    }

    private fun rookMove(from: Int, to: Int) : Move {
        return MoveBuilder(from, to)
                .add(RookMoveAspect(from))
                .add(MoveAspect(from, to))
                .build()
    }

    private fun rookCapture(from: Int, to: Int) : Move {
        return MoveBuilder(from, to)
                .add(RookMoveAspect(from))
                .add(CaptureAspect(to))
                .add(MoveAspect(from, to))
                .build()
    }

    private fun pawnInitialMove(from: Int, to: Int, over: Int) : Move {
        return MoveBuilder(from, to)
                .add(PawnMoveAspect())
                .add(PawnInitialMoveAspect(over))
                .add(MoveAspect(from, to))
                .build()
    }

    private fun pawnPromotion(from: Int, to: Int, promoteTo: PieceType) : Move {
        return MoveBuilder(from, to, false, promoteTo)
                .add(PawnMoveAspect())
                .add(MoveAspect(from, to))
                .add(PawnPromotionAspect(to, promoteTo))
                .build()
    }

    private fun pawnCaptureWithPromotion(from: Int, to: Int, promoteTo: PieceType) : Move {
        return MoveBuilder(from, to, true, promoteTo)
                .add(CaptureAspect(to))
                .add(MoveAspect(from, to))
                .add(PawnPromotionAspect(to, promoteTo))
                .build()
    }

    private fun pawnEnPassantCapture(from: Int, to: Int, enPassantCapturedPiecePos: Int) : Move {

        return MoveBuilder(from, to, true)
                .add(CaptureAspect(enPassantCapturedPiecePos))
                .add(MoveAspect(from, to))
                .build()
    }

    inner class MoveBuilder {

        private val moveNames: List<String>
        val aspects = mutableListOf<Aspect>(BaseMoveAspect())

        constructor(from: Int, to: Int, capture: Boolean = false, promoteTo: PieceType? = null) {
            this.moveNames = generateMoveNames(from, to, capture, promoteTo)
        }

        constructor(moveName: String) {
            this.moveNames = listOf(moveName)
        }


        fun add(aspect: Aspect) : MoveBuilder {
            aspects.add(aspect)
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
            aspects.add(SwapTurnsAspect())
            return Move(moveNames, aspects)
        }
    }

}
