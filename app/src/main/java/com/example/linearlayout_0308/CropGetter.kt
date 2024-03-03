package com.example.linearlayout_0308

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.annotation.Nullable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit


class CropGetter(val glistener: GetListener, val url:String)
{
    private val getUrl = url+"/AllCrop";
    private val okhttpClient = OkHttpClient.Builder()
        .build();

    init { }

    public fun getAllCrop()
    {
        glistener.start()

        try
        {
            val request = Request.Builder()
                .url(this.getUrl)
                .get()
                .build()
            glistener.start()
            okhttpClient.newCall(request).enqueue( object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("OkHTTP","[Failure] : $e");
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseStr = response.body!!.string();
                    Log.i("OkHTTP",responseStr);
                    //val status = JSONObject(responseStr);
                    val all = JSONArray(responseStr);
                    GlobalScope.launch {
                        try {
                            withContext(Dispatchers.Main)
                            {
                                val data = all.getJSONObject(0).get("all") as JSONArray
                                val datalist = mutableListOf<String>()
                                for(i in 0 until data.length()){ datalist.add(data.get(i).toString()) }
                                val topic1 = datalist[0]
                                //Log.i("CropData",array.toString())
                                //Log.i("CropData",data As List<String>)
                                glistener.finish(datalist);
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace();
                        }
                    }
                }
            })
        }
        catch (e:Throwable) {
            e.printStackTrace();
        }
    }

}