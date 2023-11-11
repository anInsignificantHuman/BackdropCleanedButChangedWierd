class Backdrop2 {
    private val canUseSetBonus = mutableListOf(true, true, true)
    var pixelsScored = mutableListOf<Pixel>()

    var mosaicCount = 0
    var setCount = 0

    private fun inPixelsScored(pixel: Hex) = pixelsScored.any { it.hex.q == pixel.q && it.hex.r == pixel.r && it.hex.s == pixel.s }

    fun scorePixel(pixel: Pixel): Backdrop2 {
        val pixelOffset = pixel.hex.cubeToOffset()

        if (pixelsScored.any {
                it.hex.q == pixel.hex.neighbor(Direction.BOTTOM_LEFT).q && it.hex.r == pixel.hex.neighbor(
                    Direction.BOTTOM_LEFT
                ).r && it.hex.s == pixel.hex.neighbor(Direction.BOTTOM_LEFT).s
            } && pixelsScored.any {
                it.hex.q == pixel.hex.neighbor(Direction.BOTTOM_RIGHT).q && it.hex.r == pixel.hex.neighbor(
                    Direction.BOTTOM_RIGHT
                ).r && it.hex.s == pixel.hex.neighbor(Direction.BOTTOM_RIGHT).s
            }) {
            pixelsScored.add(pixel)
        } else if (pixel.hex.r == 0) {
            pixelsScored.add(pixel)
        } else if (pixelOffset.x == 0 && pixelOffset.y % 2 == 1 && pixelsScored.any {
                it.hex.q == pixel.hex.neighbor(
                    Direction.BOTTOM_RIGHT
                ).q && it.hex.r == pixel.hex.neighbor(Direction.BOTTOM_RIGHT).r && it.hex.s == pixel.hex.neighbor(
                    Direction.BOTTOM_RIGHT
                ).s
            }) {
            pixelsScored.add(pixel)
        } else if (pixelOffset.x == 6 && pixelOffset.y % 2 == 1) {
            pixelsScored.add(pixel)
        } else {
            println("Unable to add your pixel because it does not obey pixel addition constraints")
        }
        return this
    }

    private fun countScorePixels(): Int {
        var score = 0
        pixelsScored.forEach { _ -> score += Points.PIXEL_POINTS.amount }
        return score
    }

    fun filterNonColored() = pixelsScored.filter { it.color != Color.WHITE }

    fun reachesSetLine(pixel: Pixel): Boolean {
        if (pixel.hex.cubeToOffset().y >= 2 && canUseSetBonus[0]) {
            canUseSetBonus[0] = false
            return true
        } else if (pixel.hex.cubeToOffset().y >= 5 && canUseSetBonus[1]) {
            canUseSetBonus[1] = false
            return true
        } else if (pixel.hex.cubeToOffset().y >= 8 && canUseSetBonus[2]) {
            canUseSetBonus[2] = false
            return true
        }

        return false
    }

    fun isPartOfMosaic(pixel: Pixel, colored: List<Pixel>): Boolean {
        val neighbors = pixel.getNeighborsFromList(colored)
        val neighborOneNeighbors: List<Pixel>
        val neighborTwoNeighbors: List<Pixel>

        if (neighbors.size == 2) {

            neighborOneNeighbors = neighbors[0].getNeighborsFromList(colored)
            neighborTwoNeighbors = neighbors[1].getNeighborsFromList(colored)

            if ((neighborOneNeighbors.size == 2 && neighborTwoNeighbors.size == 2) && (Hex.areNeighbors(
                    neighbors[0].hex,
                    neighbors[1].hex
                ))
            ) {

                if ((pixel.color == neighbors[0].color && pixel.color == neighbors[1].color)) {
                    return true
                } else if (pixel.color != neighbors[0].color && neighbors[0].color != neighbors[1].color && pixel.color != neighbors[1].color) {
                    return true
                }

            }

        }

        return false
    }

    fun calculateScore(): Int {
        var score = 0
        val colored = filterNonColored()
        var neighbors: MutableList<Pixel>
        val pixelsToRemove = mutableListOf<Pixel>()

        score += countScorePixels()

        for (pixel in pixelsScored) {
            if (reachesSetLine(pixel)) {
                score += Points.SET_BONUS.amount
                setCount++
            }
        }

        for (pixel in colored) {
            if (pixel in pixelsToRemove) {
                continue
            }

            if (isPartOfMosaic(pixel, colored)) {
                neighbors = pixel.getNeighborsFromList(colored) as MutableList<Pixel>
                score += Points.MOSAIC_BONUS.amount
                mosaicCount++
                pixelsToRemove += mutableListOf(pixel, neighbors[0], neighbors[1])
            }
        }

        return score
    }

    fun sortBoard() {
//        pixelsScored.sortBy { it.hex.q }
//        pixelsScored.sortByDescending { it.hex.r }
    }

    private fun populateAvailableMoves(pixel: Hex, direction: Direction): Collection<Pixel> {
        return Color.values().map { Pixel(pixel.neighbor(direction), it) }
    }

    private fun pixelPlaceable(pixel: Pixel, direction1: Direction, direction2: Direction): Boolean {
        return pixel.hasNeighborInList(
            pixelsScored,
            direction1
        ) && !(this.inPixelsScored(pixel.hex.neighbor((direction2))))
    }

    private fun pixelPlaceableOddCorners(pixel: Pixel): Boolean {
        return (pixel.hex.cubeToOffset().x == 0 && !(this.inPixelsScored(pixel.hex.neighbor((Direction.TOP_LEFT))))) || (pixel.hex.cubeToOffset().x == 5 && !(this.inPixelsScored(
            pixel.hex.neighbor((Direction.TOP_RIGHT))
        )))
    }

    fun getAvailableMoves(): HashSet<Pixel> {
        var availableMoves = hashSetOf<Pixel>()
        val evenFlags = mutableListOf(false, false, false, false, false, false)

        for (pixel in pixelsScored.filter { it.hex.r == 0 }) {
            evenFlags[pixel.hex.cubeToOffset().x] = true
        }

        evenFlags.forEachIndexed { index, flag ->
            if (!flag) {
                Color.values().forEach { availableMoves.add(Pixel((Offset(index, 0)).offsetToCube(), it)) }
            }
        }

        for (pixel in pixelsScored.filter { it.hex.r % 2 != 0 && it.hex.cubeToOffset().y <= 9 }) {
            if (pixelPlaceable(pixel, Direction.LEFT, Direction.TOP_LEFT)) {
                availableMoves.addAll(populateAvailableMoves(pixel.hex, Direction.TOP_LEFT))

            } else if (pixelPlaceable(pixel, Direction.RIGHT, Direction.TOP_RIGHT)) {
                availableMoves.addAll(populateAvailableMoves(pixel.hex, Direction.TOP_RIGHT))
            }
        }

        for (pixel in pixelsScored.filter { it.hex.r % 2 == 0 && it.hex.cubeToOffset().y < 10 }) {
            if (pixelPlaceable(pixel, Direction.RIGHT, Direction.TOP_RIGHT)) {
                availableMoves.addAll(populateAvailableMoves(pixel.hex, Direction.TOP_RIGHT))

            }
            if (pixelPlaceable(pixel, Direction.RIGHT, Direction.TOP_RIGHT)) {
                availableMoves.addAll(populateAvailableMoves(pixel.hex, Direction.TOP_RIGHT))
            }
            if (pixelPlaceableOddCorners(pixel)) {
                availableMoves.addAll(populateAvailableMoves(pixel.hex, Direction.TOP_LEFT))
            } else if (pixelPlaceableOddCorners(pixel)) {
                availableMoves.addAll(populateAvailableMoves(pixel.hex, Direction.TOP_RIGHT))
            }
        }

        availableMoves =
            availableMoves.distinctBy { "${it.hex.q}-${it.hex.r}-${it.hex.s}-${it.color}" }.toHashSet()

        return availableMoves
    }
}

fun main() {
//    println()
//
//    val backdrop = Backdrop()
//    val pixel1 = Pixel(Hex(0, 0, 0), Color.WHITE)
//    val pixel2 = Pixel(Hex(1, 0, -1), Color.WHITE)
//    val pixel3 = Pixel(Hex(0, -1, 1), Color.GREEN)
//    val pixel4 = Pixel(Hex(1, -1, 0), Color.GREEN)
//    val pixel5 = Pixel(Hex(1, -2, 1), Color.GREEN)
//
//    backdrop.scorePixel(pixel1)
//    backdrop.scorePixel(pixel2)
//    backdrop.scorePixel(pixel3)
//    backdrop.scorePixel(pixel4)
//    backdrop.scorePixel(pixel5)
//
//    backdrop.sortBoard()
//
//    println(backdrop.calculateScore())
//    println(backdrop.getAvailableMoves())

}
