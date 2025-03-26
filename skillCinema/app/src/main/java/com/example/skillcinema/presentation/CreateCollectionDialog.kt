package com.example.skillcinema.presentation

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import com.example.skillcinema.R
import com.google.android.material.textfield.TextInputEditText

class CreateCollectionDialog(
    private val context: Context,
    private val onDoneClicked: (String) -> Unit
) {
    fun show() {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_create_collection,
            FrameLayout(context)
        )
        val builder = AlertDialog.Builder(context)
        builder.setView(view)

        val dialog: AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.show()

        view.findViewById<ImageView>(R.id.dialog_close_icon).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<Button>(R.id.dialog_done_button).setOnClickListener {
            val dialogEditText =
                view.findViewById<TextInputEditText>(R.id.dialog_create_collection_edit_text)
            if (!dialogEditText.text.isNullOrBlank()) {
                onDoneClicked(dialogEditText.text.toString())
            }
            dialog.dismiss()
        }
    }
}