package com.tyro.slack.response

import com.tyro.slack.data.KarmaList

interface ResponseBuilder {
    fun buildRecipientBalanceMessage(recipient: String, balance: Int, hasChanged: Boolean = false): String
    fun buildMultipleRecipientBalanceMessage(karmaList: KarmaList, hasChanged: Boolean = false, sorted: Boolean = true): String
    fun buildAllKarmaMessage(karmaList: KarmaList): String
}
