package com.jim.grouptodo

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        askNotificationPermission()
        FirebaseMessaging.getInstance().subscribeToTopic("todolist")

        val button: Button = findViewById(R.id.btnAddTask)
        val input: EditText = findViewById(R.id.etTask)
        val list: ListView = findViewById(R.id.list)
        val database = Firebase.database("https://grouptodo-e7508-default-rtdb.europe-west1.firebasedatabase.app")
        val ref = database.getReference("list")

        val todos: MutableList<String> = mutableListOf()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,todos)
        list.adapter = adapter
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d(TAG, token)
        })

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                todos.clear()
                for (todoSnapshot in dataSnapshot.children) {
                    val value = todoSnapshot.getValue(String::class.java) 
                    value?.let { todos.add(it) }
                }
                adapter.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

        button.setOnClickListener {
            val inputText = input.text.toString().trim()
            if (inputText.isNotEmpty()){
                NotificationSender.sendMessage("New thing to do added!",inputText)
                adapter.insert(inputText,0)
                ref.setValue(todos)
                input.text.clear()
            }
        }

        list.setOnItemClickListener { _, _, position, _ ->
            val removedItem = todos[position]

            // Remove item from list
            todos.removeAt(position)
            adapter.notifyDataSetChanged()

            // Remove item from Firebase
            ref.setValue(todos)

            //Send notification
            NotificationSender.sendMessage("Item was removed from the list",removedItem)

            Toast.makeText(this@MainActivity, "Deleted: $removedItem", Toast.LENGTH_SHORT).show()
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // The user granted the permission, FCM can post notifications
        } else {
            // The user denied the permission
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted
            } else {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

//    Make list items deletion
}