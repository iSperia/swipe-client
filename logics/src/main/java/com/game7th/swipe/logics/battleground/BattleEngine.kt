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
                    health = it.level * it.template.healthPerLevel,
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
                    health = it.level * it.template.healthPerLevel,
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
        return events.sortedBy { it.priority }
    }

    fun consumeEvents() = events.clear()

    fun triggerSkill(level: Int) {
        val charactersAvailable = characters.filter { it.control == CharacterControlType.Human && it.health > 0 }
        if (charactersAvailable.isEmpty()) return
        val personage = charactersAvailable[random.nextInt(charactersAvailable.size)]
        val ability = personage.template.abilities.find { it.level == level }
        System.err.println("trigger skill $level ${personage.id} ${ability?.titleId}")
        activateAbility(ability, personage)
    }

    private fun activateAbility(ability: CharacterAbility?, personage: Personage) {
        ability?.let { ability ->
            when (ability.logic) {
                AbilityLogic.MeleeAttack -> processMeleeAttack(personage, ability)
                else -> Unit
            }
        }
    }

    fun processNpcPersonages() {
        characters.filter { it.control == CharacterControlType.Ai && it.health > 0 }.forEach { npc ->
            var triggeredAbility: CharacterAbility? = null
            npc.template.abilities.sortedByDescending { it.level }.forEach { ability ->
                triggeredAbility ?: let {
                    val randomNumer = random.nextFloat()
                    if (randomNumer < ability.rate ?: 0f) {
                        triggeredAbility = ability
                    }
                }
            }

            activateAbility(triggeredAbility, npc)
        }
    }

    private fun processMeleeAttack(personage: Personage, ability: CharacterAbility) {
        val foesAlive = characters.filter { it.side != personage.side && it.health > 0 }
        val lines = foesAlive.groupBy { it.x }
        val target = lines.maxBy { it.key }?.value?.maxBy { it.health }
        target?.let { attackTarget ->
            val baseAttackValue = ability.arguments["base_value"] ?: 0 //0 is fallback value

            val amount = baseAttackValue
            target.health -= amount

            events.add(BAMeleeAttack(personage.id, target.id))
            events.add(BATakeDamage(target.id, amount, target.health))

            if (target.health <= 0) {
                events.add(BADestroyPersonage(target.id))
            }
        }
    }
}