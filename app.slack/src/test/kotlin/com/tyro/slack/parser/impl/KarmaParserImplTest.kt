package com.tyro.slack.parser.impl

import com.tyro.slack.data.KarmaAdjustment
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class KarmaParserImplTest {
    private val karmaParser = KarmaParserImpl()

    @Test
    fun `parser should not extract any adjustments for message with no ++ or --`() {
        assertThat(karmaParser.parseMessageForKarma("test"))
            .isEqualTo(
                emptyList<KarmaAdjustment>(),
            )
    }

    @ParameterizedTest
    @CsvSource(
        "'something-else +++', 'something-else', 3",
        "'@adel +++', '@adel', 3",
        "'@adel+++', '@adel', 3",
        "'@jason -- ', '@jason', -2",
        "'@jason -----', '@jason', -5",
        "'@clarence ++++abc', '@clarence', 4",
        "'@adel +++-+-+', '@adel', 3", // adjustment truncates at the first instance of a different char
        "'@jason --+---', '@jason', -2", // adjustment truncates at the first instance of a different char
    )
    fun `parser should extract single adjustment from simple message`(message: String, recipient: String, amount: Int) {
        assertThat(karmaParser.parseMessageForKarma(message))
            .isEqualTo(
                listOf(
                    KarmaAdjustment(
                        recipient = recipient,
                        adjustmentAmount = amount,
                    ),
                ),
            )
    }

    @ParameterizedTest
    @CsvSource(
        "Thanks @adel +++ for doing stuff, @adel, 3",
        "Sorry @jason -- thats wrong, @jason, -2",
    )
    fun `parser should extract single adjustment from complex message`(message: String, recipient: String, amount: Int) {
        assertThat(karmaParser.parseMessageForKarma(message))
            .isEqualTo(
                listOf(
                    KarmaAdjustment(
                        recipient = recipient,
                        adjustmentAmount = amount,
                    ),
                ),
            )
    }

    @Test
    fun `parser should extract two adjustments from complex message`() {
        assertThat(karmaParser.parseMessageForKarma("Thanks @clarence ++++ @adel +++ for stuff"))
            .isEqualTo(
                listOf(
                    KarmaAdjustment(
                        recipient = "@clarence",
                        adjustmentAmount = 4,
                    ),
                    KarmaAdjustment(
                        recipient = "@adel",
                        adjustmentAmount = 3,
                    ),
                ),
            )
    }
}
