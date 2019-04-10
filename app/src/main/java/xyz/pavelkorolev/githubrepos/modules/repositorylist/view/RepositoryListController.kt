package xyz.pavelkorolev.githubrepos.modules.repositorylist.view

import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.entities.Repository

class RepositoryListController : EpoxyController() {

    var repositoryList: List<Repository>? = null

    override fun buildModels() {
        val repositoryList = repositoryList ?: return
        for (repository in repositoryList) {
            RepositoryListItemModel(repository, View.OnClickListener {
                // TODO repository click listener
            }).addTo(this)
        }
    }

}

class RepositoryListItemModel(
    private val repository: Repository,
    private val clickListener: View.OnClickListener
) : EpoxyModelWithHolder<RepositoryListItemModel.ViewHolder>() {

    init {
        id(repository.hashCode())
    }

    @LayoutRes
    public override fun getDefaultLayout(): Int = R.layout.list_item_title

    override fun bind(holder: ViewHolder) {
        holder.textView.text = repository.title
    }

    override fun createNewHolder() = ViewHolder()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as RepositoryListItemModel

        if (repository != other.repository) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + repository.hashCode()
        return result
    }

    class ViewHolder : EpoxyHolder() {
        lateinit var textView: TextView
        lateinit var rootView: View

        override fun bindView(view: View) {
            rootView = view
            textView = view.findViewById(R.id.title_text_view)
        }
    }

}
