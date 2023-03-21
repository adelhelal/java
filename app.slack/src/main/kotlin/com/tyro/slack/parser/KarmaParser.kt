package com.tyro.slack.parser

import com.tyro.slack.data.KarmaAdjustment

interface KarmaParser {
    fun parseMessageForKarma(message: String): List<KarmaAdjustment>
}
