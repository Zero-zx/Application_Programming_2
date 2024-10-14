import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.model.Category

class CustomSpinnerAdapterV2(
    private val context: Context,
    private val categories: MutableList<Category>
) : BaseAdapter() {

    fun submitList(newList: List<Category>) {
        categories.clear()
        categories.addAll(newList)
        notifyDataSetChanged()
    }

    private val topLevelCategories: List<Category> = categories.filter { it.parent == null }

    override fun getCount(): Int {
        return topLevelCategories.size + categories.filter { it.parent != null }.size
    }

    override fun getItem(position: Int): Any {
        var index = 0
        for (category in topLevelCategories) {
            if (index == position) {
                return category // Return top-level category
            }
            index++
            for (subCategory in categories.filter { it.parent?.id == category.id }) {
                if (index == position) {
                    return subCategory // Return subcategory
                }
                index++
            }
        }
        return Any() // Just a placeholder
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        val textView: TextView = view.findViewById(R.id.text)
        val imageView: ImageView = view.findViewById(R.id.icon) // Custom layout with an ImageView

        val item = getItem(position)
        when (item) {
            is Category -> {
                textView.text = item.name
                imageView.visibility = View.VISIBLE
                val padding = if (item.parent == null) 0 else 42
                textView.setPadding(padding, 0, 0, 0)
            }
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent!!)
    }
}
