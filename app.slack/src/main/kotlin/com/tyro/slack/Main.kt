package com.tyro.slack

import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.methods.MethodsClient
import com.slack.api.model.event.*
import java.util.regex.Pattern

fun main() {
    val botToken = System.getenv("SLACK_BOT_TOKEN")
    val appToken = System.getenv("SLACK_APP_TOKEN")

    val karmaPersistence = ServiceFactory.getKarmaPersistence()
    val app = App(AppConfig.builder().singleTeamBotToken(botToken).build())

    app.message(Pattern.compile(".*\\S+?\\s?[+-]{2,}.*", Pattern.CASE_INSENSITIVE)) { payload, ctx ->
        if (!karmaPersistence.hasKarmaEvent(payload.eventId)) {
            giveKarma(payload.event, ctx.client())
            karmaPersistence.addKarmaEvent(payload.eventId)
        }
        ctx.ack()
    }

    app.event(AppMentionEvent::class.java) { payload, ctx ->
        ctx.ack()
    }

    app.event(ReactionAddedEvent::class.java) { payload, ctx ->
        if (payload != null) {
            ServiceFactory.getSlackCommand().handleReactionAdded(payload.event, ctx)
        }
        ctx.ack()
    }

    app.event(ReactionRemovedEvent::class.java) { payload, ctx ->
        if (payload != null) {
            ServiceFactory.getSlackCommand().handleReactionRemoved(payload.event, ctx)
        }
        ctx.ack()
    }

    app.event(MessageBotEvent::class.java) { payload, ctx ->
        ctx.ack()
    }

    app.event(MessageEvent::class.java) { payload, ctx ->
        ctx.ack()
    }

    app.event(MessageChangedEvent::class.java) { payload, ctx ->
        ctx.ack()
    }

    app.event(MessageDeletedEvent::class.java) { payload, ctx ->
        ctx.ack()
    }

    app.command("/karma") { req, ctx ->
        val res = runCommand(req, ctx)
        ctx.ack(res)
    }

    val socketModeApp = SocketModeApp(appToken, app)
    socketModeApp.start()
}

fun giveKarma(event: MessageEvent, client: MethodsClient) {
    val events = ServiceFactory.getSlackEvent(client)
    events.giveKarma(event.channel, event.ts, event.text)
}

fun runCommand(req: SlashCommandRequest, ctx: SlashCommandContext): SlashCommandResponse {
    val slackCommand = ServiceFactory.getSlackCommand()

    return if (req.payload.text.equals("list") || req.payload.text.isEmpty()) {
        slackCommand.handleGetAllKarma(req, ctx)
    } else {
        slackCommand.handleGetKarmaFor(req, ctx)
    }
}
