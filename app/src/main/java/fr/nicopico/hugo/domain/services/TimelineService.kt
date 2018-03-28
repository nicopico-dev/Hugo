package fr.nicopico.hugo.domain.services

import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.model.User

interface TimelineService : FetcherService<Timeline.Entry> {
    var user: User?
    var baby: Baby?
}

