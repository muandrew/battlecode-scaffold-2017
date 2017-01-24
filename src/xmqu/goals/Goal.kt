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

abstract class Goal(val agent: Agent) {

    var status: Status = Status.INACTIVE

    open fun activate() {
        debug_onActivate()
        onActivate()
    }

    abstract fun onActivate()

    fun process(): Status {
        activateIfInactive()
        onProcess()
        return status
    }

    abstract fun onProcess()

    fun terminate() {
        onTerminate()
    }

    abstract fun onTerminate()

    abstract fun addSubGoal(goal: Goal)

    open fun handleMessage(@Suppress("UNUSED_PARAMETER") telegram: Telegram): Boolean {
        return false
    }

    fun activateIfInactive() {
        if (status != Status.ACTIVE) {
            status = Status.ACTIVE
            activate()
        }
    }

    private fun debug_onActivate() {
        System.out.println(this.toString())
    }
}

abstract class InitialGoalStub(agent: Agent) : Goal(agent) {

    override fun onActivate() {}

    override fun onTerminate() {
        debug_crash("InitialGoal does not terminate.")
    }

    override fun addSubGoal(goal: Goal) {
        debug_crash("InitialGoal does not addSubGoal.")
    }
}

abstract class CompositeGoal(agent: Agent) : Goal(agent) {

    val subGoals: Stack<Goal> = Stack()

    override fun activate() {
        clearSubGoals()
        super.activate()
    }

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
