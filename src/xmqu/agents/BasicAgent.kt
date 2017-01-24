package xmqu.agents

import battlecode.common.MapLocation
import battlecode.common.RobotController
import xmqu.goals.AtomicGoal
import xmqu.goals.Goal
import xmqu.goals.Status
import xmqu.goals.Telegram

open class BasicAgent(controller: RobotController) : Agent(controller) {

    override fun getInitialGoal(): Goal {
        return StubGoal(this)
    }
}

class StubGoal(owner: Agent) : AtomicGoal(owner) {
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
