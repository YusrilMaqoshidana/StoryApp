package id.usereal.storyapp.component

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import id.usereal.storyapp.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var parentLayout: TextInputLayout? = null

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (parentLayout != null && !isValidInput(s)) {
                    // Set error message and icon when input is invalid
                    parentLayout?.error = context.getString(R.string.error_email)
                    setErrorIcon(true)
                } else {
                    // Clear error and remove icon when input is valid
                    parentLayout?.error = null
                    setErrorIcon(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun isValidInput(input: CharSequence?): Boolean {
        return !input.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }

    private fun setErrorIcon(showIcon: Boolean) {
        val errorIcon = if (showIcon) {
            ContextCompat.getDrawable(context, R.drawable.ic_error) // Replace with your error icon drawable
        } else {
            null
        }
        setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], errorIcon, compoundDrawables[3])
    }

    fun setParentLayout(layout: TextInputLayout) {
        parentLayout = layout
    }
}
