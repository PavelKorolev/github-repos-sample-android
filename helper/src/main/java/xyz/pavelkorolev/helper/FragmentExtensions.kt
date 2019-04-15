package xyz.pavelkorolev.helper

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

inline fun <reified T : Fragment> instanceOf(vararg params: Pair<String, Any>): T = T::class.java.newInstance().apply {
    arguments = bundleOf(*params)
}

inline fun FragmentManager.transaction(action: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().action().commit()
}

inline fun <reified T : View> Fragment.find(id: Int): T = view?.findViewById(id) as T

class ArgumentException(override val message: String) : IllegalStateException(message)

fun Fragment.getArgumentString(key: String): String =
    arguments?.getString(key) ?: throw  ArgumentException("Argument $key must not be null")