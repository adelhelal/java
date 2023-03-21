package com.tyro.slack.persistence.impl

import com.tyro.slack.data.Karma
import com.tyro.slack.data.KarmaAdjustment
import com.tyro.slack.data.KarmaList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KarmaPersistenceMapImplTest {
    private val karmaPersistenceMap = KarmaPersistenceMapImpl()

    @Test
    fun `should return the list of recipients`() {
        val expectedRecipients = KarmaList(karmaPersistenceMap.karmaMap.values.toList())
        assertThat(karmaPersistenceMap.getAllKarma())
            .isEqualTo(expectedRecipients)
    }

    @Test
    fun `should get the balance based on the recipient name`() {
        val expectedRecipient = Karma("user1", 5)
        assertThat(karmaPersistenceMap.getKarmaFor("user1")).isEqualTo(expectedRecipient.balance)
    }

    @Test
    fun `should update the karma of recipient`() {
        val karmaAdjustment = KarmaAdjustment("user1", 4)

        val balanceBefore = karmaPersistenceMap.getKarmaFor("user1")
        assertThat(balanceBefore).isEqualTo(5)

        karmaPersistenceMap.updateKarma(karmaAdjustment)

        val balanceAfter = karmaPersistenceMap.getKarmaFor("user1")
        assertThat(balanceAfter).isEqualTo(9)
    }

    @Test
    fun `should update the karma of recipients`() {
        val karmaAdjustment1 = KarmaAdjustment("user3", 4)
        val karmaAdjustment2 = KarmaAdjustment("user5", 9)

        val balanceBefore1 = karmaPersistenceMap.getKarmaFor("user3")
        assertThat(balanceBefore1).isEqualTo(2)
        val balanceBefore2 = karmaPersistenceMap.getKarmaFor("user5")
        assertThat(balanceBefore2).isEqualTo(11)

        karmaPersistenceMap.updateKarma(karmaAdjustment1, karmaAdjustment2)

        val balanceAfter1 = karmaPersistenceMap.getKarmaFor("user3")
        assertThat(balanceAfter1).isEqualTo(6)
        val balanceAfter2 = karmaPersistenceMap.getKarmaFor("user5")
        assertThat(balanceAfter2).isEqualTo(20)
    }



}
