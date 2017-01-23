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

abstract class Goal<A : Agent>(val owner: A) {

    var status: Status = Status.INACTIVE

    abstract fun activate()

    abstract fun process(): Status

    abstract fun terminate()

    abstract fun handleMessage(telegram: Telegram): Boolean

    abstract fun addSubGoal(goal: Goal<A>)

    fun activateIfInactive() {
        if (status != Status.ACTIVE) {
            status = Status.ACTIVE
            activate()
        }
    }
}

abstract class CompositeGoal<A : Agent>(owner: A) : Goal<A>(owner) {

    val subGoals: Stack<Goal<A>> = Stack()

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

    override fun addSubGoal(goal: Goal<A>) {
        subGoals.push(goal)
    }
}

abstract class AtomicGoal<A : Agent>(owner: A) : Goal<A>(owner) {

    override fun addSubGoal(goal: Goal<A>) {
        debug_crash()
    }
}
