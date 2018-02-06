@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.Baby
import fr.nicopico.hugo.model.Message
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.model.User

object SIGN_IN
data class AUTHENTICATED(val user: User)

object FETCH_BABIES
object STOP_FETCHING_BABIES
data class SELECT_BABY(val baby: Baby)
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

data class DISPLAY_MESSAGE(val message: Message)
data class REMOVE_MESSAGE(val message: Message)