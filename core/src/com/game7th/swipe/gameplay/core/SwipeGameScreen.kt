package com.game7th.swipe.gameplay.core

import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

abstract class SwipeGameScreen : Group(), GestureDetector.GestureListener {

    override fun fling(velocityX: Float, velocityY: Float, button: Int) = false

    override fun zoom(initialDistance: Float, distance: Float): Boolean = false

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean = false

    override fun pinchStop() {}

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean = false

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean = false

    override fun longPress(x: Float, y: Float): Boolean = false

    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean = false

    override fun pinch(initialPointer1: Vector2?, initialPointer2: Vector2?, pointer1: Vector2?, pointer2: Vector2?): Boolean = false
}