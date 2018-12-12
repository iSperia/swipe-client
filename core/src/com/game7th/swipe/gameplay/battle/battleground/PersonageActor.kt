package com.game7th.swipe.gameplay.battle.battleground

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.game7th.swipe.logics.battleground.FieldSide

class PersonageActor(var skin: String, var health: Int, var maxHealth: Int, var side: FieldSide) : Group() {

    val shapeRenderer = ShapeRenderer()

    var sx = 0f
    var sy = 0f

    init {
        val img = Image(Texture("$skin.png"))
        img.scaleX = when (side) {
            FieldSide.Right -> -1f
            else -> 1f
        }
        img.x = when (side) {
            FieldSide.Right -> CHARACTER_CELL_WIDTH
            else -> 0f
        }
        addActor(img)
    }

    fun place(x: Float, y: Float) {
        this.x = x
        this.y = y
        sx = x
        sy = y
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        batch?.let { batch ->
            batch.end()

            val location = localToStageCoordinates(Vector2(0f, 0f))
            val myx = location.x
            val myy = location.y

            shapeRenderer.projectionMatrix = batch.projectionMatrix
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.setColor(1f, 0f, 0f, 1f)
            shapeRenderer.rect(myx + 6f, myy + 2f, CHARACTER_CELL_WIDTH - 12, 2f)
            shapeRenderer.setColor(0f, 1f, 0f, 1f)
            val percentage = health.toFloat() / maxHealth.toFloat()
            shapeRenderer.rect(myx + 6f, myy + 2f, (CHARACTER_CELL_WIDTH - 12) * percentage, 2f)
            shapeRenderer.end()

            batch.begin()
        }
    }
}
