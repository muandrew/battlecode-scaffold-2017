package xmqu.movements

import xmqu.agents.Agent

interface Movement {

    fun move(agent: Agent): Boolean
}
