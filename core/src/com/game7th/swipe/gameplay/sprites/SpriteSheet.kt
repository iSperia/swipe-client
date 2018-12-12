package com.game7th.swipe.gameplay.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.scenes.scene2d.Actor

class SpriteSheet : Actor() {

    lateinit var atlas: TextureAtlas
    lateinit var animation: Animation<TextureRegion>
    var elapsedTime = 0f

    fun setAtlas(atlasId: String) {
        atlas = TextureAtlas(atlasId)
        animation = Animation(1/8f, atlas.regions)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        elapsedTime += Gdx.graphics.deltaTime
        batch?.draw(animation.getKeyFrame(elapsedTime, true), x, y)
    }
}