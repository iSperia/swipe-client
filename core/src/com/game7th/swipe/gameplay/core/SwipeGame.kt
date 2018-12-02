package com.game7th.swipe.gameplay.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game7th.swipe.gameplay.battle.BattleScreen

class SwipeGame : ApplicationAdapter() {
    lateinit var batch: SpriteBatch

    lateinit var stage: Stage
    lateinit var gameSwipeGameScreen: SwipeGameScreen
    lateinit var resources: Resources

    override fun create() {
        resources = Resources()

        batch = SpriteBatch()

        stage = Stage(FitViewport(480f, 850f))

        gameSwipeGameScreen = BattleScreen(resources)
        stage.addActor(gameSwipeGameScreen)

        Gdx.input.inputProcessor = GestureDetector(object : GestureDetector.GestureListener {

            override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean = gameSwipeGameScreen.fling(velocityX, velocityY, button)

            override fun zoom(initialDistance: Float, distance: Float): Boolean = false

            override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean = false

            override fun pinchStop() {}

            override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean = false

            override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean = false

            override fun longPress(x: Float, y: Float): Boolean = false

            override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean = false

            override fun pinch(initialPointer1: Vector2?, initialPointer2: Vector2?, pointer1: Vector2?, pointer2: Vector2?): Boolean = false
        })
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)

        batch.begin()
        stage.draw()
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }
}

