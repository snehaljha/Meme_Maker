package com.fapps.mememaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


class MemeAdapter(private val ct: Context, private val memes: ArrayList<Template>): ArrayAdapter<Template>(ct, 0, memes), Filterable {

    var selectedMemes = memes

    override fun getCount(): Int {
        return selectedMemes.size
    }

    override fun getItem(position: Int): Template? {
        return selectedMemes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any?): CharSequence {
                val meme = resultValue as Template
                return meme.name
            }

            @SuppressLint("DefaultLocale")
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.toLowerCase()
                val filterResults = FilterResults()
                filterResults.values = if (query==null || query.isEmpty())
                    memes
                else {
                    memes.filter {
                        it.name.toLowerCase().contains(query)
                    }
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                selectedMemes = results?.values as ArrayList<Template>
                notifyDataSetChanged()
            }
        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItem: View =
            convertView ?: LayoutInflater.from(ct).inflate(R.layout.list_item, parent,false)

        val name = selectedMemes[position].name
        val memeId = selectedMemes[position].id
        val ins = ct.assets.open("imgs_50x50/${memeId}" + ".jpg")
        val d: Drawable = Drawable.createFromStream(ins, null)
        val tv = listItem.findViewById<TextView>(R.id.tv)
        val imgV = listItem.findViewById<ImageView>(R.id.img_v)
        tv.text = name
        imgV.setImageDrawable(d)
        listItem.setOnClickListener { itemClicked(selectedMemes[position]) }
        return listItem
    }

    private fun itemClicked(meme: Template) {
        val intent = Intent(ct, MemeActivity::class.java)
        intent.putExtra("meme", meme)
        ct.startActivity(intent)
    }
}