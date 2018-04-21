@file:Suppress("ClassName", "ClassNaming")

package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.model.Message
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.model.User

data class AUTHENTICATED(val user: User)
object DISCONNECTED
object GO_BACK
object EXIT_APP
object ON_APP_EXIT

object FETCH_BABIES
object STOP_FETCHING_BABIES
data class SELECT_BABY(val baby: Baby)
object UNSELECT_BABY
data class ADD_BABY(val baby: Baby)
data class UPDATE_BABY(val baby: Baby)
data class REMOVE_BABY(val baby: Baby)
data class BABY_ADDED(val baby: Baby)
data class BABY_MODIFIED(val baby: Baby)
data class BABY_REMOVED(val baby: Baby)

object FETCH_TIMELINE
object STOP_FETCHING_TIMELINE
data class REMOTE_ERROR(val error: Exception)

data class ADD_ENTRY(val entry: Timeline.Entry)
data class UPDATE_ENTRY(val entry: Timeline.Entry)
data class REMOVE_ENTRY(val entry: Timeline.Entry)

data class ENTRY_ADDED(val entry: Timeline.Entry)
data class ENTRY_MODIFIED(val entry: Timeline.Entry)
data class ENTRY_REMOVED(val entry: Timeline.Entry)

data class ENTRIES_ADDED(val entries: List<Timeline.Entry>)
data class ENTRIES_REMOVED(val entries: List<Timeline.Entry>)

data class DISPLAY_MESSAGE(val message: Message)
data class REMOVE_MESSAGE(val message: Message)