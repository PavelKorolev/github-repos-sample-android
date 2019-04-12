package xyz.pavelkorolev.githubrepos.modules.contributorlist.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.entities.User
import xyz.pavelkorolev.githubrepos.services.ImageLoader

class ContributorListController(private val imageLoader: ImageLoader) : EpoxyController() {

    var contributorList: List<User>? = null

    override fun buildModels() {
        val contributorList = contributorList ?: return
        for (contributor in contributorList) {
            ContributorListItemModel(contributor, imageLoader).addTo(this)
        }
    }

}

class ContributorListItemModel(
    private val contributor: User,
    private val imageLoader: ImageLoader
) :
    EpoxyModelWithHolder<ContributorListItemModel.ViewHolder>() {

    init {
        id(contributor.hashCode())
    }

    @LayoutRes
    public override fun getDefaultLayout(): Int = R.layout.list_item_user

    override fun bind(holder: ViewHolder) {
        holder.textView.text = contributor.login
        imageLoader.loadCircleAvatar(holder.avatarImageView, contributor.avatarUrl)
    }

    override fun createNewHolder() = ViewHolder()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ContributorListItemModel

        if (contributor != other.contributor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + contributor.hashCode()
        return result
    }

    class ViewHolder : EpoxyHolder() {
        lateinit var textView: TextView
        lateinit var avatarImageView: ImageView
        lateinit var rootView: View

        override fun bindView(view: View) {
            rootView = view
            textView = view.findViewById(R.id.title_text_view)
            avatarImageView = view.findViewById(R.id.avatar_image_view)
        }
    }

}
