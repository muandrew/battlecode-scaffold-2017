package xmqu.reflex

import battlecode.common.Direction
import battlecode.common.RobotController
import xmqu.Dir

interface Movement {

    fun move(): Boolean

    class RandomWalk(val controller: RobotController, val run: Int = 1) : Movement {
        var dir = Direction.NORTH
        var stepsToTake = 0

        override fun move(): Boolean {
            if (stepsToTake <= 0) {
                stepsToTake = run
                dir = Dir.random()
            }
            if (controller.canMove(dir)) {
                controller.move(dir)
                stepsToTake--
                return true
            } else {
                stepsToTake = 0
                return false
            }
        }
    }
}
