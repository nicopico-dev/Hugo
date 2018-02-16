package fr.nicopico.hugo.ui

import android.support.v4.app.Fragment
import fr.nicopico.hugo.redux.StateHelper

abstract class BaseFragment : Fragment(), StateHelper {
    abstract val screen: String
}