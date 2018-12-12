package com.game7th.swipe.logics.field

data class BlockInfo(
        val type: BlockType,
        var x: Int,
        var y: Int,
        var level: Int,
        val id: Int = COUNT++
) {
    constructor(shiftedBlock: FieldEngine.ShiftedBlock) : this(
            shiftedBlock.type, shiftedBlock.x, shiftedBlock.y, shiftedBlock.level, shiftedBlock.id
    )

    fun match(block: BlockInfo) = level == block.level && type == block.type

    override fun toString() = "$type level=$level x=$x y=$y id=$id"

    companion object {
        var COUNT = 0
    }
}

enum class BlockType(val maxLevel: Int) {
    Skill(4), Coins(0), Exp(0)
}

open class FieldAction

data class FACreateBlock(
        val block: BlockInfo
) : FieldAction()

data class FAMoveBlock(
        val id: Int,
        val targetX: Int,
        val targetY: Int
) : FieldAction()

data class FAMergeBlock(
        val id: Int,
        var targetX: Int,
        val targetY: Int) : FieldAction()

data class FARemoveBlock(
        val id: Int
) : FieldAction()

data class FAUpgradeBlock(
        val block: BlockInfo,
        val level: Int
) : FieldAction()

data class FABlockResource(
        val block: BlockInfo
) : FieldAction()