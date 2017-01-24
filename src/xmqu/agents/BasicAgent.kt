package xmqu.agents

import battlecode.common.MapLocation
import battlecode.common.RobotController
import xmqu.goals.Goal
import xmqu.goals.InitialGoalStub

open class BasicAgent(controller: RobotController) : Agent(controller) {

    override fun getInitialGoal(): Goal {
        return InitialGoal(this)
    }

    class InitialGoal(owner: Agent) : InitialGoalStub(owner) {
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

        override fun onProcess() {
            agent.moveTowards(dest)
        }
    }
}
