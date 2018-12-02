package com.game7th.swipe.logics.battleground

class Personage(val template: CharacterTemplate,
                val side: FieldSide,
                var x: Int,
                var y: Int,
                val control: CharacterControlType,
                val id: Int = INDEX++,
                var health: Int,
                var healthMax: Int) {

    companion object {
        var INDEX = 0
    }
}