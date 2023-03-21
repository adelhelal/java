package com.tyro.slack.commands

import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.model.event.ReactionAddedEvent
import com.slack.api.model.event.ReactionRemovedEvent

interface SlackCommand {
    fun handleGetKarmaFor(req: SlashCommandRequest, ctx: SlashCommandContext): SlashCommandResponse
    fun handleGetAllKarma(req: SlashCommandRequest, ctx: SlashCommandContext): SlashCommandResponse
    fun handleReactionAdded(event: ReactionAddedEvent, ctx: EventContext)
    fun handleReactionRemoved(event: ReactionRemovedEvent, ctx: EventContext)
}
