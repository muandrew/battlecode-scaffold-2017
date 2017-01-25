package xmqu.agents

import battlecode.common.*
import xmqu.*
import xmqu.goals.Goal

abstract class Agent(val controller: RobotController) {

    class Environment {
        var location: MapLocation = MapLocation(0f, 0f)
        var bullets: Array<BulletInfo> = arrayOf()
        var robots: Array<RobotInfo> = arrayOf()
        var trees: Array<TreeInfo> = arrayOf()

        fun update(controller: RobotController) {
            location = controller.location
            bullets = controller.senseNearbyBullets()
            robots = controller.senseNearbyRobots()
            trees = controller.senseNearbyTrees()
        }

        fun nearbyRobots(team: Team, robotType: RobotType, distanceSq: Float): List<RobotInfo> {
            return robots
                    .filter { it.team == team }
                    .filter { it.type == robotType }
                    .filter { it.location.distanceSquaredTo(location) < distanceSq }
        }

        fun nearbyRobots(team: Team, distanceSq: Float): List<RobotInfo> {
            return robots
                    .filter { it.team == team }
                    .filter { it.location.distanceSquaredTo(location) < distanceSq }
        }
    }

    var env = Environment()
    val team = controller.team!!
    val opponent = team.opponent()

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
    open fun moveTowards(dest: MapLocation): Boolean {
        val location = controller.location
        val heading = Vector2D(location, dest).normalize()
        for (bullet in env.bullets) {
            heading.addWith(inverseRSq(bullet.location, location, 30f))
        }
        for (robot in env.robots) {
            val mag = if (robot.team.isPlayer) 10f else {
                when {
                    robot.type == RobotType.LUMBERJACK -> 55f
                    else -> 20f
                }
            }
            heading.addWith(inverseRSq(robot.location, location, mag))
        }
        for (tree in env.trees) {
            heading.addWith(inverseRSq(tree.location, location, 4f))
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
        return moveTo(Dir.random())
    }

    fun areEnemiesNearby(distanceSq: Float): Boolean {
        return env.nearbyRobots(opponent, distanceSq).isNotEmpty()
    }

    abstract fun getInitialGoal(): Goal
}
