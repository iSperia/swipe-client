package com.game7th.swipe.gameplay.battle.battleground

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.game7th.swipe.gameplay.sprites.SpriteSheet
import com.game7th.swipe.logics.battleground.*
import ktx.actors.alpha
import java.util.*

class BattleActor : Group() {

    init {
        val random = Random()
        addActor(Image(Texture("battle_bg_${random.nextInt(3)}.fw.png")))
    }

    private fun placeStandardCharacter(x: Int, y: Int, side: FieldSide, actor: PersonageActor) {
        when (side) {
            FieldSide.Left -> {
                actor.place(
                        CHARACTER_CELL_WIDTH * x + 12f * y,
                        CHARACTER_CELL_WIDTH * y)
            }
            FieldSide.Right -> {
                actor.place(
                        480f - CHARACTER_CELL_WIDTH * (x + 1) - 12f * y,
                        CHARACTER_CELL_WIDTH / 2 + CHARACTER_CELL_WIDTH * y)
            }
        }
    }

    fun createPersonage(action: BACreatePersonage) {
        addAction(Actions.delay(action.delay, Actions.run {
            val personage = action.character
            val actor = PersonageActor(personage.template.graphics, personage.health, personage.healthMax, personage.side)
            actor.name = personage.id.toString()
            placeStandardCharacter(personage.x, personage.y, personage.side, actor)
            addActor(actor)
            actor.setOrigin(CHARACTER_CELL_WIDTH / 2, CHARACTER_CELL_WIDTH / 2)
            actor.alpha = 0f
            actor.y += CHARACTER_CELL_WIDTH * 3
            actor.addAction(Actions.parallel(
                    Actions.moveTo(actor.x, actor.y - CHARACTER_CELL_WIDTH * 3, 0.25f),
                    Actions.alpha(1f, 0.15f)))
        }))
    }

    private fun animateMelee(action: BAMeleeAttack) {
        addAction(Actions.delay(action.delay, Actions.run {
            val actor = findActor<PersonageActor>(action.id.toString())
            val targetActor = findActor<PersonageActor>(action.targetId.toString())

            val delta = if (targetActor.sx > actor.sx) -1 else 1
            val jumpX = targetActor.sx + delta * CHARACTER_CELL_WIDTH / 2f

            val oldX = actor.sx
            val oldY = actor.sy

            actor.addAction(Actions.sequence(
                    Actions.run {
                        actor.addActor(SpriteSheet().apply {
                            setAtlas("unnamed.atlas")
                            name = "effect"
                        })
                    },
                    Actions.delay(0.5f, Actions.sequence(
                            Actions.run {
                                actor.removeActor(actor.findActor("effect"))
                            },
                            Actions.moveTo(jumpX, targetActor.y, MELEE_MOVE_DURATION),
                            Actions.scaleTo(1.2f, 1.2f, MELEE_ANIMATE_DURATION / 2f),
                            Actions.scaleTo(1f, 1f, MELEE_ANIMATE_DURATION / 2f),
                            Actions.moveTo(oldX, oldY, MELEE_MOVE_DURATION)))
                    ))
        }))
    }

    private fun animateTakeDamage(action: BATakeDamage) {
        val actor = findActor<PersonageActor>(action.id.toString())
        val alphaShift = when (actor.side) {
            FieldSide.Left -> 30f
            FieldSide.Right -> -30f
        }
        actor.addAction(Actions.delay(action.delay, Actions.sequence(
                Actions.rotateTo(alphaShift, TAKE_DAMAGE_DURATION / 2f),
                Actions.rotateTo(0f, TAKE_DAMAGE_DURATION / 2f),
                Actions.run {
                    actor.health = action.newAmount
                }
        )))
    }

    private fun animateDestroyPersonage(action: BADestroyPersonage) {
        val actor = findActor<PersonageActor>(action.id.toString())
        actor.addAction(Actions.delay(action.delay, Actions.sequence(
                Actions.alpha(0f, DESTROY_PERSONAGE_DURATION),
                Actions.run {
                    actor.parent.removeActor(actor)
                }
        )))
    }

    private fun startParallel(action: BAParallel) {
        action.actions.forEach { processAction(it) }
    }

    fun processAction(it: BattleAction) {
        when (it) {
            is BACreatePersonage -> createPersonage(it)
            is BAMeleeAttack -> animateMelee(it)
            is BATakeDamage -> animateTakeDamage(it)
            is BADestroyPersonage -> animateDestroyPersonage(it)
            is BAParallel -> startParallel(it)
        }
    }

}
