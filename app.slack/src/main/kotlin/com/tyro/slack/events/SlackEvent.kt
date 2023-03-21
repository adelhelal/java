package com.tyro.slack.events

interface SlackEvent {
    fun giveKarma(channelId: String, ts: String, text: String)
    fun addReaction(channelId: String, ts: String)
    fun postMessage(channelId: String, ts: String, text: String)
}
