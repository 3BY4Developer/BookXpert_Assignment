package com.bitbyter.bookxpert_assignment.ui

import android.app.AlertDialog
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bitbyter.bookxpert_assignment.R
import com.bitbyter.bookxpert_assignment.database.AccountEntity
import com.bumptech.glide.Glide


class AccountAdapter(
    private var accounts: List<AccountEntity>,
    private val onImageSelected: (AccountEntity, Uri?) -> Unit,
    private val onAlternateNameSet: (AccountEntity, String) -> Unit,
    private val onSpeechToText: (AccountEntity) -> Unit
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.accountImage)
        val nameTextView: TextView = view.findViewById(R.id.accountName)
        val alternateNameTextView: TextView = view.findViewById(R.id.alternateName)
        val menuButton: ImageView = view.findViewById(R.id.menuButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.nameTextView.text = account.actName
        holder.alternateNameTextView.text = account.alternateName ?: "No Alternate Name"
        Glide.with(holder.itemView.context)
            .load(account.imageUri)
            .placeholder(R.drawable.ic_add_photo)
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery")
            AlertDialog.Builder(it.context)
                .setTitle("Select Image")
                .setItems(options) { _, which ->
                    if (which == 0) onImageSelected(account, null)
                    else onImageSelected(account, Uri.EMPTY)
                }
                .show()
        }


        holder.menuButton.setOnClickListener {
            val popup = PopupMenu(it.context, holder.menuButton)
            popup.menuInflater.inflate(R.menu.menu_account, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.add_alternate_name -> {
                        val input = EditText(it.context)
                        input.hint = "Enter or Speak Alternate Name"
                        AlertDialog.Builder(it.context)
                            .setTitle("Alternate Name")
                            .setView(input)
                            .setPositiveButton("Save") { _, _ ->
                                val alternateName = input.text.toString()
                                if (alternateName.isNotEmpty()) {
                                    onAlternateNameSet(account, alternateName)
                                    account.alternateName = alternateName
                                    notifyItemChanged(position)
                                }
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                        true
                    }

                    R.id.speech_to_text -> {
                        onSpeechToText(account)
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount() = accounts.size

    fun updateList(newAccounts: List<AccountEntity>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }
}
