package com.example.linearlayout_0308

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private var video_name :String = "";
    private var mediaController: MediaController? = null;
    private lateinit var imgBitmap: Bitmap;
    private lateinit var openBTN: Button;
    private val REQUEST_CODE = 100;
    private var state:Boolean = false;
    private var uploadState:Boolean = false;
    private var crop_type = "crop";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val uploadBTN = findViewById<Button>(R.id.UploadBTN);
        val croptxt = findViewById<TextView>(R.id.cropTypeTXT);
        //if(!uploadState) {
        //    uploadBTN.isEnabled = false;
        //}
        getCropType()
    }

    private fun openGalleryForImage()
    {
        val intent = Intent(Intent.ACTION_PICK);
        intent.type = "image/*";
        startActivityForResult(intent,REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode == REQUEST_CODE)
        {
            val uploadBTN = findViewById<Button>(R.id.UploadBTN);
            val ImgView = findViewById<ImageView>(R.id.imageView);
            Log.i("Image","Intent = " + data?.data.toString())
            val resolver = this.contentResolver;
            val bitmap = MediaStore.Images.Media.getBitmap(resolver, data?.data)
            this.imgBitmap = bitmap
            ImgView.setImageBitmap(this.imgBitmap);
            uploadBTN.isEnabled = true;
        }
    }

    public fun upload(view: View)
    {
        val textView = findViewById<TextView>(R.id.status_txt);
        val uploadBTN = findViewById<Button>(R.id.UploadBTN);
        uploadBTN.isEnabled = false;
        APIcaller(object: PostListener{
            override fun start(){
                textView.text = "Uploading..."
            }
            override fun finish(data: String) {
                textView.text = data;
                if(data=="Done") { uploadBTN.isEnabled = true; }
            }
        },getString(R.string.ServerIP), "phone.png", this.imgBitmap, crop_type).postImg();
    }

    public fun getCropType()
    {
        CropGetter(object: GetListener{
            override fun start(){}
            override fun finish(data: List<String>) {
                val cropLayoutList = findViewById<LinearLayout>(R.id.cropViewerLayout)
                val croptxt = findViewById<TextView>(R.id.cropTypeTXT);
                for(file in data)
                {
                    val aCropLayout = LinearLayout(this@MainActivity)
                    aCropLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                    aCropLayout.orientation = LinearLayout.HORIZONTAL

                    val txtView = TextView(this@MainActivity)
                    val lpView = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    lpView.setMargins(0,0,0,8)
                    txtView.setLayoutParams(lpView)
                    txtView.text = file

                    val chooseBTN = Button(this@MainActivity)
                    chooseBTN.text = "Choose"
                    chooseBTN.setLayoutParams(lpView)
                    chooseBTN.setOnClickListener {
                        checkBTNstate(file)
                        croptxt.setText("Choosed Crop Type: "+file)
                        croptxt.setTextColor(Color.BLUE)
                    }

                    val viewBTN = Button(this@MainActivity)
                    viewBTN.text = "Preview"
                    viewBTN.setLayoutParams(lpView)
                    viewBTN.setOnClickListener {
                        previewCrop(file)
                    }
                    aCropLayout.addView(txtView)
                    aCropLayout.addView(chooseBTN)
                    aCropLayout.addView(viewBTN)

                    cropLayoutList.addView(aCropLayout)
                }
            }
        },getString(R.string.ServerIP)).getAllCrop();
    }

    public fun openImg(view: View)
    {
        openGalleryForImage();
    }

    public fun toVideoPlay(view: View)
    {
        val intent = Intent(this,VideoPage::class.java)
        intent.putExtra("URL","/FOMresult")
        startActivity(intent)
    }

    private fun checkBTNstate(id:String)
    {
        Log.i("CheckCrop",id)
        crop_type = id;
    }

    private fun previewCrop(id: String)
    {
        val intent = Intent(this,VideoPage::class.java)
        intent.putExtra("URL","/Crop?type="+id)
        startActivity(intent)
    }
}