package xmqu.agents

import battlecode.common.*
import xmqu.*
import xmqu.goals.Goal

abstract class Agent(val controller: RobotController) {

    class Environment {
        var bullets: Array<BulletInfo> = arrayOf()
        var robots: Array<RobotInfo> = arrayOf()
        var trees: Array<TreeInfo> = arrayOf()

        fun update(controller: RobotController) {
            bullets = controller.senseNearbyBullets()
            robots = controller.senseNearbyRobots()
            trees = controller.senseNearbyTrees()
        }
    }

    var env = Environment();

    fun run() {
        val goal = getInitialGoal()
        while (true) {
            env.update(controller)
            goal.process()
            try {
                Clock.`yield`()
            } catch (e: Exception) {
                e.printStackTrace()
                debug_crash(e.message ?: "no message.")
            }
        }
    }

    /**
     * Attempts to move towards a direction, has fallback plans.
     */
    fun moveTowards(dest: MapLocation): Boolean {
        val location = controller.location
        val heading = Vector2D(location, dest).normalize()
        for (bullet in env.bullets) {
            heading.addWith(inverseRSq(bullet.location, location, 30F))
        }
        for (robot in env.robots) {
            val mag = if (robot.team.isPlayer) -1F else 20F
            heading.addWith(inverseRSq(robot.location, location, mag))
        }
        for (tree in env.trees) {
            heading.addWith(inverseRSq(tree.location, location, 5F))
        }
        val dir = heading.toDirection()
        return moveTo(dir) || moveRandomly() || moveRandomly()
    }

    /**
     * A basic move operation.
     */
    fun moveTo(dir: Direction): Boolean {
        if (controller.canMove(dir)) {
            debug_move(controller, dir)
            controller.move(dir)
            return true
        } else {
            return false
        }
    }

    /**
     * Choose one random direction to try.
     */
    fun moveRandomly(): Boolean {
        return moveTo(randomDir())
    }

    abstract fun getInitialGoal(): Goal
}
