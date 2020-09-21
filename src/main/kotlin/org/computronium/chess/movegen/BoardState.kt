package org.computronium.chess.movegen

import java.util.*

/**
 * The main class representing a complete board state.
 */
class BoardState(private val board: Array<Piece?>) : IBoardState {


    val sideConfig = arrayOf(
            SideConfig(
                    25,
                    37,
                    97,
                    12,
                    29),
            SideConfig(
                    109,
                    97,
                    37,
                    -12,
                    113
            ))

    override var whoseTurn = WHITE

    override var moveNumber = 0

    override var enPassantCapturePos: Int? = null

    override var halfMovesSinceCaptureOrPawnAdvance = 0
    
    fun copy() : BoardState {
        val boardCopy = board.copyOf()
        val boardStateCopy = BoardState(boardCopy)
        boardStateCopy.whoseTurn = whoseTurn
        boardStateCopy.moveNumber = moveNumber
        boardStateCopy.enPassantCapturePos = enPassantCapturePos
        boardStateCopy.halfMovesSinceCaptureOrPawnAdvance = halfMovesSinceCaptureOrPawnAdvance
        boardStateCopy.sideConfig[WHITE].copyFrom(sideConfig[WHITE])
        boardStateCopy.sideConfig[BLACK].copyFrom(sideConfig[BLACK])
        return boardStateCopy
    }

    init {
        // Save the coordinate for each king, so we can figure out if the king is in check.
        for (index in BOARD_INDEXES) {
            if (board[index]?.type == PieceType.KING) {
                sideConfig[board[index]?.color!!].kingPos = index
            }
        }

        // TODO set canCastle if (boardState[whoseTurnConfig().kingHomePos()] == )
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

    override fun pieceAt(file: Int, rank: Int) : Piece? {
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

        if (!sideConfig[WHITE].canKingSideCastle &&
            !sideConfig[WHITE].canQueenSideCastle &&
            !sideConfig[BLACK].canKingSideCastle &&
            !sideConfig[BLACK].canQueenSideCastle) {
            sb.append("-")
        } else {
            if (sideConfig[WHITE].canKingSideCastle) sb.append("K")
            if (sideConfig[WHITE].canQueenSideCastle) sb.append("Q")
            if (sideConfig[BLACK].canKingSideCastle) sb.append("k")
            if (sideConfig[BLACK].canQueenSideCastle) sb.append("q")
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
    }

    fun piecePositions(color: Int): List<Int> {
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
        val pawnIndex = pos - whoseTurnConfig().pawnMoveDirection
        return isPieceOfType(PieceType.PAWN, pawnIndex-1, attackingColor) ||
                isPieceOfType(PieceType.PAWN, pawnIndex+1, attackingColor)
    }

    private fun isAttackedByKnight(pos: Int, attackingColor: Int): Boolean {
        for (offset in KNIGHT_MOVE_OFFSETS) {
            if (isPieceOfType(PieceType.PAWN, pos+offset, attackingColor)) {
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

    fun whoseTurnConfig() : SideConfig {
        return sideConfig[whoseTurn]
    }

    fun isKingInCheck(color: Int): Boolean {
        return isAttacked(sideConfig[color].kingPos, 1-color)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoardState

        if (!board.contentEquals(other.board)) return false
        if (!sideConfig.contentEquals(other.sideConfig)) return false
        if (whoseTurn != other.whoseTurn) return false
        if (moveNumber != other.moveNumber) return false
        if (enPassantCapturePos != other.enPassantCapturePos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.contentHashCode()
        result = 31 * result + sideConfig.contentHashCode()
        result = 31 * result + whoseTurn
        result = 31 * result + moveNumber
        result = 31 * result + (enPassantCapturePos ?: 0)
        return result
    }


    companion object {

        const val WHITE = 0
        const val BLACK = 1

        val ROOK_MOVE_OFFSETS = arrayOf(-12, -1, 1, 12)
        val QUEEN_MOVE_OFFSETS = arrayOf(-13, -12, -11, -1, 1, 11, 12, 13)
        val KNIGHT_MOVE_OFFSETS = arrayOf(-25, -23, -14, -10, 10, 14, 23, 25)
        val KING_MOVE_OFFSETS = arrayOf(-13, -12, -11, -1, 1, 11, 12, 13)
        val BISHOP_MOVE_OFFSETS = arrayOf(-13, -11, 11, 13)

        // The boardState is implemented as a single array that wraps around, with padding around
        // the outside so that we can easily check for a piece trying to move off the boardState.
        // These are the resulting array indexes of the squares that make up the boardState itself,
        // excluding the padding:
        val BOARD_INDEXES = HashSet<Int>()

        // An efficient array for testing if a particular index is on the boardState.
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
                boardState.sideConfig[WHITE].canKingSideCastle = false
            }
            if (whoCanCastle == "-" || !whoCanCastle.contains("Q")) {
                boardState.sideConfig[WHITE].canQueenSideCastle = false
            }
            if (whoCanCastle == "-" || !whoCanCastle.contains("k")) {
                boardState.sideConfig[BLACK].canKingSideCastle = false
            }
            if (whoCanCastle == "-" || !whoCanCastle.contains("q")) {
                boardState.sideConfig[BLACK].canQueenSideCastle = false
            }

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

    data class SideConfig(val homeRankStart: Int, val pawnHomeRankStart: Int,
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

        fun copyFrom(other: SideConfig) {
            kingPos = other.kingPos
            isInCheck = other.isInCheck
            canQueenSideCastle = other.canQueenSideCastle
            canKingSideCastle = other.canKingSideCastle
        }
    }
}
