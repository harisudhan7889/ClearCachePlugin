package com.hari.clearcaches

import com.intellij.CommonBundle
import com.intellij.reference.SoftReference
import org.jetbrains.annotations.PropertyKey
import java.lang.ref.Reference
import java.util.ResourceBundle

/**
 * @author Hari Hara Sudhan.N
 */
object Strings {

    //Todo: This class will be used later for getting the string resources

    private val BUNDLE_NAME = "messages.strings"
    private var bundleStrings: Reference<ResourceBundle>? = null

    private fun getBundle(): ResourceBundle {
        var bundle = SoftReference.dereference(bundleStrings)
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME)
            bundleStrings = SoftReference(bundle)
        }
        return bundle!!
    }

    fun message(@PropertyKey(resourceBundle = "messages.strings") key: String, vararg params: Any): String {
        return CommonBundle.message(getBundle(), key, *params)
    }

}