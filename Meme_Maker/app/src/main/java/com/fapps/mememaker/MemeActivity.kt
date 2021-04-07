package com.fapps.mememaker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MemeActivity : AppCompatActivity() {
    private lateinit var memeImg: ImageView
    private lateinit var setBtn: Button
    private lateinit var sendBtn: Button
    private var origBmp: Bitmap? = null
    private var memeName = ""
    private lateinit var meme: Template
    private lateinit var dialog: Dialog
    private lateinit var diaApplyByn: Button
    private val textEts = ArrayList<EditText>()
    private var selectedColor = 0
    @SuppressLint("UseSparseArrays")
    private val colorMap = HashMap<Int, Int>()
    @SuppressLint("UseSparseArrays")
    val idMap = HashMap<Int, Int>()
    private lateinit var eBmp: Bitmap
    private var tSize = 18
    private lateinit var seeker: SeekBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)
        memeImg = findViewById(R.id.meme_img)
        setBtn = findViewById(R.id.set_btn)
        sendBtn = findViewById(R.id.send_btn)


        meme = intent.getSerializableExtra("meme") as Template

        val url = meme.imgLink
        memeName = meme.name

        initColorM()

        dialog = Dialog(this)
        dialog.setContentView(R.layout.prop_dia)

        seeker = dialog.findViewById(R.id.size_sb)
        seeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tSize = 14 + progress*22/100
            }
        })

        memeImg.requestLayout()
        @Suppress("DEPRECATION")
        memeImg.layoutParams.height = (this.windowManager.defaultDisplay.width*meme.ratio).toInt()



        Picasso.get().load(url).fit().into(memeImg)

        addTV()
        diaApplyByn.setOnClickListener { applyprops() }


        setBtn.setOnClickListener { dialog.show() }

        sendBtn.setOnClickListener { shareMeme(memeImg) }

    }

    override fun onResume() {
        super.onResume()
        memeImg.setOnClickListener {
            val texts = ArrayList<String>()
            for (i in 1..meme.textFieldCount)
                texts.add("text $i")
            textify(texts)
        }
    }

    private fun applyprops() {
        val texts = ArrayList<String>()
        for (i in textEts) {
            texts.add(i.text.toString())
        }
        textify(texts)
    }

    @SuppressLint("SetTextI18n")
    private fun addTV() {
        val ll = dialog.findViewById<LinearLayout>(R.id.main_ll)
        val oet = dialog.findViewById<EditText>(R.id.et_1)
        textEts.add(oet)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        for (i in 2 .. meme.textFieldCount) {
            val net = EditText(this)
            net.layoutParams = params
            net.setText("", TextView.BufferType.EDITABLE)
            net.hint = "text_$i"
            net.isSingleLine = true
            net.textSize = 18F
            textEts.add(net)
            ll.addView(net)

        }

        diaApplyByn = Button(this)
        diaApplyByn.layoutParams = params
        diaApplyByn.text = "Apply"
        diaApplyByn.textSize = 18F
        ll.addView(diaApplyByn)

    }

    private fun initColorM() {
        colorMap[0] = Color.BLACK
        colorMap[1] = Color.WHITE
        colorMap[2] = Color.BLUE
        colorMap[3] = Color.GREEN
        colorMap[4] = Color.RED
        colorMap[5] = Color.rgb(165, 42, 42)
        idMap[0] = R.id.black_gola
        idMap[1] = R.id.white_gola
        idMap[2] = R.id.blue_gola
        idMap[3] = R.id.green_gola
        idMap[4] = R.id.red_gola
        idMap[5] = R.id.brown_gola

    }

    private fun shareMeme(imgv: ImageView) {
        val bmp = loadBitmapFromView(imgv)
        try {
            val cachedPath = File(baseContext.cacheDir, "images")
            cachedPath.mkdirs()
            val file = File(cachedPath, "image.png")
            val outs = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outs)
            outs.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            Toast.makeText(this, "Failed!!", Toast.LENGTH_SHORT).show()
        }

        val imgPath = File(baseContext.cacheDir, "images")
        val newFile = File(imgPath , "image.png")
        val uri: Uri = FileProvider.getUriForFile(baseContext, "com.fapps.mememaker.fileprovider", newFile)

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, contentResolver.getType(uri))
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(intent)
    }

    private fun loadBitmapFromView(imgv: ImageView): Bitmap {
        val b: Bitmap = Bitmap.createBitmap(imgv.width, imgv.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(b)
        imgv.layout(imgv.left, imgv.top, imgv.right, imgv.bottom)
        imgv.draw(canvas)
        return b
    }

    private fun writeOnImage(txt: String, x: Float, y: Float) {
        val bmp = eBmp

        val tf = Typeface.create("Helvetica", Typeface.BOLD)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = colorMap[selectedColor]!!
        paint.typeface = tf
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = convertToPixels(tSize).toFloat()
        val txtRect = Rect()
        val canvas = Canvas(bmp)
        paint.getTextBounds(txt, 0, txtRect.width(), txtRect)
        val w = bmp.width*x
        val h = bmp.height*y

        canvas.drawText(txt, w, h, paint)

        memeImg.setImageBitmap(bmp)
    }

    private fun convertToPixels(nDP: Int): Int {
        val conversionScale = this.resources.displayMetrics.density

        return (nDP * conversionScale + 0.5f).toInt()

    }

    private fun textify(texts: ArrayList<String>) {
        if (origBmp == null) {
            eBmp = loadBitmapFromView(memeImg)
            origBmp = eBmp.copy(eBmp.config, true)
        }
        else
            eBmp = origBmp!!.copy(origBmp!!.config, true)
        for (i in 0 until meme.textFieldCount) {
            val x = meme.xposes[i]
            val y = meme.yposes[i]
            writeOnImage(texts[i], x, y)

        }
    }

    fun colorSelect(view: View) {
        val iv = view as ImageView
        val prevV = dialog.findViewById<ImageView>(idMap[selectedColor]!!)
        prevV!!.alpha = 0.5F
        iv.alpha = 1F
        selectedColor = when (view.id) {
            R.id.black_gola -> 0
            R.id.white_gola -> 1
            R.id.blue_gola -> 2
            R.id.green_gola -> 3
            R.id.red_gola -> 4
            else -> 5
        }
    }

}