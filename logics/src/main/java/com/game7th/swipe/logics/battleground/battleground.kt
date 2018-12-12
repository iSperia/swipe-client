package com.game7th.swipe.logics.battleground

enum class FieldSide {
    Left, Right
}

/**
 * Abstract battleground action used to transfer state of battleground
 */
open class BattleAction(val endurance: Float, var delay: Float = 0f, val priority: Int = 1)

/**
 * Event that syas 'create new character'
 */
data class BACreatePersonage (val character: Personage): BattleAction(CREATE_PERSONAGE_DURATION, 0f, 1)

data class BAParallel(val actions: List<BattleAction>) : BattleAction(actions.maxBy { it.endurance }?.endurance ?: 0f, 0f, actions.maxBy { it.priority }?.priority ?: 1)

data class BAMeleeAttack(val id: Int, val targetId: Int) : BattleAction(MELEE_TOTAL_DURATION, 0f, 2) {
    override fun toString() = "MeleeAttack($id->$targetId)"
}

data class BATakeDamage(val id: Int, val amount: Int, val newAmount: Int) : BattleAction(TAKE_DAMAGE_DURATION, 0f, 2) {
    override fun toString() = "TakeDamage($id: $amount->$newAmount)"
}

data class BADestroyPersonage (val id: Int): BattleAction(DESTROY_PERSONAGE_DURATION, 0f, 3) {
    override fun toString(): String = "DestroyPersonage($id)"
}