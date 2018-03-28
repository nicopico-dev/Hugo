package fr.nicopico.hugo.domain.services

import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.model.User

interface BabyService : FetcherService<Baby> {
    var user: User?
}