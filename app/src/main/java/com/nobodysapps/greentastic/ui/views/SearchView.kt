@file:Suppress("JoinDeclarationAndAssignment")

package com.nobodysapps.greentastic.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.ListPopupWindow
import com.nobodysapps.greentastic.R

class SearchView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val imageView: ImageView
    private val progressBar: ProgressBar
    val editText: EditText
    private val popupWindow: ListPopupWindow
    private val popupArray: MutableList<String> = ArrayList()

    @Suppress("MemberVisibilityCanBePrivate")
    var listener: Listener? = null

    @Suppress("MemberVisibilityCanBePrivate")
    var iconId: Int = -1
        set(value) {
            field = value
            if (value != -1) imageView.setImageResource(value)
        }

    @Suppress("MemberVisibilityCanBePrivate")
    var imageContentDescription: CharSequence
        get() = imageView.contentDescription
        set(value) {
            imageView.contentDescription = value
        }


    @Suppress("MemberVisibilityCanBePrivate")
    var imeOptions: Int
        get() = editText.imeOptions
        set(value) {
            editText.imeOptions = value
        }


    var isLoading: Boolean
        get() = progressBar.visibility == View.VISIBLE
        set(value) {
            if (value) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }

    init {
        imageView = ImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }
        addView(imageView)

        addView(FrameLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            editText = EditText(context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                inputType =
                    (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS)
                addActionListener()
                addTextWatcher()
            }
            addView(editText)

            progressBar = ProgressBar(context).apply {
                val progressHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    PROGRESSBAR_HEIGHT_DP,
                    resources.displayMetrics
                ).toInt()
                layoutParams = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, progressHeight)
                    .apply { gravity = Gravity.END or Gravity.CENTER_VERTICAL }
                isIndeterminate = true
                visibility = View.GONE
            }
            addView(progressBar)
        })
        gravity = Gravity.CENTER_VERTICAL

        popupWindow = ListPopupWindow(editText.context).apply {
            isModal = true
            setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, popupArray))
            anchorView = editText
            setOnItemClickListener { _, view, position, _ ->
                val selectedText = (view as TextView).text.toString()
                if (listener?.onPopupItemClicked(selectedText, position) == true) dismissPopupView()
            }
        }
        setupAttributes(attrs)
    }

    private fun EditText.addTextWatcher() {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                listener?.onTextChanged(s.toString())
            }

        })
    }

    private fun EditText.addActionListener() {
        setOnEditorActionListener { _, actionId, event ->
            if (actionId in listOf(
                    EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO,
                    EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_SEARCH,
                    EditorInfo.IME_ACTION_SEND
                )
                || (event != null && event.action == ACTION_DOWN && event.keyCode == KEYCODE_ENTER)
            ) {
                return@setOnEditorActionListener listener?.onEditTextConfirm(
                    actionId,
                    event
                ) ?: false
            }
            false
        }
    }

    @SuppressLint("PrivateResource")
    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.SearchView, 0, 0)
        iconId = typedArray.getResourceId(R.styleable.SearchView_iconId, -1)
        editText.hint = typedArray.getString(R.styleable.SearchView_android_hint)
        imageContentDescription =
            typedArray.getString(R.styleable.SearchView_android_contentDescription) ?: ""
        imeOptions = typedArray.getInt(R.styleable.SearchView_android_imeOptions, EditorInfo.IME_ACTION_SEARCH)
        typedArray.recycle()
    }

    fun setListener(
        onEditTextAction: (actionId: Int, event: KeyEvent?) -> Boolean,
        onPopupItemClicked: (text: String, position: Int) -> Boolean,
        onTextChanged: (text: String) -> Unit
    ) {
        this.listener = object : Listener {
            override fun onTextChanged(text: String) {
                onTextChanged(text)
            }

            override fun onEditTextConfirm(actionId: Int, event: KeyEvent?) =
                onEditTextAction(actionId, event)

            override fun onPopupItemClicked(text: String, position: Int) =
                onPopupItemClicked(text, position)
        }
    }

    private fun dismissPopupView() {
        popupArray.clear()
        popupWindow.dismiss()
    }

    fun showPopup(items: List<String>) {
//        Log.d("SearchView", lifecycleOwner.lifecycle.currentState.name)
////        if (lifecycleOwner.lifecycle.currentState !in listOf(Lifecycle.State.RESUMED)) {
////            return
////        }
//        lifecycleOwner.lifecycle.addObserver(object: LifecycleObserver {
//            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//            fun onPaused() {
//                dismissPopupView()
//            }
//        })
        popupArray.addAll(items)
        post {
            popupWindow.show()
        }
    }

    fun dismissPopup() {
        dismissPopupView()
    }

    interface Listener {
        fun onTextChanged(text: String)
        fun onEditTextConfirm(actionId: Int, event: KeyEvent?): Boolean
        fun onPopupItemClicked(text: String, position: Int): Boolean
    }

    companion object {
        private const val PROGRESSBAR_HEIGHT_DP = 30f
    }
}