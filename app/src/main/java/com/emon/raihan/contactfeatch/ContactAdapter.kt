package com.emon.raihan.contactfeatch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.contact_list_items.view.*


class ContactAdapter(items: List<Contact>, listener: OnItemClickListener, context: Context) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>(),
    Filterable {

    private lateinit var contactList: List<Contact>
    private lateinit var oldContactList: List<Contact>

    private val listener = listener
    private val context = context

    inner class ViewHolder(v: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(v) {

        var name = v.tvUserNameSL
        var number = v.tvUserMobileNo
        var uri = v.userImage
        fun bind(data: Contact) {
            val value = data.name?.let { firstLetterWord(it.uppercase()) }
            //val drawable = TextDrawable.builder().buildRect(value, R.color.colorPrimary)
           // var drawable=  textAsBitmap(value.toString(),22f,)


            name.text = data.name
            number.text = data.number
            if ((data.uri == null) || data.uri.equals("")) {
                uri.setImageResource(R.drawable.ic_person);
                //uri.setImageBitmap(drawable);
            } else {
                Glide.with(context)
                    .load(data.uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(uri);
            }
        }


    }

    init {
        contactList = items
        oldContactList = contactList
        notifyDataSetChanged()

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_list_items, parent, false)
        return ViewHolder(inflater, listener)
    }

    override fun getItemCount(): Int {
        return contactList.size
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            listener.onItemClickListener(contactList[position])
        }
        holder.bind(contactList[position])

    }

//    fun filterList(filteredList: List<Contact>) {
//        contactList = filteredList
//        notifyDataSetChanged()
//    }


    interface OnItemClickListener {
        fun onItemClickListener(contact: Contact)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                if (constraint.toString().isEmpty()) {
                    contactList = oldContactList
                } else {


                    val resultList = ArrayList<Contact>()
                    for (row in oldContactList) {
                        if (
                            row.number.toString().lowercase()
                                .contains(constraint.toString().lowercase()) or
                            row.name.toString().lowercase()
                                .contains(constraint.toString().lowercase())
                        ) {
                            resultList.add(row)
                        }
                    }
                    contactList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = contactList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                contactList = results?.values as ArrayList<Contact>
                notifyDataSetChanged()
            }

        }
    }

    fun firstLetterWord(str: String): String? {
        var result = ""

        // Traverse the string.
        var v = true
        for (i in 0 until str.length) {
            // If it is space, set v as true.
            if (str[i] == ' ') {
                v = true
            } else if (str[i] != ' ' && v == true) {
                result += str[i]
                v = false
                //Log.e("space99",result)
            }
            if (result.length == 2) {
                break
            }

        }
        return result
    }

    fun textAsBitmap(text: String, textSize: Float, textColor: Int): Bitmap? {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setTextSize(textSize)
        paint.setColor(textColor)
        paint.setTextAlign(Paint.Align.LEFT)
        val baseline: Float = -paint.ascent() // ascent() is negative
        val width = (paint.measureText(text) + 0.5f).toInt() // round
        val height = (baseline + paint.descent() + 0.5f).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(text, textSize, baseline, paint)
        return image
    }


}