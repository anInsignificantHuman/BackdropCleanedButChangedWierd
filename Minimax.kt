var bestValue = Int.MIN_VALUE
var bestMovePath = mutableListOf<Pixel>()

fun minimax(backdrop: Backdrop, depth: Int, movePath: List<Pixel> = listOf()): Int {
    if (depth == 0 || backdrop.getAvailableMoves().isEmpty()) {
        return backdrop.calculateScore()
    }

    for (move in backdrop.getAvailableMoves()) {
        val newBackdrop = Backdrop()
        backdrop.pixelsScored.forEach { newBackdrop.scorePixel(it) }
        newBackdrop.scorePixel(move)

        val newValue = minimax(newBackdrop, depth - 1, movePath + move)

        if (bestValue < newValue) {
            bestValue = newValue
            bestMovePath = (movePath + move).toMutableList()
        }
    }

    return bestValue
}


fun main() {
    val backdrop = Backdrop()
    backdrop.scorePixel(Pixel(Hex(1, 0, -1), Color.WHITE))
    backdrop.scorePixel(Pixel(Hex(3, 0, -3), Color.YELLOW))
    backdrop.scorePixel(Pixel(Hex(4, 0, -4), Color.YELLOW))
    backdrop.scorePixel(Pixel(Hex(4, -1, -3), Color.WHITE))

    val timeStart = System.currentTimeMillis()
    println(minimax(backdrop, 5))
    println(bestMovePath)
    backdrop.pixelsScored.addAll(bestMovePath)

    backdrop.calculateScore()
    println("set count: ${backdrop.setCount}, mosaic count: ${backdrop.mosaicCount}")
    println(System.currentTimeMillis() - timeStart)
}
