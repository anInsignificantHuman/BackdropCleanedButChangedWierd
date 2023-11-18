import kotlin.math.abs

enum class Color(val tag: String) {
    WHITE("W"),
    GREEN("G"),
    YELLOW("Y"),
    PURPLE("P")
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

data class Hex(val q: Int, val r: Int, val s: Int) {
    init { if((q + r + s) != 0) { throw IllegalArgumentException() } }

    operator fun plus(b: Hex) = Hex(this.q + b.q, this.r + b.r, this.s + b.s)

    operator fun unaryMinus() = Hex(-this.q, -this.r, -this.s)

    operator fun minus(hex: Hex) = this + -hex

    fun neighbor(direction: Direction) = this + direction.offset

    fun cubeToOffset(): Offset {
        val col = abs(q + (r + (r and 1)) / 2)
        val row = abs(r)

        return Offset(col, row)
    }

    override operator fun equals(other: Any?) =
        if (other is Hex)
            this.q == other.q && this.r == other.r && this.s == other.s
        else false

    fun allNeighbors(): List<Hex> = Direction.values().map { neighbor(it) }

    companion object {
        fun areNeighbors(a: Hex, b: Hex): Boolean = a.allNeighbors().any { it == b }
    }
}

data class Pixel(val hex: Hex, val color: Color) {
    override operator fun equals(other: Any?) =
        if (other is Pixel)
            this.hex == other.hex
        else false

    override fun toString(): String {
        return "Pixel(hex=$hex, color=$color)"
    }

    fun getNeighborsFromList(pixels: List<Pixel>): List<Pixel> {
        return pixels.filter { Hex.areNeighbors(this.hex, it.hex) }
    }

    fun hasNeighborInList(pixels: List<Pixel>, direction: Direction): Boolean {
        val neighbor = hex.neighbor(direction)

        return getNeighborsFromList(pixels).any { it.hex == neighbor }
    }

    private fun compareWithColor(pixel: Pixel): Boolean { return compareNoColor(pixel) && this.color == pixel.color }

    fun compareNoColor(pixel: Pixel) = pixel == this

}
