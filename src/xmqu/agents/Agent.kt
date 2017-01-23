package xmqu.agents

import battlecode.common.*
import xmqu.Vector2D
import xmqu.debug_move
import xmqu.goals.AtomicGoal
import xmqu.goals.Goal
import xmqu.goals.Status
import xmqu.goals.Telegram
import xmqu.inverseRSq

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

    abstract fun getInitialGoal(): Goal<Agent>
}

open class BasicAgent(controller: RobotController) : Agent(controller) {

    override fun getInitialGoal(): Goal<Agent> {
        return StubGoal(this)
    }
}

class StubGoal<A : Agent>(owner: A) : AtomicGoal<A>(owner) {
    val dest: MapLocation

    init {
        val controller = owner.controller
        val locations = controller.getInitialArchonLocations(controller.team.opponent())
        if (locations.isNotEmpty()) {
            dest = locations[0]
        } else {
            dest = controller.location
        }
    }

    override fun activate() {}

    override fun process(): Status {
        owner.moveTowards(dest)
        return Status.ACTIVE
    }

    override fun terminate() {}

    override fun handleMessage(telegram: Telegram): Boolean {
        return true
    }
}

fun randomDir(): Direction {
    return Direction((Math.random() * Math.PI * 2).toFloat())
}
