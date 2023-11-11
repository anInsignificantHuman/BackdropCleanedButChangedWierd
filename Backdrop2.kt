class Backdrop2() {

    var pixelsScored = mutableListOf<Pixel>()

//    constructor(preloads: MutableList<Pixel>) : this() {
//        pixelsScored = preloads
//    }

    private fun inPixelsScored(pixel: Hex): Boolean = pixelsScored.any { it.hex.q == pixel.q && it.hex.r == pixel.r && it.hex.s == pixel.s }


    fun scorePixel(pixel: Pixel): Backdrop2 {
        val pixelOffset = pixel.hex.cubeToOffset()

        pixelsScored.forEach {
            if(pixel.compareNoColor(it)) {
                println("cannot add pixel because pixel already added")
            }
        }

        if(pixelsScored.any { it.hex.q == pixel.hex.neighbor(Direction.BOTTOM_LEFT).q && it.hex.r == pixel.hex.neighbor(Direction.BOTTOM_LEFT).r && it.hex.s == pixel.hex.neighbor(Direction.BOTTOM_LEFT).s } && pixelsScored.any { it.hex.q == pixel.hex.neighbor(Direction.BOTTOM_RIGHT).q && it.hex.r == pixel.hex.neighbor(Direction.BOTTOM_RIGHT).r && it.hex.s == pixel.hex.neighbor(Direction.BOTTOM_RIGHT).s }) {
            pixelsScored.add(pixel)
        }
        else if(pixel.hex.r == 0) {
            pixelsScored.add(pixel)
        }
        else if(pixelOffset.x == 0 && pixelOffset.y % 2 == 1 && pixelsScored.any { it.hex.q == pixel.hex.neighbor(Direction.BOTTOM_RIGHT).q && it.hex.r == pixel.hex.neighbor(Direction.BOTTOM_RIGHT).r && it.hex.s == pixel.hex.neighbor(Direction.BOTTOM_RIGHT).s }) {
            pixelsScored.add(pixel)
        }
        else if(pixelOffset.x == 6 && pixelOffset.y % 2 == 1) {
            pixelsScored.add(pixel)
        }
        else {
            println("Unable to add your pixel because it does not obey pixel addition constraints")
        }
        return this
    }

    var mosaicCount = 0
    var setCount = 0

    fun calculateScore(): Int {
        var score = 0
        val colored = mutableListOf<Pixel>()
        var currNeighbors: MutableList<Pixel>
        var neighborOneNeighbors: MutableList<Pixel>
        var neighborTwoNeighbors: MutableList<Pixel>
        var pixelsToRemove = mutableListOf<Pixel>()
        val canUseSetBonus = mutableListOf(0, 0, 0)

        for(pixel in pixelsScored) {
            score += Points.PIXEL_POINTS.amount

            if(pixel.color != Color.WHITE) {
                colored.add(pixel)
            }

            if((pixel.hex.cubeToOffset()).y >= 8 && canUseSetBonus[2] == 0) {
                score += Points.SET_BONUS.amount
                canUseSetBonus[2] = 1
                setCount++
            }
            else if((pixel.hex.cubeToOffset()).y >= 5 && canUseSetBonus[1] == 0) {
                score += Points.SET_BONUS.amount
                canUseSetBonus[1] = 1
                setCount++
            }
            else if((pixel.hex.cubeToOffset()).y >= 2 && canUseSetBonus[0] == 0) {
                score += Points.SET_BONUS.amount
                canUseSetBonus[0] = 1
                setCount++
            }

        }

        for(pixel in colored) {

            if(pixel in pixelsToRemove) { continue }

            currNeighbors = pixel.getNeighborsFromList(colored) as MutableList<Pixel>

            if(currNeighbors.size == 2) {

                neighborOneNeighbors = currNeighbors[0].getNeighborsFromList(colored) as MutableList<Pixel>
                neighborTwoNeighbors = currNeighbors[1].getNeighborsFromList(colored) as MutableList<Pixel>

                if(neighborOneNeighbors.size == 2 && neighborTwoNeighbors.size == 2) {

                    if((pixel.color == currNeighbors[0].color && pixel.color == currNeighbors[1].color)) {
                        score += Points.MOSAIC_BONUS.amount
                        mosaicCount++
                        pixelsToRemove = mutableListOf(pixel, currNeighbors[0], currNeighbors[1])
                    }

                    else if((pixel.color != currNeighbors[0].color && currNeighbors[0].color != currNeighbors[1].color && pixel.color != currNeighbors[1].color) && (neighborOneNeighbors[0].compareNoColor(neighborTwoNeighbors[0]) || neighborOneNeighbors[1].compareNoColor(neighborTwoNeighbors[1]))) {
                        score += Points.MOSAIC_BONUS.amount
                        mosaicCount++
                        pixelsToRemove = mutableListOf(pixel, currNeighbors[0], currNeighbors[1])
                    }

                }

            }
//            colored.removeAll(pixelsToRemove)
        }

        return score

    }

    fun sortBoard() {
        pixelsScored.sortBy { it.hex.q }
        pixelsScored.sortByDescending { it.hex.r }
    }

//    fun printBoard(): String {
////        val evenRow = mutableListOf(" X", "X", "X", "X", "X", "X \n")
////        val oddRow  = mutableListOf("X", "X", "X", "X", "X", "X", "X\n")
////
////        var board = mutableListOf<List<String>>()
////        for (i in 0..10) {
////            board.add(if (i % 2 == 0) evenRow else oddRow)
////        }
////
////        for (pixel in pixelsScored) {
////            val x = pixel.hex.cubeToOffset().x
////            val y = pixel.hex.cubeToOffset().y
////
////            when (pixel.color) {
////                Color.WHITE -> board[y]
////                Color.YELLOW -> board[y][x] = ""
////                Color.PURPLE -> TODO()
////                Color.GREEN -> TODO()
////            }
////        }
//
////        sortBoard()
////
////        for (i in 10 downTo 0) {
////            val rowMembers = pixelsScored.filter { it.hex.cubeToOffset().y == i }
////
////            var evenRow = mutableListOf(" X  ", "X  ", "X  ", "X  ", "X  ", "X \n")
////            var oddRow  = mutableListOf("X  ", "X  ", "X  ", "X  ", "X  ", "X  ", "X\n")
////
////            for (pixel in rowMembers) {
////                val x = pixel.hex.cubeToOffset().x
////                if (i % 2 == 0) {
////                    evenRow[x].replace()
////                }
////            }
////        }
////        return board
//    }


    private fun populateAvailableMoves(pixel: Hex): MutableList<Pixel> {
        val moves = mutableListOf<Pixel>()
        Color.values().forEach { moves.add(Pixel(pixel.neighbor(Direction.TOP_RIGHT), it)) }
        return moves
    }

    fun getAvailableMoves(): List<Pixel> {

        var availableMoves = mutableListOf<Pixel>()
        val evenFlags = mutableListOf(false, false, false, false, false, false)

        for(pixel in pixelsScored.filter { it.hex.r == 0 }) {
            evenFlags[pixel.hex.cubeToOffset().x] = true
        }

        evenFlags.forEachIndexed { index, flag ->
            if(!flag) {
                availableMoves.add(Pixel((Offset(index, 0)).offsetToCube(), Color.WHITE))
                availableMoves.add(Pixel((Offset(index, 0)).offsetToCube(), Color.GREEN))
                availableMoves.add(Pixel((Offset(index, 0)).offsetToCube(), Color.YELLOW))
                availableMoves.add(Pixel((Offset(index, 0)).offsetToCube(), Color.PURPLE))
            }
        }

        for(pixel in pixelsScored.filter { it.hex.r % 2 != 0 && it.hex.cubeToOffset().y <= 9 }) {
            if(pixel.hasNeighborInList(pixelsScored, Direction.LEFT) && !(this.inPixelsScored(pixel.hex.neighbor((Direction.TOP_LEFT))))) {
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_LEFT), Color.WHITE))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_LEFT), Color.GREEN))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_LEFT), Color.YELLOW))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_LEFT), Color.PURPLE))

            }
            else if(pixel.hasNeighborInList(pixelsScored, Direction.RIGHT) && !(this.inPixelsScored(pixel.hex.neighbor((Direction.TOP_RIGHT))))) {
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.WHITE))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.GREEN))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.YELLOW))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.PURPLE))
            }
        }

        for(pixel in pixelsScored.filter { it.hex.r % 2 == 0 && it.hex.cubeToOffset().y < 10 }) {
            if(pixel.hasNeighborInList(pixelsScored, Direction.RIGHT) && !(this.inPixelsScored(pixel.hex.neighbor((Direction.TOP_RIGHT))))) {
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.WHITE))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.GREEN))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.YELLOW))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.PURPLE))

            }
            if(pixel.hasNeighborInList(pixelsScored, Direction.RIGHT) && !(this.inPixelsScored(pixel.hex.neighbor((Direction.TOP_RIGHT))))) {
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.WHITE))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.GREEN))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.YELLOW))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.PURPLE))
            }
            if(pixel.hex.cubeToOffset().x == 0 && !(this.inPixelsScored(pixel.hex.neighbor((Direction.TOP_LEFT))))) {
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_LEFT), Color.WHITE))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_LEFT), Color.GREEN))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_LEFT), Color.YELLOW))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_LEFT), Color.PURPLE))
            }
            else if(pixel.hex.cubeToOffset().x == 5 && !(this.inPixelsScored(pixel.hex.neighbor((Direction.TOP_RIGHT))))) {
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.WHITE))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.GREEN))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.YELLOW))
                availableMoves.add(Pixel(pixel.hex.neighbor(Direction.TOP_RIGHT), Color.PURPLE))
            }
        }

        availableMoves = availableMoves.distinctBy { "${ it.hex.q }-${ it.hex.r }-${ it.hex.s }-${ it.color }" } as MutableList<Pixel>

        return availableMoves

    }

}

//fun main() {
//    println()
//    val backdrop = Backdrop()
//    val pixel1 = Pixel(Hex(1, 0, -1), Color.WHITE)
//    val pixel2 = Pixel(Hex(2, 0, -2), Color.WHITE)
//    val pixel3 = Pixel(Hex(3, 0, -3), Color.WHITE)
//    val pixel4 = Pixel(Hex(4, 0, -4), Color.WHITE)
//    val pixel5 = Pixel(Hex(2, -1, -1), Color.GREEN)
//    val pixel6 = Pixel(Hex(3, -1, -2), Color.PURPLE)
//    val pixel7 = Pixel(Hex(4, -1, -3), Color.PURPLE)
//    val pixel8 = Pixel(Hex(3, -2, -1), Color.YELLOW)
//
//    backdrop.scorePixel(pixel1)
//    backdrop.scorePixel(pixel2)
//    backdrop.scorePixel(pixel3)
//    backdrop.scorePixel(pixel4)
//    backdrop.scorePixel(pixel5)
//    backdrop.scorePixel(pixel6)
//    backdrop.scorePixel(pixel7)
//    backdrop.scorePixel(pixel8)
//
//    backdrop.sortBoard()
//
//    println(backdrop.calculateScore())
//    println(backdrop.getAvailableMoves())
//
//}