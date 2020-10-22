package org.computronium.chess.movegen

import org.computronium.chess.movegen.moves.Move
import org.computronium.chess.movegen.moves.aspects.CaptureAspect
import org.computronium.chess.movegen.moves.aspects.CastleKingSideAspect
import org.computronium.chess.movegen.moves.aspects.CastleQueenSideAspect
import org.computronium.chess.movegen.moves.aspects.KingMoveAspect
import org.computronium.chess.movegen.moves.aspects.MoveAspect
import org.computronium.chess.movegen.moves.aspects.PawnInitialMoveAspect
import org.computronium.chess.movegen.moves.aspects.PawnMoveAspect
import org.computronium.chess.movegen.moves.aspects.PawnPromotionAspect
import org.computronium.chess.movegen.moves.aspects.RookMoveAspect
import java.util.stream.Collectors

/**
 * The main class representing a complete board state.
 */
class MoveGenerator(val boardState : BoardState) {

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

    private fun appendAll(buffers: List<StringBuffer>, s: String) {
        for (buffer in buffers) {
            buffer.append(s)
        }
    }

    fun standardMove(from: Int, to: Int) : Move {
        return Move(
                generateMoveNames(from, to),
                listOf(
                        MoveAspect(from, to)))
    }

    fun standardCapture(from: Int, to: Int) : Move {
        return Move(
                generateMoveNames(from, to, true),
                listOf(CaptureAspect(to),
                        MoveAspect(from, to)))
    }

    fun kingMove(from: Int, to: Int) : Move {
        return Move(
                generateMoveNames(from, to),
                listOf(KingMoveAspect(from, to),
                        MoveAspect(from, to)))
    }

    fun kingSideCastle() : Move {
        return Move(
                listOf("O-O"),
                listOf(CastleKingSideAspect()))
    }

    fun queenSideCastle() : Move {
        return Move(
                listOf("O-O-O"),
                listOf(CastleQueenSideAspect()))
    }

    fun kingCapture(from: Int, to: Int) : Move {
        return Move(
                generateMoveNames(from, to),
                listOf(CaptureAspect(to),
                        MoveAspect(from, to),
                        KingMoveAspect(from, to)))
    }

    fun rookMove(from: Int, to: Int) : Move {
        return Move(
                generateMoveNames(from, to),
                listOf(MoveAspect(from, to),
                        RookMoveAspect(from)))
    }

    fun rookCapture(from: Int, to: Int) : Move {
        return Move(
                generateMoveNames(from, to),
                listOf(CaptureAspect(to),
                        MoveAspect(from, to),
                        RookMoveAspect(from)))
    }

    fun pawnInitialMove(from: Int, to: Int, over: Int) : Move {
        return Move(
                generateMoveNames(from, to),
                listOf(MoveAspect(from, to),
                        PawnMoveAspect(),
                        PawnInitialMoveAspect(over)))
    }

    fun pawnPromotion(from: Int, to: Int, promoteTo: PieceType) : Move {
        return Move(
                generateMoveNames(from, to, false, promoteTo),
                listOf(MoveAspect(from, to),
                        PawnPromotionAspect(from, to, promoteTo),
                        PawnMoveAspect()))
    }

    fun pawnCaptureWithPromotion(from: Int, to: Int, promoteTo: PieceType) : Move {
        return Move(
                generateMoveNames(from, to, true, promoteTo),
                listOf(CaptureAspect(to),
                        MoveAspect(from, to),
                            PawnPromotionAspect(from, to, promoteTo)))
    }

    fun pawnEnPassantCapture(from: Int, to: Int, enPassantCapturedPiecePos: Int) : Move {
        return Move(
                generateMoveNames(from, to, true),
                listOf(CaptureAspect(enPassantCapturedPiecePos),
                        MoveAspect(from, to)))
    }
}
