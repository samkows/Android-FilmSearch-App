package com.example.skillcinema

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import com.example.skillcinema.databinding.CustomContainerBinding

class CustomViewContainer
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: CustomContainerBinding

    init {
        val inflatedView = inflate(context, R.layout.custom_container, this)

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomViewContainer, 0, 0
        )

        binding = CustomContainerBinding.bind(inflatedView)

        binding.titleView.text = typedArray.getString(R.styleable.CustomViewContainer_title_text)
        binding.quantityView.text =
            typedArray.getString(R.styleable.CustomViewContainer_quantity_text)

        if (typedArray.getBoolean(
                R.styleable.CustomViewContainer_enable_quantity_imageView,
                false
            )
        ) {
            binding.quantityImageView.visibility = VISIBLE
        }

        setStartPaddingToRecyclerView()
        setTopPaddingToRecyclerView(
            typedArray.getFloat(
                R.styleable.CustomViewContainer_recycler_view_top_padding, 24f
            )
        )

        binding.quantityView.setOnClickListener {
            quantityViewClicked?.invoke()
        }
        binding.quantityImageView.setOnClickListener {
            quantityViewClicked?.invoke()
        }

        typedArray.recycle()
    }


    var quantityViewClicked: (() -> Unit)? = null
    val recyclerView = binding.recyclerView

    fun setTitle(text: String?) {
        binding.titleView.text = text
    }

    fun setQuantity(text: String?) {
        binding.quantityView.text = text
    }

    fun enableQuantityOfFilmsOrSeries() {
        binding.quantityOfFilmsOrSeries.visibility = VISIBLE
        binding.recyclerView.visibility = GONE
    }

    fun setQuantityOfFilmsOrSeries(s: String) {
        binding.quantityOfFilmsOrSeries.text = s
    }

    private fun setTopPaddingToRecyclerView(paddingInDp: Float) {
        val r: Resources = resources
        val paddingInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            paddingInDp,
            r.displayMetrics
        )
        binding.recyclerView.updatePadding(top = paddingInPixels.toInt())
    }

    private fun setStartPaddingToRecyclerView() {
        val paddingInDp = 26f
        val r: Resources = resources
        val paddingInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            paddingInDp,
            r.displayMetrics
        )
        binding.recyclerView.updatePadding(left = paddingInPixels.toInt())
    }
}