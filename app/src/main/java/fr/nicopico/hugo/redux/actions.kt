@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.service.User

data class REQUEST_REMOTE_DATA(val user: User)
data class REMOTE_DATA_FETCHED(val timeline: Timeline)
data class REMOTE_DATA_ERROR(val error: Exception)

data class ADD_ENTRY(val entry: Timeline.Entry)
data class UPDATE_ENTRY(val entry: Timeline.Entry)
data class REMOVE_ENTRY(val entry: Timeline.Entry)

data class ENTRY_ADDED(val entry: Timeline.Entry)
data class ENTRY_MODIFIED(val entry: Timeline.Entry)
data class ENTRY_REMOVED(val entry: Timeline.Entry)