package com.example.wapapp2.repository

import com.example.wapapp2.R
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.repository.interfaces.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


class ChatRepositorylmpl private constructor() : ChatRepository {
    private val fireStore = FirebaseFirestore.getInstance()


    companion object {
        private lateinit var INSTANCE: ChatRepositorylmpl

        fun getINSTANCE() = INSTANCE

        fun initialize() {
            INSTANCE = ChatRepositorylmpl()

        }
    }

    override suspend fun sendMsg(calcRoomDTO: CalcRoomDTO, chatDTO: ChatDTO) {
        fireStore
            .collection("calc_rooms")
            .document(calcRoomDTO.id!!)
            .collection("chats")
            .document()
            .set(chatDTO)
            .addOnFailureListener { exception ->
                TODO("전송실패")
            }
    }


    //  https://firebase.google.com/docs/cloud-messaging/http-server-ref

    fun send(tokens : List<String>, roomDTO: CalcRoomDTO, chatDTO: ChatDTO) : Int {
        try {
            //This is not recommended way because anyone can get your API key by using tools like Smali2Java and can send the notification to anyone.
            val apiKey = R.string.Firebase_ApiKey
            val url = URL("https://fcm.googleapis.com/fcm/send")
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.doOutput = true
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "key=$apiKey")

            val remote_msg = JSONObject()
            remote_msg.put("registration_ids", tokens)
            remote_msg.put("priority", "high")

            val data = JSONObject()
            data.put("room_name", roomDTO.name)
            data.put("chatDTO_username", chatDTO.userName)
            data.put("chatDTO_msg", chatDTO.msg)
            data.put("chatDTO_sendedTime", chatDTO.sendedTime)
            data.put("chatDTO_senderId", chatDTO.senderId)

            remote_msg.put("data", data)

            val os: OutputStream = conn.outputStream
            os.write(remote_msg.toString().toByteArray())
            os.flush()
            os.close()

            return conn.responseCode
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }
}