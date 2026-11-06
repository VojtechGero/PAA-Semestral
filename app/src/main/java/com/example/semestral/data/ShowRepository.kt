package com.example.semestral.data

class ShowRepository(private val dao: ShowDao, private val api: TvMazeApi) {
    val shows = dao.observeAll()

    fun observeShow(id: Int) = dao.observeById(id)
    suspend fun findSaved(id: Int) = dao.getById(id)
    suspend fun search(query: String) = api.search(query).map { it.show.toSavedShow() }
    suspend fun fetchShow(id: Int) = api.getShow(id).toSavedShow()
    suspend fun add(show: SavedShow) = dao.insert(show)
    suspend fun changeStatus(id: Int, status: WatchStatus) = dao.updateStatus(id, status)
    suspend fun remove(id: Int) = dao.delete(id)
}
