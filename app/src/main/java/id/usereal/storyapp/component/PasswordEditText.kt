package id.usereal.storyapp.component

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import id.usereal.storyapp.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var parentLayout: TextInputLayout? = null

    init {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        addTextChangedListener { s ->
            if (parentLayout != null && (s == null || s.length < 8)) {
                parentLayout?.error =
                    context.getString(R.string.error_password)
            } else {
                parentLayout?.error = null
            }
        }
    }

    fun setParentLayout(layout: TextInputLayout) {
        parentLayout = layout
    }
}
