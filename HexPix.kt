import kotlin.math.abs

enum class Color {
    WHITE,
    GREEN,
    YELLOW,
    PURPLE
}

enum class Points(val amount: Int) {
    PIXEL_POINTS(3),
    SET_BONUS(10),
    MOSAIC_BONUS(10)
}

enum class Direction(val offset: Hex) {
    LEFT(Hex(-1, 0, 1)),
    RIGHT(Hex(1, 0, -1)),
    TOP_LEFT(Hex(0, -1, 1)),
    TOP_RIGHT(Hex(1, -1, 0)),
    BOTTOM_LEFT(Hex(-1, 1, 0)),
    BOTTOM_RIGHT(Hex(0, 1, -1))
}

class Offset(val x: Int, val y: Int) {

    init {
        if (y !in 0..10)              { throw IllegalArgumentException() }
        if (y % 2 == 0 && x !in 0..5) { throw IllegalArgumentException() }
        if (y % 2 != 0 && x !in 0..6) { throw IllegalArgumentException() }
    }

    fun offsetToCube(): Hex {
        val q = x - (y + (y and 1)) / 2
        val r = y
        val s = -q - r
        return Hex(q, r, s)
    }

}

class Hex(val q: Int, val r: Int, val s: Int) {

    init { if((q + r + s) != 0) { throw IllegalArgumentException() } }

    override fun toString(): String { return "Hex(q=$q, r=$r, s=$s)" }

    private fun add(b: Hex): Hex { return Hex(this.q + b.q, this.r + b.r, this.s + b.s) }

    private fun subtract(b: Hex): Hex { return Hex(this.q - b.q, this.r - b.r, this.s - b.s) }

    fun neighbor(direction: Direction): Hex { return add(direction.offset) }

    private fun length(): Int { return ((abs(q) + abs(r) + abs(s)) / 2) }

    fun cubeToOffset(): Offset {
        val col = abs(q + (r + (r and 1)) / 2)
        val row = abs(r)
        return Offset(col, row)
    }

    fun areEqual(b: Hex): Boolean { return (this.q == b.q && this.r == b.r && this.s == b.s) }

    fun allNeighbors(): List<Hex> = Direction.values().map { neighbor(it) }

    companion object {

        fun areNeighbors(a: Hex, b: Hex): Boolean = a.allNeighbors().any { it.areEqual(b) }

    }
}
class Pixel(val hex: Hex, val color: Color) {

    override fun toString(): String {
        return "Pixel(hex=$hex, color=$color)"
    }

    fun getNeighborsFromList(pixels: List<Pixel>): List<Pixel> {
        return pixels.filter { Hex.areNeighbors(this.hex, it.hex) }
    }

    fun hasNeighborInList(pixels: List<Pixel>, direction: Direction): Boolean {
        val neighbor = hex.neighbor(direction)
        return getNeighborsFromList(pixels).any { it.hex.areEqual(neighbor) }
    }

    fun compareWithColor(pixel: Pixel): Boolean { return compareNoColor(pixel) && this.color == pixel.color }

    fun compareNoColor(pixel: Pixel): Boolean { return this.hex.q == pixel.hex.q && this.hex.r == pixel.hex.r && this.hex.s == pixel.hex.s }

}