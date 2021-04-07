
package com.fapps.mememaker

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import org.json.JSONObject
import java.lang.Exception
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var selectMemeBtn: Button
    private var memeTemplatesList =  ArrayList<Template>()
    private var memeNamesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getMemes()
        selectMemeBtn = findViewById(R.id.select_btn)

        selectMemeBtn.setOnClickListener { templateSel() }
    }

    private fun templateSel() {
        val dia = Dialog(this)
        dia.setContentView(R.layout.selection)
        val et = dia.findViewById<EditText>(R.id.search_et)
        val lv = dia.findViewById<ListView>(R.id.lv)
        val clrImg = dia.findViewById<ImageView>(R.id.clear_img)

        clrImg.setOnClickListener { et.text.clear() }

        val adapter = MemeAdapter(this, memeTemplatesList)
        lv.adapter = adapter

        lv.setOnItemClickListener { _, _, _, _ -> dia.dismiss() }

        et.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        dia.show()

    }

    private fun getMemes() {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val jsonFile = JSONObject(readJSON())
        val len = jsonFile.length()
        for (i in 1..len) {
            val memeJsonElement: JSONObject = jsonFile.get("Image$i") as JSONObject
            val memeTemplate = Template(memeJsonElement, i)
            memeTemplatesList.add(memeTemplate)
            memeNamesList.add(memeTemplate.name)
        }
    }

    private fun readJSON(): String? {
        var json: String? = null
        try {
            val inis = assets.open("imgLinksFinal.json")
            json = inis.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(this, "JSON can't be loaded", Toast.LENGTH_SHORT).show()
        }
        return json
    }
}
