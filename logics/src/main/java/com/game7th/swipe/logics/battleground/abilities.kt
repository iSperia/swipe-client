package com.game7th.swipe.logics.battleground

interface Ability {
    fun apply(owner: Personage, engine: BattleEngine)
}

class MeleeAttackAbility : Ability {
    override fun apply(owner: Personage, engine: BattleEngine) {

    }
}

class AbilityFactory {

    fun produceAbility(type: AbilityLogic): Ability = when (type) {
        else -> MeleeAttackAbility()
    }
}