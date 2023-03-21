package com.tyro.slack.persistence.impl

import com.tyro.slack.data.Karma
import com.tyro.slack.data.KarmaAdjustment
import com.tyro.slack.data.KarmaList
import com.tyro.slack.persistence.KarmaPersistence

class KarmaPersistenceMapImpl : KarmaPersistence {
    var karmaEventList = ArrayList<String>()
    val karmaMap = hashMapOf<String, Karma>(
        "@Ayesha Fernando" to Karma("@Ayesha Fernando", 5),
        "@Paul Keen" to Karma("@Paul Keen", 10),
        "@Mark Lockery" to Karma("@Mark Lockery", 2),
        "@Cyrus Cadungog" to Karma("@Cyrus Cadungog", 4),
        "@Jon Davey" to Karma("@Jon Davey", 11),
    )

    override fun updateKarma(vararg karmaAdjustment: KarmaAdjustment) {
        karmaAdjustment.forEach { adjustment ->
            println("Saving adjustment of ${adjustment.adjustmentAmount} for ${adjustment.recipient}")

            val karma = karmaMap[adjustment.recipient]
            if (karma != null) {
                karmaMap[adjustment.recipient] = Karma(adjustment.recipient, karma.balance + adjustment.adjustmentAmount)
            } else {
                karmaMap[adjustment.recipient] = Karma(adjustment.recipient, adjustment.adjustmentAmount)
            }
        }
    }

    override fun getKarmaFor(recipient: String): Int {
        println("getKarmaFor($recipient)")
        return karmaMap[recipient]?.balance ?: 0
    }

    override fun getAllKarma(): KarmaList {
        return KarmaList(karmaMap.values.toList())
    }

    override fun hasKarmaEvent(eventId: String): Boolean {
        return karmaEventList.contains(eventId)
    }

    override fun addKarmaEvent(eventId: String) {
        if (!hasKarmaEvent(eventId)) {
            karmaEventList.add(eventId)
        }
    }
}
