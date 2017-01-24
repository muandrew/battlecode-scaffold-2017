package xmqu.agents

import battlecode.common.RobotController
import xmqu.goals.CompositeGoal
import xmqu.goals.Goal
import xmqu.goals.Status
import xmqu.goals.Telegram

class Archon(controller: RobotController) : Agent(controller) {

    override fun getInitialGoal(): Goal {
        return InitialGoal(this)
    }

    class InitialGoal(owner: Archon) : CompositeGoal(owner) {

        override fun activate() {}

        override fun process(): Status {
            return Status.ACTIVE
        }

        override fun terminate() {}

        override fun handleMessage(telegram: Telegram): Boolean {
            return false
        }

        override fun addSubGoal(goal: Goal) {}
    }
}
