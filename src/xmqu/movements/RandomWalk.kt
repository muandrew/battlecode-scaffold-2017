package xmqu.movements

import battlecode.common.Direction
import xmqu.Dir
import xmqu.agents.Agent

class RandomWalk(val run: Int = 1) : Movement {
    var dir = Direction.NORTH
    var stepsToTake = 0

    override fun move(agent: Agent): Boolean {
        val controller = agent.controller
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
