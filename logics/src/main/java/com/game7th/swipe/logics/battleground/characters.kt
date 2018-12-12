package com.game7th.swipe.logics.battleground

import com.google.gson.annotations.SerializedName

enum class AbilityLogic {
    @SerializedName("melee_attack") MeleeAttack,
    @SerializedName("ranged_attack") RangedAttack,
    @SerializedName("heal_one_ally") HealOneAlly
}

data class CharacterAbility(
        @SerializedName("titleId") val titleId: String,
        @SerializedName("logic") val logic: AbilityLogic,
        @SerializedName("arguments") val arguments: Map<String, Int>,
        @SerializedName("level") val level: Int,
        @SerializedName("rate") val rate: Float?
)

data class CharacterTemplate(
        @SerializedName("graphics") val graphics: String,
        @SerializedName("healthPerLevel") val healthPerLevel: Int,
        @SerializedName("abilities") val abilities: List<CharacterAbility>,
        @SerializedName("id") val id: String
)

data class CharacterConfig(
        @SerializedName("template") val template: CharacterTemplate,
        @SerializedName("level") val level: Int,
        @SerializedName("x") val x: Int,
        @SerializedName("y") val y: Int
        /*
        TODO: Equipment set, effects, skins etc.
         */
)

data class CharacterConfigList(
        @SerializedName("characters") val characters: List<CharacterTemplate>
)

data class PartyConfig(
        val characters: List<CharacterConfig>,
        val control: CharacterControlType

        //TODO: for player config, add some scrolls, potions etc stuff
)

enum class CharacterControlType {
    Human, Ai
}

enum class CharacterPose(val endurance: Float) {
    Attack(1f), Ability(1f), Ultimate(3f)
}