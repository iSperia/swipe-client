package com.game7th.swipe.logics.battleground

import java.util.*

class BattleEngine(configA: PartyConfig, configB: PartyConfig) {

    private val random = Random()

    private val events = mutableListOf<BattleAction>()
    private val characters = mutableListOf<Personage>()

    init {
        val eventsToAdd = mutableListOf<BattleAction>()

        configA.characters.forEach {
            characters.add(Personage(
                    template = it.template,
                    side = FieldSide.Left,
                    x = it.x,
                    y = it.y,
                    control = configA.control,
                    health = it.level * it.template.healthPerLevel / 2,
                    healthMax = it.level * it.template.healthPerLevel
            ).apply { eventsToAdd.add(BACreatePersonage(this)) })
        }

        configB.characters.forEach {
            characters.add(Personage(
                    template = it.template,
                    side = FieldSide.Right,
                    x = it.x,
                    y = it.y,
                    control = configB.control,
                    health = it.level * it.template.healthPerLevel / 2,
                    healthMax = it.level * it.template.healthPerLevel
            ).apply { eventsToAdd.add(BACreatePersonage(this)) })
        }

        this@BattleEngine.events.add(BAParallel(eventsToAdd))
    }

    fun events(): List<BattleAction> {
        var shift = 0f
        events.forEach {
            it.delay = shift
            shift += it.endurance
        }
        return events
    }

    fun consumeEvents() = events.clear()

    fun triggerSkill(level: Int) {
        val charactersAvailable = characters.filter { it.control == CharacterControlType.Human && it.health > 0 }
        val personage = charactersAvailable[random.nextInt(charactersAvailable.size)]
        val ability = personage.template.abilities.find { it.level == level }
        ability?.let { ability ->
            when (ability.logic) {
                AbilityLogic.MeleeAttack -> processMeleeAttack(personage, ability)
                else -> Unit
            }
        }
    }

    private fun processMeleeAttack(personage: Personage, ability: CharacterAbility) {
        val foes = characters.filter { it.side != personage.side }
        val lines = foes.groupBy { it.x }
        val target = lines.maxBy { it.key }?.value?.maxBy { it.health }
        target?.let { attackTarget ->
            val baseAttackValue = ability.arguments["base_value"] ?: 0 //0 is fallback value

            val amount = baseAttackValue
            target.health -= amount

            events.add(BAMeleeAttack(personage.id, target.id))
            events.add(BATakeDamage(target.id, amount, target.health))
        }
    }
}