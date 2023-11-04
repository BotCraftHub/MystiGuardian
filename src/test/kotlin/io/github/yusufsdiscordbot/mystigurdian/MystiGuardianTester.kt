package io.github.yusufsdiscordbot.mystigurdian

import org.javacord.api.DiscordApi
import org.javacord.api.interaction.SlashCommandInteraction
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import java.util.function.UnaryOperator

class MystiGuardianTester {
    private val discordApi: DiscordApi = mock(DiscordApi::class.java)


    fun createSlashEvent() {
        val mockOperator: UnaryOperator<SlashCommandInteraction> =
            UnaryOperator<SlashCommandInteraction> { event: SlashCommandInteraction ->
                spy(event)
                mockSlashEvent(event)
                return@UnaryOperator event
            }


    }

    fun mockSlashEvent(event: SlashCommandInteraction) {
        
    }
}