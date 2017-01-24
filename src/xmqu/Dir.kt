package xmqu

import battlecode.common.Direction

class Dir {

    companion object {
        val rad30 = Math.PI / 3
        fun random(): Direction {
            return Direction((Utils.random.nextDouble() * Math.PI * 2).toFloat())
        }
    }

    enum class Hex(val dir: Direction) {
        D00(Direction(0f)),
        D02(Direction(rad30.toFloat())),
        D04(Direction((rad30 * 2).toFloat())),
        D06(Direction((rad30 * 3).toFloat())),
        D08(Direction((rad30 * 4).toFloat())),
        D10(Direction((rad30 * 5).toFloat()));

        companion object {
            val hex = Hex.values()
            val hexLen = hex.size
            fun random(): Hex {
                return hex[Utils.random.nextInt(hexLen)]
            }
        }
    }
}
