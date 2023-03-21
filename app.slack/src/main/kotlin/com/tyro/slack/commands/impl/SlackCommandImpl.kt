package com.tyro.slack.commands.impl

import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.model.event.ReactionAddedEvent
import com.slack.api.model.event.ReactionRemovedEvent
import com.tyro.slack.ServiceFactory
import com.tyro.slack.commands.SlackCommand
import com.tyro.slack.data.KarmaAdjustment
import java.util.regex.Pattern

class SlackCommandImpl : SlackCommand {
    private val userTokenPattern = Pattern.compile("<@(.*?)\\|.*>")

    override fun handleGetKarmaFor(req: SlashCommandRequest, ctx: SlashCommandContext): SlashCommandResponse {
        val matcher = userTokenPattern.matcher(req.payload.text)
        val recipient = if (matcher.matches()) {
            "<@${matcher.group(1)}>"
        } else {
            req.payload.text
        }
        val balance = ServiceFactory.getKarmaPersistence().getKarmaFor(recipient)

        val response = ServiceFactory.getResponseBuilder().buildRecipientBalanceMessage(recipient, balance, false)

        return SlashCommandResponse.builder()
            .text(response)
            .responseType("in_channel")
            .build()
    }

    override fun handleGetAllKarma(req: SlashCommandRequest, ctx: SlashCommandContext): SlashCommandResponse {
        val karmaList = ServiceFactory.getKarmaPersistence().getAllKarma()

        val response = ServiceFactory.getResponseBuilder().buildAllKarmaMessage(karmaList)

        return SlashCommandResponse.builder()
            .text(response)
            .responseType("in_channel")
            .build()
    }

    override fun handleReactionAdded(event: ReactionAddedEvent, ctx: EventContext) {
        println(event.itemUser)
        val user = "<@${event.itemUser}>"
        val adjustment = when (event.reaction) {
            "+1", "thank_you", "thankyou", "awesome", "great_job", "rocker", "tada", "karma" -> 1
            else -> 0
        }
        val channelId = event.item.channel

        handleReactionMessage(adjustment, user, ctx, channelId, event.item.ts)
    }

    override fun handleReactionRemoved(event: ReactionRemovedEvent, ctx: EventContext) {
        val user = "<@${event.itemUser}>"
        val adjustment = when (event.reaction) {
            "+1", "thank_you", "thankyou", "awesome", "great_job", "rocker", "tada", "karma" -> -1
            else -> 0
        }
        val channelId = event.item.channel

        handleReactionMessage(adjustment, user, ctx, channelId, event.item.ts)
    }

    private fun handleReactionMessage(adjustment: Int, user: String, ctx: EventContext, channelId: String, ts: String) {
        if (adjustment != 0) {
            val karmaPersistence = ServiceFactory.getKarmaPersistence()
            karmaPersistence.updateKarma(
                KarmaAdjustment(
                    recipient = user,
                    adjustmentAmount = adjustment,
                ),
            )
            val balance = karmaPersistence.getKarmaFor(user)

            val responseBuilder = ServiceFactory.getResponseBuilder()
            val responseText = responseBuilder.buildRecipientBalanceMessage(
                recipient = user,
                balance = balance,
                hasChanged = true,
            )

            val events = ServiceFactory.getSlackEvent(ctx.client())
            events.postMessage(channelId, ts, responseText)
        }
    }
}
