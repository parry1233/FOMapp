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


class APIcaller(val plistener: PostListener,val url:String , file:String, val bitmap: Bitmap, cropType: String)
{
    private val postUrl = url+"/FOMupload";
    private var filename: String = "";
    private var img: Bitmap;
    private var crop: String = "";
    private val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(60,TimeUnit.SECONDS)
        .readTimeout(60,TimeUnit.SECONDS)
        .writeTimeout(60,TimeUnit.SECONDS)
        .build();

    init
    {
        this.filename = file;
        this.img = bitmap;
        crop = cropType;
    }

    private fun createPayload_GetVideo():String
    {
        val json_obj = JSONObject();
        json_obj.put("filename",this.filename);
        return json_obj.toString();
    }

    private fun IMGtoB64(bitmap: Bitmap):String
    {
        val ByteStream = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ByteStream);
        val b64 = ByteStream.toByteArray();
        return Base64.encodeToString(b64,Base64.DEFAULT);
    }

    private fun createPayload_PostVideo():String
    {
        val json_obj = JSONObject();
        json_obj.put("name",this.filename);
        val b64 =
        json_obj.put("img", IMGtoB64(this.img));
        json_obj.put("crop",this.crop);
        return json_obj.toString();
    }

    /*
    public fun getVideo()
    {
        try
        {
            plistener.start()
            val video = Uri.parse(this.getUrl);
            GlobalScope.launch {
                try {
                    withContext(Dispatchers.Main)
                    {
                        //responseStr = responseStr.replace("<br>","\n")
                        //responseStr =responseStr.replace("&nbsp;"," ")
                        plistener.finish(responseStr);
                    }
                } catch (e: Throwable) {
                    e.printStackTrace();
                }
            }
        }
        catch (e:Throwable) {
            e.printStackTrace();
        }
    }
     */

    public fun postImg()
    {
        plistener.start()

        try
        {
            val requestBody = createPayload_PostVideo().toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(this.postUrl)
                .post(requestBody)
                .build()
            plistener.start()
            okhttpClient.newCall(request).enqueue( object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("OkHTTP","[Failure] : $e");
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseStr = response.body!!.string();
                    Log.i("OkHTTP",responseStr);
                    //val status = JSONObject(responseStr);
                    val status = JSONArray(responseStr);
                    GlobalScope.launch {
                        try {
                            withContext(Dispatchers.Main)
                            {
                                //responseStr = responseStr.replace("<br>","\n")
                                //responseStr =responseStr.replace("&nbsp;"," ")
                                plistener.finish(status.getJSONObject(0).get("status").toString());
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