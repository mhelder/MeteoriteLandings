package com.antonio.samir.meteoritelandingsspots.ui.recyclerView

import androidx.lifecycle.Observer
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils

import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListPresenter
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelector

import org.apache.commons.lang3.StringUtils

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
class MeteoriteAdapter(private val mContext: Context, private val meteoriteSelector: MeteoriteSelector, private val mPresenter: MeteoriteListPresenter) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolderMeteorite>() {
    private var mSelectedMeteorite: String? = null
    var vieHolderMeteorite: ViewHolderMeteorite? = null
        private set
    private var mMeteorites: List<Meteorite>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMeteorite {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_meteorite, parent, false)
        val vh = ViewHolderMeteorite(view)

        //On view click use MeteoriteSelector to do execute the proper according the current layout
        view.setOnClickListener { view1 ->
            vieHolderMeteorite = vh
            meteoriteSelector.selectItemId(vh.id)
        }
        return vh
    }


    override fun getItemCount(): Int {
        return if (mMeteorites != null) mMeteorites!!.size else 0
    }

    fun setData(data: List<Meteorite>) {
        mMeteorites = data
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(viewHolder: ViewHolderMeteorite, position: Int) {
        val meteorite = mMeteorites!![position]

        val meteoriteName = meteorite.name
        val year = meteorite.yearString

        val idString = meteorite.id.toString()

        viewHolder.mName.text = mContext.getString(R.string.title, meteoriteName, year)
        viewHolder.mYear.text = year
        viewHolder.mYear.visibility = View.GONE

        viewHolder.mName.contentDescription = meteoriteName
        viewHolder.mYear.contentDescription = year

        setLocationText(meteorite, viewHolder)

        viewHolder.id = idString


        var color = R.color.unselected_item_color
        var title_color = R.color.title_color
        var elevation = R.dimen.unselected_item_elevation

        if (StringUtils.equals(idString, mSelectedMeteorite)) {
            color = R.color.selected_item_color
            title_color = R.color.selected_title_color
            elevation = R.dimen.selected_item_elevation
        }

        viewHolder.mCardview.setCardBackgroundColor(mContext.resources.getColor(color))
        viewHolder.mCardview.cardElevation = mContext.resources.getDimensionPixelSize(elevation).toFloat()
        viewHolder.mName.setTextColor(mContext.resources.getColor(title_color))

    }


    fun setLocationText(meteorite: Meteorite, viewHolder: ViewHolderMeteorite) {
        val address = meteorite.address

        //Always remove the previous observer
        if (viewHolder.liveMet != null && viewHolder.addressObserver != null) {
            viewHolder.liveMet.removeObserver(viewHolder.addressObserver)
            viewHolder.liveMet = null
            viewHolder.addressObserver = null
        }

        if (StringUtils.isNotEmpty(address)) {
            showAddress(viewHolder, address)
        } else {
            //If address is still empty then observe this entity to be aware of any change
            viewHolder.liveMet = mPresenter.getMeteorite(meteorite)

            var addressObserver = Observer<Meteorite> { meteorite1 ->
                val newAddress = meteorite1?.getAddress()
                if (StringUtils.isNotEmpty(newAddress)) {
                    showAddress(viewHolder, newAddress!!)
                    viewHolder.mLocation.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.view_show))
                    viewHolder.liveMet.removeObserver(viewHolder.addressObserver)
                }
            }

            viewHolder.addressObserver = addressObserver
            viewHolder.liveMet.observeForever(viewHolder.addressObserver)
            viewHolder.mLocation.visibility = View.INVISIBLE
        }
    }

    private fun showAddress(viewHolder: ViewHolderMeteorite, address: String) {
        viewHolder.mLocation.text = address
        viewHolder.mLocation.visibility = View.VISIBLE
    }

    fun setSelectedMeteorite(selectedMeteorite: String) {
        notifyDataSetChanged()
        this.mSelectedMeteorite = selectedMeteorite
    }
}