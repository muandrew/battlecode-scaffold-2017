package xmqu.goals

import scala.collection.mutable.Stack
import xmqu.agents.Agent
import xmqu.debug_crash

enum class Status {
    ACTIVE,
    INACTIVE,
    COMPLETE,
    FAILED
}

abstract class Goal(val owner: Agent) {

    var status: Status = Status.INACTIVE

    abstract fun activate()

    abstract fun process(): Status

    abstract fun terminate()

    abstract fun handleMessage(telegram: Telegram): Boolean

    abstract fun addSubGoal(goal: Goal)

    fun activateIfInactive() {
        if (status != Status.ACTIVE) {
            status = Status.ACTIVE
            activate()
        }
    }
}

abstract class CompositeGoal(owner: Agent) : Goal(owner) {

    val subGoals: Stack<Goal> = Stack()

    fun processSubGoals(): Status {
        while (!subGoals.isEmpty
                && (subGoals.top().status == Status.COMPLETE
                || subGoals.top().status == Status.FAILED)) {
            val goal = subGoals.pop()
            goal.terminate()
        }
        if (!subGoals.isEmpty) {
            val status = subGoals.top().process()
            if (status == Status.COMPLETE && subGoals.length() > 1) {
                return Status.ACTIVE
            } else {
                return status
            }
        } else {
            return Status.COMPLETE
        }
    }

    fun clearSubGoals() {
        while (!subGoals.isEmpty) {
            subGoals.pop().terminate()
        }
    }

    override fun handleMessage(telegram: Telegram): Boolean {
        if (subGoals.isEmpty) {
            return false
        } else {
            return subGoals.top().handleMessage(telegram)
        }
    }

    override fun addSubGoal(goal: Goal) {
        subGoals.push(goal)
    }
}

abstract class AtomicGoal(owner: Agent) : Goal(owner) {

    override fun addSubGoal(goal: Goal) {
        debug_crash("AtomicGoal can not add sub-Goals.")
    }
}
