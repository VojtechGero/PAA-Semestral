package com.example.semestral

import com.example.semestral.data.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun missingOptionalApiFieldsCanBeSaved() {
        val show = ShowDto(42, "Example", null, null, null, null, null, null).toSavedShow()
        assertEquals("", show.summary)
        assertEquals("", show.genres)
        assertNull(show.imageUrl)
        assertNull(show.rating)
        assertEquals("https://www.tvmaze.com/shows/42", show.tvmazeUrl)
        assertEquals(WatchStatus.PLAN_TO_WATCH, show.status)
    }

    @Test
    fun originalImageIsUsedWhenMediumIsMissing() {
        val show = ShowDto(1, "Dark", "<p>Summary</p>", ImageDto(null, "https://example.com/poster.jpg"),
            listOf("Drama", "Mystery"), "2017-12-01", RatingDto(8.7), "https://www.tvmaze.com/shows/1")
            .toSavedShow()
        assertEquals("https://example.com/poster.jpg", show.imageUrl)
        assertEquals("Drama · Mystery", show.genres)
        assertEquals(8.7, show.rating!!, 0.001)
    }
}
