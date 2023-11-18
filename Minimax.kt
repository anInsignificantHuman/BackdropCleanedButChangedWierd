var bestValue = Int.MIN_VALUE
var bestMovePath = mutableListOf<Pixel>()

fun weightMove(currBackdrop: Backdrop, move: Pixel): Int {
    val tempBackdrop = Backdrop()
    var weight = 0
    currBackdrop.pixelsScored.forEach { tempBackdrop.scorePixel(it) }
    tempBackdrop.scorePixel(move)

    if (tempBackdrop.isPartOfMosaic(move, currBackdrop.filterNonColored())) {
        weight += 10
    }

    if (tempBackdrop.reachesSetLine(move)) {
        weight += 10
    }

    return weight
}

fun minimax(backdrop: Backdrop, depth: Int, movePath: List<Pixel> = listOf(), currentEval: Int = 0): Int {
//    if (depth == 0 || depth == 1) {
//        val move = movePath.last()
//        if (move.color != Color.WHITE && !backdrop.isMosaicPossibleNextTwoMoves(move, backdrop)) return Int.MIN_VALUE
//    }

    if (depth == 0 || backdrop.getAvailableMoves().isEmpty()) {
        return backdrop.calculateScore()
    }

    if (backdrop.calculateScore() + 20 * depth < bestValue) {
        return Int.MIN_VALUE
    }

    if (backdrop.calculateScore() < currentEval) {
        return Int.MIN_VALUE
    }

//    backdrop.getAvailableMoves().sortedByDescending {
//        weightMove(backdrop, it)
//    }

    for (move in backdrop.getAvailableMoves()) {
        val newBackdrop = Backdrop()
        backdrop.pixelsScored.forEach { newBackdrop.scorePixel(it) }

        newBackdrop.scorePixel(move)

        val newValue = minimax(newBackdrop, depth - 1, movePath + move, backdrop.calculateScore())

        if (bestValue < newValue) {
            bestValue = newValue
            bestMovePath = (movePath + move).toMutableList()
        }
    }

    return bestValue
}

fun main() {
    val backdrop = Backdrop()
//    backdrop.scorePixel(Pixel(Hex(1, 0, -1), Color.WHITE))
//    backdrop.scorePixel(Pixel(Hex(3, 0, -3), Color.YELLOW))
//    backdrop.scorePixel(Pixel(Hex(4, 0, -4), Color.YELLOW))
//    backdrop.scorePixel(Pixel(Hex(4, -1, -3), Color.WHITE))
    println(backdrop.pixelsLeft)

    val timeStart = System.currentTimeMillis()

    for (i in 0..1) {
        println(minimax(backdrop, 5))
        println(bestMovePath)
        backdrop.scorePixel(*bestMovePath.toTypedArray())
        println(backdrop.pixelsLeft)
    }

    for (i in 0..4){
        println(minimax(backdrop, 6))
        println(bestMovePath)
        backdrop.scorePixel(*bestMovePath.toTypedArray())
        println(backdrop.pixelsLeft)
    }

    backdrop.printBoard()

    println(System.currentTimeMillis() - timeStart)
//    backdrop.scorePixel(Pixel(Hex(0, 0, 0), Color.YELLOW))
//    backdrop.scorePixel(Pixel(Hex(1, 0, -1), Color.YELLOW))
//    backdrop.scorePixel(Pixel(Hex(1, -1, 0), Color.YELLOW))
//
//    println(backdrop.calculateScore())
}

