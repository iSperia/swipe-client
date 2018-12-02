package com.game7th.swipe.gameplay.battle

import com.badlogic.gdx.Gdx
import com.game7th.swipe.gameplay.core.SwipeGameScreen
import com.game7th.swipe.gameplay.battle.battleground.BattleActor
import com.game7th.swipe.gameplay.battle.grid.BlockFieldActor
import com.game7th.swipe.gameplay.core.Resources
import com.game7th.swipe.logics.battleground.*
import com.game7th.swipe.logics.field.*
import kotlin.math.abs

/**
 * Game screen
 */
class BattleScreen(val resources: Resources) : SwipeGameScreen() {

    private var aField: BlockFieldActor
    private var aBattle: BattleActor

    var fieldEngine: FieldEngine
    var battleEngine: BattleEngine

    init {
        fieldEngine = FieldEngine()
        for (i in 0..5) fieldEngine.generateBlock()

        battleEngine = BattleEngine(createHumanParty(), createAiParty())

        aField = BlockFieldActor()
        addActor(aField)
        aField.y = 80f

        aBattle = BattleActor()
        addActor(aBattle)
        aBattle.y = 560f
    }

    override fun act(delta: Float) {
        super.act(delta)

        battleEngine.events().forEach {
            aBattle.processAction(it)

        }
        battleEngine.consumeEvents()

        fieldEngine.events().forEach {
            when (it) {
                is FACreateBlock -> aField.addBlock(it.block, 0.25f)
                is FAMoveBlock -> aField.moveBlock(it.id, it.targetX, it.targetY)
                is FAMergeBlock -> aField.mergeBlock(it.id, it.targetX, it.targetY)
                is FAUpgradeBlock -> aField.upgradeBlock(it.block, it.level)
                is FARemoveBlock -> aField.removeBlock(it.id)
                is FABlockResource -> processBlockResource(it)
            }
        }
        fieldEngine.consumeEvents()
    }

    private fun processBlockResource(blockResource: FABlockResource) {
        if (blockResource.block.type == BlockType.Skill) {
            battleEngine.triggerSkill(blockResource.block.level + 1)
        }
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        Gdx.app.log("Swipe", "fling $velocityX $velocityY $button")
        var createNew = true
        if (velocityX < -1000f && abs(velocityY) < 300f) {
            fieldEngine.shiftLeft()
        } else if (abs(velocityX) < 300f && velocityY < -1000f) {
            fieldEngine.shiftUp()
        } else if (abs(velocityX) < 300f && velocityY > 1000f) {
            fieldEngine.shiftDown()
        } else if (velocityX > 1000f && abs(velocityY) < 300f) {
            fieldEngine.shiftRight()
        } else {
            createNew = false
        }
        if (createNew) {
            fieldEngine.generateBlock()
        }
        return false
    }

    private fun createHumanParty() = PartyConfig(
            listOf(
                    CharacterConfig(resources.getCharacterTemplate("p_knight")!!, 1, 0, 1),
                    CharacterConfig(resources.getCharacterTemplate("p_archer")!!, 1, 0, 0),
                    CharacterConfig(resources.getCharacterTemplate("p_priest")!!, 1, 0, 2)
            ),
            CharacterControlType.Human
    )

    private fun createAiParty() = PartyConfig(
            listOf(
                    CharacterConfig(resources.getCharacterTemplate("npc_goblin")!!, 1, 1, 1),
                    CharacterConfig(resources.getCharacterTemplate("npc_goblin")!!, 1, 1, 2),
                    CharacterConfig(resources.getCharacterTemplate("npc_goblin")!!, 1, 2, 1),
                    CharacterConfig(resources.getCharacterTemplate("npc_goblin")!!, 1, 2, 0)
            ),
            CharacterControlType.Ai
    )
}
