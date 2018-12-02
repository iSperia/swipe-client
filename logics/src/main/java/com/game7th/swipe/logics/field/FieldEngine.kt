package com.game7th.swipe.logics.field

import java.util.*

class FieldEngine {

    val cells = mutableListOf<BlockInfo?>()

    private val events = mutableListOf<FieldAction>()

    val fieldSize = 5
    val totalCells = fieldSize * fieldSize

    val random = Random()

    init {
        for (i in 0 until totalCells) cells.add(null)
    }

    fun generateBlock() {
        val blocksSize = cells.count { it != null }
        if (blocksSize == totalCells) throw IllegalStateException("Can't generate block, field is full")

        val index = random.nextInt(totalCells - blocksSize)
        val float = random.nextFloat()
        val generatedBlockType = if (float < 0.8f) BlockType.Skill else if (float < 0.9f) BlockType.Coins else BlockType.Exp
        val generatedCell = cells.withIndex().filter { it.value == null }[index].index

        val generatedX = generatedCell % fieldSize
        val generatedY = generatedCell / fieldSize

        BlockInfo(generatedBlockType, generatedX, generatedY, 0).apply {
            cells[generatedCell] = this
            events.add(FACreateBlock(this))
        }
    }

    fun shiftLeft() = shift(-1, 0)
    fun shiftRight() = shift(1, 0)
    fun shiftUp() = shift(0, 1)
    fun shiftDown() = shift(0, -1)

    data class ShiftedBlock(
            var isMerged: Boolean,
            var x: Int,
            var y: Int,
            var level: Int,
            var type: BlockType,
            val id: Int
    ) {
        fun match(block: ShiftedBlock) = level == block.level && type == block.type
    }

    private fun shift(dx: Int, dy: Int) {
        val lockedIndexes = mutableListOf<Int>()

        //copy stuff to shifted blocks
        val shiftedCells = cells.map {
            it?.let { ShiftedBlock(false, it.x, it.y, it.level, it.type, it.id) }
        }.toMutableList()

        val mergeActions = mutableListOf<FieldAction>()

        val fx = if (dx > 0) fieldSize - 1 else 0
        val fy = if (dy > 0) fieldSize - 1 else 0
        val tx = if (dx > 0) 0 else fieldSize - 1
        val ty = if (dy > 0) 0 else fieldSize - 1

        val rx = if (dx > 0) fx downTo tx else fx..tx
        var ry = if (dy > 0) fy downTo ty else fy..ty

        var isAnythingHappened = true

        while (isAnythingHappened) {
            isAnythingHappened = false

            for (i in ry) {
                for (j in rx) {
                    val idx = i * fieldSize + j
                    shiftedCells[idx]?.let { shiftedBlock ->
                        val tox = shiftedBlock.x + dx
                        val toy = shiftedBlock.y + dy
                        val toIdx = toy * fieldSize + tox
                        if (tox >= 0 && toy >= 0 && tox < fieldSize && toy < fieldSize && !shiftedBlock.isMerged && !lockedIndexes.contains(toIdx)) {
                            val toCell = shiftedCells[toIdx]
                            if (toCell != null) {
                                if (shiftedBlock.match(toCell) && !toCell.isMerged) {
                                    shiftedCells[idx] = null

                                    mergeActions.add(FAMergeBlock(shiftedBlock.id, tox, toy))

                                    cells[toCell.x + toCell.y * fieldSize]?.let { block ->
                                        mergeActions.add(FABlockResource(block.copy(level = block.level)))
                                    }

                                    if (toCell.level < toCell.type.maxLevel) {
                                        toCell.isMerged = true
                                        toCell.level++
                                    } else {
                                        mergeActions.add(FAMoveBlock(toCell.id, toCell.x, toCell.y))
                                        mergeActions.add(FARemoveBlock(toCell.id))
                                        shiftedCells[toIdx] = null
                                        lockedIndexes.add(toIdx)
                                    }

                                    isAnythingHappened = true
                                }
                            } else {
                                shiftedCells[toIdx] = shiftedBlock
                                shiftedBlock.x = tox
                                shiftedBlock.y = toy
                                shiftedCells[idx] = null

                                isAnythingHappened = true
                            }
                        }
                    }
                }
            }
        }

        val moveActions = mutableListOf<FieldAction>()

        shiftedCells.withIndex().forEach { (index, block) ->
            cells[index] = block?.let {
                BlockInfo(it.type, it.x, it.y, it.level, it.id)
            }
        }

        shiftedCells.filter { it != null }.forEach { it ->
            it?.let {
                moveActions.add(FAMoveBlock(it.id, it.x, it.y))
                if (it.isMerged) {
                    moveActions.add(FAUpgradeBlock(cells[it.y * fieldSize + it.x]!!, it.level))
                }
            }
        }

        events.addAll(moveActions)
        events.addAll(mergeActions)
    }

    fun events() = events

    fun consumeEvents() = events.clear()
}