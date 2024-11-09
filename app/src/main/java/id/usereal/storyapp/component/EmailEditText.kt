package id.usereal.storyapp.component

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import id.usereal.storyapp.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var parentLayout: TextInputLayout? = null

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (parentLayout != null && !isValidInput(s)) {
                    parentLayout?.error = context.getString(R.string.error_email)
                } else {
                    parentLayout?.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun isValidInput(input: CharSequence?): Boolean {
        return !input.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }

    fun setParentLayout(layout: TextInputLayout) {
        parentLayout = layout
    }
}