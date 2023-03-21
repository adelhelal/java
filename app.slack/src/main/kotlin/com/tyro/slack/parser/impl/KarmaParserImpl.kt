package com.tyro.slack.parser.impl

import com.tyro.slack.data.KarmaAdjustment
import com.tyro.slack.parser.KarmaParser
import java.util.regex.Pattern

class KarmaParserImpl : KarmaParser {

    private val matchingPattern = Pattern.compile("^.*?([\\S]*?)\\s?([+-]{2,}).*?$")

    override fun parseMessageForKarma(message: String): List<KarmaAdjustment> {
        val adjustmentList = mutableListOf<KarmaAdjustment>()
        var remainingMessage = message
        var matcher = matchingPattern.matcher(message)
        while (matcher.matches()) {
            val recipient = matcher.group(1)
            val adjustment = matcher.group(2)

            // work out how many consecutive + or - there are
            var count = 1
            for (i in 1 until adjustment.length) {
                if (adjustment[i] == adjustment[0]) {
                    count++
                } else {
                    break
                }
            }
            val adjustmentAmount = when (adjustment[0]) {
                '+' -> count
                '-' -> -count
                else -> 0
            }

            // add the resulting adjustment to the list
            adjustmentList.add(KarmaAdjustment(recipient, adjustmentAmount))

            // trim the message and load the new matcher to look for the next match
            remainingMessage = remainingMessage.substring(matcher.end(2))
            matcher = matchingPattern.matcher(remainingMessage)
        }
        return adjustmentList
    }
}
