package com.game7th.swipe.gameplay.battle.grid

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.game7th.swipe.logics.field.BlockInfo
import com.game7th.swipe.logics.field.BlockType
import ktx.actors.alpha

class BlockFieldActor : Group() {

    init {
        val texture = Texture("grid_bg.png")
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                val image = Image(texture)
                addActor(image)
                image.apply {
                    width = CELL_SIZE
                    height = CELL_SIZE
                    setPosition(j * CELL_SIZE, i * CELL_SIZE)
                    setOrigin(Align.center)
                }
            }
        }
    }

    fun addBlock(block: BlockInfo) = addBlock(block, 0f)

    fun addBlock(block: BlockInfo, delay: Float) {
        val texture = Texture(getTexture(block))
        Image(texture).apply {
            addActor(this)
            width = CELL_SIZE
            height = CELL_SIZE
            setPosition(block.x * CELL_SIZE, block.y * CELL_SIZE)
            setOrigin(Align.center)
            name = block.id.toString()
            alpha = 0f
            addAction(Actions.sequence(Actions.delay(delay), Actions.alpha(1f, 0.25f)))
        }
    }

    private fun getTexture(block: BlockInfo) = when (block.type) {
        BlockType.Skill -> when (block.level) {
            0 -> "gem_level_1.fw.png"
            1 -> "gem_level_2.fw.png"
            2 -> "gem_level_3.fw.png"
            3 -> "gem_level_4.fw.png"
            else -> "gem_level_5.fw.png"
        }
        BlockType.Coins -> "coins.fw.png"
        BlockType.Exp -> "star.fw.png"
    }

    fun moveBlock(id: Int, targetX: Int, targetY: Int) {
        findActor<Image>(id.toString())?.let { image ->
            image.addAction(Actions.moveTo(targetX * CELL_SIZE, targetY * CELL_SIZE, 0.25f))
        }
    }

    fun mergeBlock(id: Int, targetX: Int, targetY: Int) {
        findActor<Image>(id.toString())?.let { image ->
            image.addAction(Actions.sequence(Actions.moveTo(targetX * CELL_SIZE, targetY * CELL_SIZE, 0.25f),
                    RunnableAction().apply { setRunnable { this@BlockFieldActor.removeActor(image) } }))
        }
    }

    fun upgradeBlock(block: BlockInfo, level: Int) {
        findActor<Image>(block.id.toString())?.let { image ->
            image.addAction(Actions.sequence(
                    Actions.delay(0.5f),
                    RunnableAction().apply {
                        findActor<Image>(block.id.toString())?.let { image ->
                            removeActor(image)
                        }
                        addBlock(block.copy(level = level))
                    })
            )
        }
    }

    fun removeBlock(id: Int) {
        findActor<Image>(id.toString())?.let { image ->
            image.addAction(Actions.sequence(
                    Actions.delay(0.5f),
                    Actions.parallel(
                            Actions.scaleTo(2f, 2f, 0.25f),
                            Actions.alpha(0f, 0.25f)
                    ),
                    RunnableAction().apply { setRunnable { this@BlockFieldActor.removeActor(image) } }
            ))
        }
    }

    companion object {
        const val CELL_SIZE = 96f
    }
}