package id.usereal.storyapp.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import id.usereal.storyapp.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var parentLayout: TextInputLayout? = null
    private val emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_email)

    init {
        // Set the initial start drawable
        setCompoundDrawablesWithIntrinsicBounds(emailIcon, null, null, null)

        // Check validation when focus is lost
        onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (parentLayout != null && !isValidInput(text)) {
                    parentLayout?.error = context.getString(R.string.error_email)
                    setErrorIcon(true)
                } else {
                    parentLayout?.error = null
                    setErrorIcon(false)
                }
            }
        }
    }

    private fun isValidInput(input: CharSequence?): Boolean {
        return !input.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }

    private fun setErrorIcon(showIcon: Boolean) {
        val errorIcon = if (showIcon) {
            ContextCompat.getDrawable(context, R.drawable.ic_error)
        } else {
            null
        }
        setCompoundDrawablesWithIntrinsicBounds(emailIcon, null, errorIcon, null)
    }

    fun setParentLayout(layout: TextInputLayout) {
        parentLayout = layout
    }
}

