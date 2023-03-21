package com.tyro.slack.response.impl

import com.tyro.slack.data.KarmaList
import com.tyro.slack.response.ResponseBuilder

class ResponseBuilderImpl : ResponseBuilder {
    override fun buildRecipientBalanceMessage(
        recipient: String,
        balance: Int,
        hasChanged: Boolean,
    ) =
        when (hasChanged) {
            true -> "$recipient now has $balance karma"
            false -> "$recipient has $balance karma"
        }

    override fun buildMultipleRecipientBalanceMessage(
        karmaList: KarmaList,
        hasChanged: Boolean,
        sorted: Boolean,
    ): String {
        val list = when (sorted) {
            true -> karmaList.karmas.sortedByDescending { it.balance }
            false -> karmaList.karmas
        }
        return list.joinToString(separator = "\n") { karma ->
            buildRecipientBalanceMessage(formatRecipient(karma.recipient), karma.balance, hasChanged)
        }
    }

    override fun buildAllKarmaMessage(karmaList: KarmaList): String {
        val karmaLines = buildMultipleRecipientBalanceMessage(
            karmaList = karmaList,
            hasChanged = false,
            sorted = true,
        )
        return "The current distribution of karma is:\n\n$karmaLines"
    }

    private fun formatRecipient(recipient: String): String {
        if (recipient.startsWith("<") && recipient.endsWith(">")) {
            return recipient
        }
        return "<https://www.tyro.com|${recipient}>"
    }
}
