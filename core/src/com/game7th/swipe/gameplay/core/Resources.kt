package com.game7th.swipe.gameplay.core

import com.badlogic.gdx.Gdx
import com.game7th.swipe.logics.battleground.CharacterConfigList
import com.game7th.swipe.logics.battleground.CharacterTemplate
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.InputStreamReader

class Resources {

    private val gson = Gson()

    private var characterTemplates: List<CharacterTemplate>

    init {
        val file = Gdx.files.internal("characters.json")

        val characterList: CharacterConfigList = gson.fromJson(JsonReader(InputStreamReader(file.read())), CharacterConfigList::class.java)
        characterTemplates = characterList.characters
    }

    fun getCharacterTemplate(id: String) = characterTemplates.find { it.id == id }
}