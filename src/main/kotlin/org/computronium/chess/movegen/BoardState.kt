package org.computronium.chess.movegen

import java.util.HashSet
import java.util.StringTokenizer
import kotlin.system.exitProcess

/**
 * The main class representing a complete board state.
 */
class BoardState(private val board: Array<Piece?>) {


    val sideData = arrayOf(
            SideData(
                    25,
                    37,
                    97,
                    12,
                    29),
            SideData(
                    109,
                    97,
                    37,
                    -12,
                    113
            ))

    var whoseTurn = WHITE

    var moveNumber = 0

    var enPassantCapturePos: Int? = null

    var halfMovesSinceCaptureOrPawnAdvance = 0
    

    init {
        // Save the coordinate for each king, so we can figure out if the king is in check.
        for (index in BOARD_INDEXES) {
            if (board[index]?.type == PieceType.KING) {
                sideData[board[index]?.color!!].kingPos = index
            }
        }

        // TODO set canCastle if (boardState[whoseTurnData().kingHomePos()] == )
    }

    override fun toString(): String {

        val sb = StringBuilder()

        for (rank in 7 downTo 0) {
            for (file in 0..7) {
                sb.append("${board[indexOf(file, rank)] ?: (if ((file+rank)%2==0) "." else " ")} ")
            }
            sb.append("\n")
        }

        sb.append("$whoseTurn to move")
        return sb.toString()
    }

    fun pieceAt(file: Int, rank: Int) : Piece? {
        return board[indexOf(file, rank)]
    }

    fun toFEN() : String {

        val sb = StringBuilder()

        for (rank in 7 downTo 0) {
            var count = 0
            for (file in 0..7) {
                val piece = board[indexOf(file, rank)]
                if (piece == null) {
                    count++
                } else {
                    if (count > 0) {
                        sb.append(count)
                        count = 0
                    }
                    sb.append(piece)
                }
            }
            if (count > 0) {
                sb.append(count)
            }
            if (rank > 0) {
                sb.append("/")
            }
        }

        sb.append(" ")
            .append(if (whoseTurn == WHITE) "w" else "b")
            .append(" ")

        if (!sideData[WHITE].canKingSideCastle &&
            !sideData[WHITE].canQueenSideCastle &&
            !sideData[BLACK].canKingSideCastle &&
            !sideData[BLACK].canQueenSideCastle) {
            sb.append("-")
        } else {
            if (sideData[WHITE].canKingSideCastle) sb.append("K")
            if (sideData[WHITE].canQueenSideCastle) sb.append("Q")
            if (sideData[BLACK].canKingSideCastle) sb.append("k")
            if (sideData[BLACK].canQueenSideCastle) sb.append("q")
        }

        sb.append(" ").append(if (enPassantCapturePos == null) "-" else squareName(enPassantCapturePos!!))

        sb.append(" $halfMovesSinceCaptureOrPawnAdvance $moveNumber")
        return sb.toString()
    }

    operator fun get(pos: Int): Piece? {
        return board[pos]
    }

    fun empty(index: Int): Boolean {
        return get(index) == null
    }

    operator fun set(index: Int, piece: Piece?) {
        board[index] = piece
    }

    fun move(from: Int, to: Int) {
        board[to] = get(from)
        board[from] = null

        if (board[to] == null) {
            exitProcess(-1)
        }

        if (board[to]!!.type == PieceType.KING) {
            whoseTurnData().kingPos = to
        }
    }

    fun piecePositions(color: Int): List<Int> {

        // TODO shouldn't have to compute this each time.
        val positions = mutableListOf<Int>()
        for (index in BOARD_INDEXES) {
            if (board[index]?.color == color) {
                positions.add(index)
            }
        }
        return positions
    }

    fun isAttacked(pos: Int, attackingColor: Int) : Boolean {
        return isAttackedByPieceTypes(pos, setOf(PieceType.QUEEN, PieceType.ROOK), ROOK_MOVE_OFFSETS, attackingColor) ||
                isAttackedByPieceTypes(pos, setOf(PieceType.QUEEN, PieceType.BISHOP), BISHOP_MOVE_OFFSETS, attackingColor) ||
                isAttackedByKnight(pos, attackingColor) ||
                isAttackedByPawn(pos, attackingColor) ||
                isAttackedByKing(pos, attackingColor)
    }

    private fun isPieceOfType(type: PieceType, pos: Int, attackingColor: Int) : Boolean {
        val piece = get(pos)
        return piece?.color == attackingColor && piece.type == type
    }

    private fun isAttackedByKing(pos: Int, attackingColor: Int): Boolean {
        for (offset in KING_MOVE_OFFSETS) {
            if (isPieceOfType(PieceType.KING, pos+offset, attackingColor)) {
                return true
            }
        }
        return false
    }

    private fun isAttackedByPawn(pos: Int, attackingColor: Int): Boolean {
        val pawnIndex = pos - sideData[attackingColor].pawnMoveDirection
        return isPieceOfType(PieceType.PAWN, pawnIndex-1, attackingColor) ||
                isPieceOfType(PieceType.PAWN, pawnIndex+1, attackingColor)
    }

    private fun isAttackedByKnight(pos: Int, attackingColor: Int): Boolean {
        for (offset in KNIGHT_MOVE_OFFSETS) {
            if (isPieceOfType(PieceType.KNIGHT, pos+offset, attackingColor)) {
                return true
            }
        }
        return false
    }

    private fun isAttackedByPieceTypes(index: Int, pieceTypes: Set<PieceType>, offsets: Array<Int>, attackingColor: Int) : Boolean {
        for (offset in offsets) {
            var currentIndex = index + offset

            while (ON_BOARD[currentIndex] && empty(currentIndex)) {
                currentIndex += offset
            }

            // see if capture
            if (ON_BOARD[currentIndex]) {
                val piece = get(currentIndex)!!
                if (piece.color == attackingColor && piece.type in pieceTypes) {
                    return true
                }
            }
        }
        return false
    }

    fun whoseTurnData() : SideData {
        return sideData[whoseTurn]
    }

    fun isKingInCheck(color: Int): Boolean {
        return isAttacked(sideData[color].kingPos, 1-color)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoardState

        if (!board.contentEquals(other.board)) return false
        if (!sideData.contentEquals(other.sideData)) return false
        if (whoseTurn != other.whoseTurn) return false
        if (moveNumber != other.moveNumber) return false
        if (enPassantCapturePos != other.enPassantCapturePos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.contentHashCode()
        result = 31 * result + sideData.contentHashCode()
        result = 31 * result + whoseTurn
        result = 31 * result + moveNumber
        result = 31 * result + (enPassantCapturePos ?: 0)
        return result
    }

    fun transpose(): BoardState {
        val transposedBoard = Array<Piece?>(ON_BOARD.size) { null }

        var transposedEnPassantCapturePos : Int? = null

        for (rank in 7 downTo 0) {
            for (file in 0..7) {
                val pos = indexOf(file, rank)
                val piece = board[pos]
                if (piece != null) {
                    transposedBoard[indexOf(file, 7-rank)] = Piece.forTypeAndColor(piece.type, 1-piece.color)
                }
                if (pos == enPassantCapturePos) {
                    transposedEnPassantCapturePos = indexOf(file, 7-rank)
                }
            }
        }

        val transposedBoardState = BoardState(transposedBoard)
        transposedBoardState.whoseTurn = 1-whoseTurn
        transposedBoardState.moveNumber = moveNumber
        transposedBoardState.enPassantCapturePos = transposedEnPassantCapturePos
        transposedBoardState.halfMovesSinceCaptureOrPawnAdvance = halfMovesSinceCaptureOrPawnAdvance
        transposedBoardState.sideData[WHITE].canQueenSideCastle = sideData[BLACK].canQueenSideCastle
        transposedBoardState.sideData[WHITE].canKingSideCastle = sideData[BLACK].canKingSideCastle
        transposedBoardState.sideData[BLACK].canQueenSideCastle = sideData[WHITE].canQueenSideCastle
        transposedBoardState.sideData[BLACK].canKingSideCastle = sideData[WHITE].canKingSideCastle
        transposedBoardState.sideData[BLACK].kingPos = sideData[WHITE].kingPos
        transposedBoardState.sideData[WHITE].kingPos = sideData[BLACK].kingPos
        transposedBoardState.sideData[WHITE].isInCheck = sideData[BLACK].isInCheck
        transposedBoardState.sideData[BLACK].isInCheck = sideData[WHITE].isInCheck
        return transposedBoardState
    }

    companion object {

        const val WHITE = 0
        const val BLACK = 1

        val ROOK_MOVE_OFFSETS = arrayOf(-12, -1, 1, 12)
        val QUEEN_MOVE_OFFSETS = arrayOf(-13, -12, -11, -1, 1, 11, 12, 13)
        val KNIGHT_MOVE_OFFSETS = arrayOf(-25, -23, -14, -10, 10, 14, 23, 25)
        val KING_MOVE_OFFSETS = arrayOf(-13, -12, -11, -1, 1, 11, 12, 13)
        val BISHOP_MOVE_OFFSETS = arrayOf(-13, -11, 11, 13)

        // 109 110 111 112 113 114 115 116
        //  97
        //  85
        //  73
        //  61
        //  49
        //  37
        //  25  26  27  28  29  30  31  32

        // The boardState is implemented as a single array that wraps around, with padding around
        // the outside so that we can easily check for a piece trying to move off the boardState.
        // These are the resulting array indexes of the squares that make up the boardState itself,
        // excluding the padding:
        val BOARD_INDEXES = HashSet<Int>()

        // An efficient array for testing if a particular index is on the board.
        private val ON_BOARD = BooleanArray(142) { false }

        // Holds the nice name for a given index on the board, e.g. "f3".
        private val SQUARES = Array(142) { "" }

        init {
            for (rank in 7 downTo 0) {
                for (file in 0..7) {
                    val index = indexOf(file, rank)
                    BOARD_INDEXES.add(index)
                    ON_BOARD[index] = true
                    SQUARES[index] = "" + (97+file).toChar() + (rank+1)
                }
            }
        }


        fun indexOf(file: Int, rank: Int) : Int {
            return 25 + 12*rank + file
        }

        fun fromFEN(fen: String) : BoardState {

            val tokenizer = StringTokenizer(fen)

            val board = boardFromFENString(tokenizer.nextToken())

            val boardState = BoardState(board)
            boardState.whoseTurn = if (tokenizer.nextToken() == "w") WHITE else BLACK

            val whoCanCastle = tokenizer.nextToken()

            if (whoCanCastle == "-" || !whoCanCastle.contains("K")) {
                boardState.sideData[WHITE].canKingSideCastle = false
            }
            if (whoCanCastle == "-" || !whoCanCastle.contains("Q")) {
                boardState.sideData[WHITE].canQueenSideCastle = false
            }
            if (whoCanCastle == "-" || !whoCanCastle.contains("k")) {
                boardState.sideData[BLACK].canKingSideCastle = false
            }
            if (whoCanCastle == "-" || !whoCanCastle.contains("q")) {
                boardState.sideData[BLACK].canQueenSideCastle = false
            }

            boardState.sideData[boardState.whoseTurn].isInCheck = boardState.isKingInCheck(boardState.whoseTurn)

            val enPassantCaptureSquare = tokenizer.nextToken()
            if (enPassantCaptureSquare != "-") {
                boardState.enPassantCapturePos = squareFor(enPassantCaptureSquare)
            }

            boardState.halfMovesSinceCaptureOrPawnAdvance = tokenizer.nextToken().toInt()

            boardState.moveNumber = tokenizer.nextToken().toInt()

            return boardState
        }

        private fun boardFromFENString(boardString: String): Array<Piece?> {
            val board = Array<Piece?>(ON_BOARD.size) { null }
            var i = 0
            var rank = 7
            var file = 0
            while (i < boardString.length) {
                val c = boardString[i++]
                when {
                    c == '/' -> {
                        file = 0
                        rank--
                    }
                    c.isDigit() -> file += Character.getNumericValue(c)
                    else -> {
                        board[indexOf(file, rank)] = Piece.ofChar(c)
                        file += 1
                    }
                }
            }
            return board
        }

        fun onBoard(pos: Int) : Boolean {
            return ON_BOARD[pos]
        }

        fun squareName(pos: Int): String {
            return SQUARES[pos]
        }

        private fun squareFor(square: String) : Int {
            return SQUARES.indexOf(square)
        }

        fun fileChar(pos: Int): Any {
            return SQUARES[pos][0]
        }

        fun rankChar(pos: Int): Any {
            return SQUARES[pos][1]
        }
    }

    data class SideData(val homeRankStart: Int, val pawnHomeRankStart: Int,
                        val aboutToPromoteRankStart: Int, val pawnMoveDirection: Int,
                        var kingPos: Int = homeRankStart+4, var isInCheck: Boolean = false,
                        var canQueenSideCastle: Boolean = true, var canKingSideCastle: Boolean = true) {

        fun kingHomePos() : Int {
            return homeRankStart + 4
        }

        fun QueenSideRookPos() : Int {
            return homeRankStart
        }

        fun KingSideRookPos() : Int {
            return homeRankStart
        }

        fun isHomeRank(pos: Int) : Boolean {
            return homeRankStart <= pos && pos <= homeRankStart + 7
        }

        fun isPawnHomeRank(pos: Int) : Boolean {
            return pawnHomeRankStart <= pos && pos <= pawnHomeRankStart + 7
        }

        fun isAboutToPromote(pos: Int) : Boolean {
            return aboutToPromoteRankStart <= pos && pos <= aboutToPromoteRankStart + 7
        }
    }
}
