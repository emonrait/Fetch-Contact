package com.emon.raihan.contactfeatch

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.provider.Settings
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : CustomAppCompatActivity(), ContactAdapter.OnItemClickListener {
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    private var model: MutableList<Contact>? = null
    private var adapter: ContactAdapter? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestContactPermission()

        if (!checkPermission()) {
            requestContactPermission()
        } else {
            model = getContacts()
            adapter = ContactAdapter(model!!, this, this)
            contactRecyclerView.layoutManager = LinearLayoutManager(this)
            contactRecyclerView.adapter = adapter
            adapter?.notifyDataSetChanged()

        }


        et_contact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {

                adapter?.filter?.filter(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })


    }

    private fun requestContactPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
            //callback onRequestPermissionsResult
        } else {
            //builder =
            getContacts()
            //listContacts.text = builder.toString()
        }


    }

    private fun requestContactPermissionCall() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts()
            } else {
                requestContactPermission()
            }
        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    override fun onItemClickListener(contact: Contact) {
        val uName = contact.name
        val uNumber = contact.number
        val uUri = contact.uri

        call(uNumber.toString(), uName.toString(), uUri.toString())
        //phoneNumberOrNameET.setText(uNumber)
    }

    private fun getContacts(): MutableList<Contact>? {
        val contactList: MutableList<Contact> = ArrayList()
        var no: String? = null
        val cr = contentResolver

        val PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )
        val cursor: Cursor? = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
            null,
            null,
            null
        )
        if (cursor != null) {
            try {
                val nameIndex: Int = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val numberIndex: Int =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val uriIndex: Int =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                var name: String
                var number: String
                var uri: String
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex)
                    number = cursor.getString(numberIndex)
                    if (cursor.getString(uriIndex) == null) {
                        uri = ""
                    } else {
                        uri = cursor.getString(uriIndex)
                    }

                    if (number.length == 11) {
                        no = number
                    }
                    if (number.length == 14) {
                        no = number.subSequence(3, 14).toString()
                    } else {
                        no = number
                    }

                    val obj = Contact(0, name, no, uri)

                    contactList.add(obj)
                }
            } finally {
                cursor.close()
            }
        }


        contactList.size
        return contactList
        cursor?.close()
    }

    private fun checkPermission(): Boolean {
        val contact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        val call = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        return contact == PackageManager.PERMISSION_GRANTED && call == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionCall(): Boolean {
        val call = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        return call == PackageManager.PERMISSION_GRANTED
    }

    private fun call(phone: String, name: String, uri: String) {

        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:" + phone)
        callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK


        val dialog = AlertDialog.Builder(this).setCancelable(false)
        val inflater = LayoutInflater.from(this)
        val reg_layout: View =
            inflater.inflate(R.layout.call_diallog, null)

        val btn_call = reg_layout.findViewById<Button>(R.id.btn_call)
        val ivClose = reg_layout.findViewById<ImageView>(R.id.ivClose)
        val tv_mail_send = reg_layout.findViewById<TextView>(R.id.tv_mail_send)
        val ivPerson = reg_layout.findViewById<ImageView>(R.id.ivPerson)


        val welcome_to = reg_layout.findViewById<TextView>(R.id.welcome_to)
        val tvName =
            reg_layout.findViewById<TextView>(R.id.tvName)
        val tv_call_message = reg_layout.findViewById<TextView>(R.id.tv_call_message)
        val tv_call_action = reg_layout.findViewById<TextView>(R.id.tv_call_action)
        val or_send_an_email_to =
            reg_layout.findViewById<TextView>(R.id.or_send_an_email_to)

        tv_call_action.setText(phone)
        tvName.setText(name)

        if (uri == null || uri == "") {
            ivPerson.setImageResource(R.drawable.call_center);
        } else {
            Glide.with(this)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(ivPerson);
        }

        dialog.setView(reg_layout)
        val alertDialog = dialog.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btn_call.setOnClickListener {
            if (!checkPermissionCall()) {
                requestContactPermissionCall()
            } else {
                this.startActivity(callIntent)
            }
        }

        ivClose.setOnClickListener {
            alertDialog.dismiss()
        }

        tv_mail_send.setOnClickListener {

            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "emonrait@gmail.com", null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (emailIntent.resolveActivity(this.packageManager) != null) {
                this.startActivity(Intent.createChooser(emailIntent, "Send email..."))
            }

        }

        alertDialog.show()

    }

}
