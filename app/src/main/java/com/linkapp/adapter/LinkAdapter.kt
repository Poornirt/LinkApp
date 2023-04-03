package com.linkapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linkapp.R
import com.linkapp.databinding.ListItemsBinding
import com.linkapp.jdo.Link
import java.util.Random

class LinkAdapter(var mContext: Context, var mList: ArrayList<Link>) :
    RecyclerView.Adapter<LinkAdapter.ViewHolder>() {

    private val mListCopy:ArrayList<Link> = mList.clone() as ArrayList<Link>

    class ViewHolder(var binding: ListItemsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(link: Link) {
            with(binding) {
                linkName.text = if (link.name.isNotEmpty()) link.name.replaceFirstChar { char -> char.titlecase() } else "Video ${Random().nextInt()}"
                linkUrl.text = link.link_url
                Glide.with(binding.imageIcon.context).load(link.image_url).into(thumbnail)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemsBinding.inflate(LayoutInflater.from(mContext),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    fun filter(pText: String) {
        val newList = ArrayList<Link>()
        mList.clear()
        if (pText.isEmpty()) {
            mList.addAll(mListCopy as Collection<Link>)
        } else {
            for (link in mListCopy) {
                if (link.name.contains(pText)) {
                    newList.add(link)
                }
            }
            mList.addAll(newList)
        }
    }
}