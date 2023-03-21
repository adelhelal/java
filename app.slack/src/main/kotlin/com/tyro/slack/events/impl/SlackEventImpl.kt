package com.tyro.slack.events.impl

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.reactions.ReactionsAddRequest
import com.tyro.slack.ServiceFactory
import com.tyro.slack.data.Karma
import com.tyro.slack.data.KarmaList
import com.tyro.slack.events.SlackEvent

class SlackEventImpl(private var client: MethodsClient) : SlackEvent {
    override fun giveKarma(channelId: String, ts: String, text: String) {
        // parse the message
        val list = ServiceFactory.getKarmaParser().parseMessageForKarma(text)

        // store the adjustments
        val karmaPersistence = ServiceFactory.getKarmaPersistence()
        list.forEach { karmaPersistence.updateKarma(it) }

        // message in thread
        val responseBuilder = ServiceFactory.getResponseBuilder()
        val karmaList = KarmaList(
            list.map { adjustment ->
                Karma(
                    recipient = adjustment.recipient,
                    balance = karmaPersistence.getKarmaFor(adjustment.recipient),
                )
            },
        )
        val responseText = responseBuilder.buildMultipleRecipientBalanceMessage(
            karmaList = karmaList,
            hasChanged = true,
            sorted = false,
        )

        postMessage(channelId, ts, responseText)
    }

    override fun addReaction(channelId: String, ts: String) {
        val reaction = client.reactionsAdd { r: ReactionsAddRequest.ReactionsAddRequestBuilder ->
            r.channel(channelId).timestamp(ts).name("eyes")
        }
        if (!reaction.isOk) {
            throw Exception("reactions.add failed: ${reaction.error}")
        }
    }

    override fun postMessage(channelId: String, ts: String, text: String) {
        val message = client.chatPostMessage { r: ChatPostMessageRequest.ChatPostMessageRequestBuilder ->
            r.channel(channelId)
                .threadTs(ts)
                .text(text)
                .unfurlLinks(true)
        }
        if (!message.isOk) {
            throw Exception("chat.postMessage failed: ${message.error}")
        }
    }
}
