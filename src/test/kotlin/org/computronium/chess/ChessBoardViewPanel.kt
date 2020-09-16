package org.computronium.chess

import java.awt.*
import java.awt.geom.*
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.*
import javax.swing.border.BevelBorder

/**
 * Chessboard renderer, converted from a StackOverflow answer in Java.
 *
 * Adapted from code posted to StackOverflow by Andrew Thompson (https://stackoverflow.com/a/18686753/215403).
 */
internal class ChessBoardViewPanel : JPanel() {

    companion object {
        /**
         * Unicode values for chess pieces.
         */
        private val pieceLookup = mapOf(
            Pair(PieceType.KING, "\u2654"),
            Pair(PieceType.QUEEN, "\u2655"),
            Pair(PieceType.ROOK, "\u2656"),
            Pair(PieceType.BISHOP, "\u2657"),
            Pair(PieceType.KNIGHT, "\u2658"),
            Pair(PieceType.PAWN, "\u2659"))

        private val EN_PASSANT_CAPTURE_SQUARE_COLOR = Color(20, 112,33)
    }

    /*
     * Colors..
     */
    private val outlineColor = Color.DARK_GRAY
    private val pieceColors = arrayOf(Color(203, 203, 197), Color(192, 142, 60))
    private val squareColors = arrayOf(Color.BLACK, Color.WHITE)

    private val innerPanel = JPanel()
    private val descriptionPanel = JTextArea()

    init {
        font = Font("Sans-Serif", Font.PLAIN, 46)
        border = BevelBorder(BevelBorder.LOWERED, Color.GRAY.brighter(), Color.GRAY, Color.GRAY.darker(), Color.GRAY)

        innerPanel.layout = GridLayout(0, 8, 0, 0)
        innerPanel.preferredSize = Dimension(300, 300)

        descriptionPanel.isEditable = false

        add(innerPanel)
        add(descriptionPanel)
    }

    fun setBoardState(fen: String?) {

        innerPanel.removeAll()
        descriptionPanel.text = ""

        if (fen != null) {
            val boardState = BoardState.fromFEN(fen)
            for (rank in 7 downTo 0) {
                for (file in 0..7) {
                    addLabel(innerPanel, boardState.pieceAt(file, rank), squareColor(rank, file, boardState))
                }
            }
            descriptionPanel.text =
                "FEN: $fen\n" +
                "Fullmove ${boardState.moveNumber}.\n" +
                "${boardState.halfMovesSinceCaptureOrPawnAdvance} half moves since capture or pawn move.\n" +
                "${if (boardState.whoseTurn == BoardState.WHITE) "White" else "Black"}'s turn.\n" +
                "White ${if (boardState.sideConfig[BoardState.WHITE].canKingSideCastle) "can" else "can't"} castle kingside.\n" +
                "White ${if (boardState.sideConfig[BoardState.WHITE].canQueenSideCastle) "can" else "can't"} castle queenside.\n" +
                "Black ${if (boardState.sideConfig[BoardState.BLACK].canKingSideCastle) "can" else "can't"} castle kingside.\n" +
                "Black ${if (boardState.sideConfig[BoardState.BLACK].canQueenSideCastle) "can" else "can't"} castle queenside.\n"
        }

        revalidate()
        repaint()
    }

    private fun squareColor(rank: Int, file: Int, boardState: BoardState) : Color {
        return if (boardState.enPassantCapturePos == BoardState.indexOf(rank, file)) {
            EN_PASSANT_CAPTURE_SQUARE_COLOR
        } else {
            squareColors[(rank + file) % 2]
        }
    }

    private fun separateShapeIntoRegions(shape: Shape): ArrayList<Shape> {
        val regions = ArrayList<Shape>()
        val pi = shape.getPathIterator(null)
        var gp = GeneralPath()
        while (!pi.isDone) {
            val coords = DoubleArray(6)
            val pathSegmentType = pi.currentSegment(coords)
            val windingRule = pi.windingRule
            gp.windingRule = windingRule
            when (pathSegmentType) {
                PathIterator.SEG_MOVETO -> {
                    gp = GeneralPath()
                    gp.windingRule = windingRule
                    gp.moveTo(coords[0], coords[1])
                }
                PathIterator.SEG_LINETO -> {
                    gp.lineTo(coords[0], coords[1])
                }
                PathIterator.SEG_QUADTO -> {
                    gp.quadTo(coords[0], coords[1], coords[2], coords[3])
                }
                PathIterator.SEG_CUBICTO -> {
                    gp.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5])
                }
                PathIterator.SEG_CLOSE -> {
                    gp.closePath()
                    regions.add(Area(gp))
                }
                else -> {
                    System.err.println("Unexpected value! $pathSegmentType")
                }
            }
            pi.next()
        }
        return regions
    }

    private fun getImageForChessPiece(piece: Piece): BufferedImage {
        val sz = font.size
        val bi = BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB)
        val g = bi.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)

        val frc = g.fontRenderContext
        val gv = font.createGlyphVector(frc, pieceLookup[piece.type])
        val shape1 = gv.outline
        val r = shape1.bounds
        val spaceX = sz - r.width
        val spaceY = sz - r.height
        val trans = AffineTransform.getTranslateInstance(-r.x + (spaceX / 2).toDouble(), -r.y + (spaceY / 2).toDouble())
        val shapeCentered = trans.createTransformedShape(shape1)
        val imageShape: Shape = Rectangle2D.Double(0.0, 0.0, sz.toDouble(), sz.toDouble())
        val imageShapeArea = Area(imageShape)
        val shapeArea = Area(shapeCentered)
        imageShapeArea.subtract(shapeArea)
        val regions = separateShapeIntoRegions(imageShapeArea)
        g.stroke = BasicStroke(1F)
        g.color = pieceColors[piece.color]
        val baseColor = pieceColors[piece.color]
        val c1 = baseColor.brighter()
        val gp = GradientPaint((sz / 2 - r.width / 4).toFloat(), (sz / 2 - r.height / 4).toFloat(), c1,
            (sz / 2 + r.width / 4).toFloat(), (sz / 2 + r.height / 4).toFloat(), baseColor, false)
        g.paint = gp
        for (region in regions) {
            val r1 = region.bounds
            if (r1.getX() >= 0.001 || r1.getY() >= 0.001) {
                g.fill(region)
            }
        }
        g.color = outlineColor
        g.fill(shapeArea)
        g.dispose()
        return bi
    }

    private fun addLabel(c: Container, piece: Piece?, bg: Color?) {
        val label: JLabel = if (piece == null) {
            JLabel()
        } else {
            JLabel(ImageIcon(getImageForChessPiece(piece)), JLabel.CENTER)
        }
        label.background = bg
        label.isOpaque = true
        c.add(label)
    }
}
