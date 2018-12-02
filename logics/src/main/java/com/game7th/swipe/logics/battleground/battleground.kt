package com.game7th.swipe.logics.battleground

enum class FieldSide {
    Left, Right
}

/**
 * Abstract battleground action used to transfer state of battleground
 */
open class BattleAction(val endurance: Float, var delay: Float = 0f)

/**
 * Event that syas 'create new character'
 */
data class BACreatePersonage (val character: Personage): BattleAction(CREATE_PERSONAGE_DURATION)

data class BAParallel(val actions: List<BattleAction>) : BattleAction(actions.maxBy { it.endurance }?.endurance ?: 0f)

data class BAMeleeAttack(val id: Int, val targetId: Int) : BattleAction(MELEE_TOTAL_DURATION)

data class BATakeDamage(val id: Int, val amount: Int, val newAmount: Int) : BattleAction(TAKE_DAMAGE_DURATION)