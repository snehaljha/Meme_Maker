package com.fapps.mememaker

import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class Template (obj: JSONObject, val id: Int) : Serializable {  // SINGLE MEME INFO/TEMPLATE

    val name = obj["Title"] as String
    val imgLink = obj["Image Link"] as String
    val textFieldCount = obj["Text Fields"] as Int // DEFAULT NO. OF TEXTFIELDS
    val xposes = ArrayList<Float>() // X CORDINATES OF TEXTBOXES
    var yposes = ArrayList<Float>() // Y CORDINATES OF TEXTBOXES
    private val origWidth = obj["Original Width"].toString().toFloat()
    private val origHeight = obj["Original Height"].toString().toFloat()
    val ratio: Float  = (origHeight/origWidth)
    private val values = ArrayList<ArrayList<Int>>()
    private val widths = ArrayList<Int>()
    private var rWidth = -1.0

    init {

        val tfStyle = obj["Styles"] as JSONArray
        val resizedTag = obj["Resized Width"] as String
        val rw = extractRw(resizedTag)

        for (i in 0 until tfStyle.length()) {
            val pcs = getatts(tfStyle[i].toString().split(" "))
            values.add(pcs)
        }

        computeXY(rw)
    }

    private fun extractRw(str: String): Int {
        var s: String = str.removePrefix("width: ")
        s = s.removeSuffix("px;")
        return s.toInt()
    }

    private fun getatts(l: List<String>): ArrayList<Int> {
        val rl = ArrayList<Int>()
        for (i in l.indices) {
            if (l[i] == "left:" || l[i] == "top:" || l[i]=="width:" || l[i] == "height:")
                rl.add(l[i+1].removeSuffix("px;").toInt())
        }

        return rl
    }

    private fun computeXY(rw: Int) {
        rWidth = rw.toDouble()
        for (i in values) {
            val left: Float = i[0].toFloat()
            val top: Float = i[1].toFloat()
            val width: Float = i[2].toFloat()
            val height: Float = i[3].toFloat()
            widths.add(width.toInt())
            val x = (left+width/2)/rw
            val y = (top+height/2)/(rw*ratio)
            xposes.add(x)
            yposes.add(y)
        }
    }


}