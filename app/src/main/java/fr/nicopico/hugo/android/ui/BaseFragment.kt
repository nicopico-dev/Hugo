package fr.nicopico.hugo.android.ui

import android.support.v4.app.Fragment
import fr.nicopico.hugo.domain.model.AppState
import redux.api.Store

abstract class BaseFragment : Fragment(), ReduxViewMixin {
    override val appStore: Store<AppState>
        get() = getAppStore()
}