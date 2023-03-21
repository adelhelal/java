package com.tyro.slack.persistence

import com.tyro.slack.data.KarmaAdjustment
import com.tyro.slack.data.KarmaList

interface KarmaPersistence {
    fun updateKarma(vararg karmaAdjustment: KarmaAdjustment)
    fun getKarmaFor(recipient: String): Int
    fun getAllKarma(): KarmaList
    fun hasKarmaEvent(eventId: String): Boolean
    fun addKarmaEvent(eventId: String)
}
