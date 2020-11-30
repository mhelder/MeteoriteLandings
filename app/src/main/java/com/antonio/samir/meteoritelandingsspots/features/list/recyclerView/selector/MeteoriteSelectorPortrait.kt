package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView.selector

import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite


class MeteoriteSelectorPortrait(private val view: MeteoriteSelectorView) : MeteoriteSelector {

    override fun selectItem(meteorite: Meteorite) {
        view.selectPortrait(meteorite)
    }
}
