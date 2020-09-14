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
 * @author Andrew Thompson
 * @see {@link https://stackoverflow.com/a/18686753/215403}
 */
internal class ChessBoard : JPanel {

    constructor(boardState: IBoardState) : super(GridLayout(0, 8, 0, 0)) {

        border = BevelBorder(BevelBorder.LOWERED, Color.GRAY.brighter(), Color.GRAY, Color.GRAY.darker(), Color.GRAY)

        for (rank in 7 downTo 0) {
            for (file in 0..7) {
                addLabel(this, boardState.pieceAt(file, rank), squareColors[(rank+file)%2], true)
            }
        }
    }

    companion object {
        private const val KING = 0
        private const val QUEEN = 1
        private const val CASTLE = 2
        private const val BISHOP = 3
        private const val KNIGHT = 4
        private const val PAWN = 5

        private const val WHITE = 0
        private const val BLACK = 1

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

        @JvmStatic
        fun main(args: Array<String>) {
            val r = Runnable {
                val chessboard = ChessBoard(BoardState.fromFEN("1k6/5P2/8/8/8/8/3K4/8 b - - 21 1"))
                val frame = JFrame("board")
                frame.size = Dimension(300, 300)
                frame.add(chessboard)
                frame.isVisible = true
            }
            SwingUtilities.invokeLater(r)
        }

//        @JvmStatic
//        fun main(args: Array<String>) {
//
//            val pawnRow = intArrayOf(PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN)
//
//            val r = Runnable {
//
//                val gradientFill = true
//                val gui = ChessBoard()
//                gui.border = BevelBorder(BevelBorder.LOWERED, Color.GRAY.brighter(), Color.GRAY, Color.GRAY.darker(), Color.GRAY)
//                // set up a chess board
//                gui.addPiecesToContainer(gui, WHITE, BLACK, order, gradientFill)
//                gui.addPiecesToContainer(gui, BLACK, BLACK, pawnRow, gradientFill)
//                gui.addBlankLabelRow(gui, WHITE)
//                gui.addBlankLabelRow(gui, BLACK)
//                gui.addBlankLabelRow(gui, WHITE)
//                gui.addBlankLabelRow(gui, BLACK)
//                gui.addPiecesToContainer(gui, WHITE, WHITE, pawnRow, gradientFill)
//                gui.addPiecesToContainer(gui, BLACK, WHITE, order, gradientFill)
//                JOptionPane.showMessageDialog(null, gui, "Chessboard", JOptionPane.INFORMATION_MESSAGE)
//                val tileSet = JPanel(GridLayout(0, 6, 0, 0))
//                tileSet.isOpaque = false
//                val tileSetOrder = intArrayOf(KING, QUEEN, CASTLE, KNIGHT, BISHOP, PAWN)
//                gui.addPiecesToContainer(tileSet, Color(0, 0, 0, 0), BLACK, tileSetOrder, gradientFill)
//                gui.addPiecesToContainer(tileSet, Color(0, 0, 0, 0), WHITE, tileSetOrder, gradientFill)
//            }
//            SwingUtilities.invokeLater(r)
//        }
    }

    /*
     * Colors..
     */
    private val outlineColor = Color.DARK_GRAY
    private val pieceColors = arrayOf(Color(203, 203, 197), Color(192, 142, 60))
    private val squareColors = arrayOf(Color.WHITE, Color.BLACK)

    init {
        font = Font("Sans-Serif", Font.PLAIN, 64)
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

    private fun getImageForChessPiece(piece: Piece, gradient: Boolean): BufferedImage {
        val sz = font.size
        val bi = BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB)
        val g = bi.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)

        val frc = g.fontRenderContext
        val gv = font.createGlyphVector(frc, pieceLookup.get(piece.type))
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
        if (gradient) {
            val c1 = baseColor.brighter()
            val gp = GradientPaint((sz / 2 - r.width / 4).toFloat(), (sz / 2 - r.height / 4).toFloat(), c1,
                (sz / 2 + r.width / 4).toFloat(), (sz / 2 + r.height / 4).toFloat(), baseColor, false)
            g.paint = gp
        } else {
            g.color = baseColor
        }
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

    private fun addLabel(c: Container, piece: Piece?, bg: Color?, gradient: Boolean) {
        val label: JLabel = if (piece == null) {
            JLabel()
        } else {
            JLabel(ImageIcon(getImageForChessPiece(piece, gradient)), JLabel.CENTER)
        }
        label.background = bg
        label.isOpaque = true
        c.add(label)
    }
}