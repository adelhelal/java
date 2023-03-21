package com.tyro.slack

import com.slack.api.methods.MethodsClient
import com.tyro.slack.commands.SlackCommand
import com.tyro.slack.commands.impl.SlackCommandImpl
import com.tyro.slack.events.impl.SlackEventImpl
import com.tyro.slack.parser.KarmaParser
import com.tyro.slack.parser.impl.KarmaParserImpl
import com.tyro.slack.persistence.KarmaPersistence
import com.tyro.slack.persistence.impl.KarmaPersistenceMapImpl
import com.tyro.slack.response.ResponseBuilder
import com.tyro.slack.response.impl.ResponseBuilderImpl

class ServiceFactory {
    companion object {
        private val karmaParser: KarmaParser = KarmaParserImpl()
        private val karmaPersistence: KarmaPersistence = KarmaPersistenceMapImpl()
        private val slackCommand: SlackCommand = SlackCommandImpl()
        private val responseBuilder: ResponseBuilder = ResponseBuilderImpl()

        fun getKarmaParser() = karmaParser
        fun getKarmaPersistence() = karmaPersistence
        fun getSlackCommand() = slackCommand
        fun getSlackEvent(client: MethodsClient) = SlackEventImpl(client)
        fun getResponseBuilder() = responseBuilder
    }
}
