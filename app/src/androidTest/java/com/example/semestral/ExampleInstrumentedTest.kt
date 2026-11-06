package com.example.semestral

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.semestral.data.SavedShow
import com.example.semestral.data.WatchStatus
import com.example.semestral.data.WatchlistDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun watchlistPersistsStatusAndPreventsDuplicates() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val databaseName = "watchlist-instrumentation-test.db"
        context.deleteDatabase(databaseName)
        var database = Room.databaseBuilder(context, WatchlistDatabase::class.java, databaseName).build()
        try {
            val show = SavedShow(1, "Test show", "Summary", null, "Drama", null, null,
                "https://www.tvmaze.com/shows/1")
            database.showDao().insert(show)
            assertEquals(WatchStatus.PLAN_TO_WATCH, database.showDao().getById(1)?.status)
            database.showDao().updateStatus(1, WatchStatus.WATCHING)
            database.showDao().insert(show)
            assertEquals(1, database.showDao().observeAll().first().size)
            assertEquals(WatchStatus.WATCHING, database.showDao().getById(1)?.status)
            database.close()

            database = Room.databaseBuilder(context, WatchlistDatabase::class.java, databaseName).build()
            assertEquals(WatchStatus.WATCHING, database.showDao().getById(1)?.status)
            database.showDao().updateStatus(1, WatchStatus.FINISHED)
            assertEquals(WatchStatus.FINISHED, database.showDao().getById(1)?.status)
            database.showDao().delete(1)
            assertTrue(database.showDao().observeAll().first().isEmpty())
        } finally {
            database.close()
            context.deleteDatabase(databaseName)
        }
    }
}
