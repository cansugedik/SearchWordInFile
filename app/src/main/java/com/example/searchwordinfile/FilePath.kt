package com.example.searchwordinfile

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import java.io.*
import java.lang.StringBuilder
import kotlin.collections.ArrayList

object FilePath {
    var path_: String? = null
    var mask_: String? = null
    var name_: String? = null
    var search_ = "'"

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFilePath(
        context: Context?,
        path: String,
        name: String?,
        mask: String?,
        searchtext: String
    ) {
        path_ = path
        mask_ = mask
        search_ = searchtext
        name_ = name
        val dir = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val files = dir.listFiles()
        for (file in files) {
            if (file.name.startsWith(name_!!)) {
                ara(mask, path)
            }
        }
        if (arananSozcukListesi != null && arananSozcukListesi!!.size > 0) createSearchTextFile(
            arananSozcukListesi
        )
    }

    var words: ArrayList<String> = ArrayList<String>()
    var charSearch_: String? = null
    var charArrayList = CharArray(0)
    var charArraySearchList = CharArray(0)
    var tmpArray: ArrayList<String> = ArrayList<String>()
    var arananSozcukListesi: ArrayList<String> = ArrayList()
    val list: ArrayList<String> = ArrayList()

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun ara(k: String?, yol: String) {
        val klasor = File(yol)
        val icerik = klasor.list()
        for (i in icerik.indices) {
            tmpArray = ArrayList<String>()
            charArraySearchList = search_.toCharArray()
            val yol1 = yol + "/" + icerik[i]
            val sz = File(yol1)
            if (sz.isDirectory) {
                val sox = sz.toString() + ""
                ara(k, sox)
            } else {
                if (icerik[i].indexOf(k!!) != -1) {
                    val file = yol + "/" + icerik[i]
                    var result: String? = null

                    //verilen dosya yolunu bul ve içeriğini getir
                    result = getUrlContent(file, result)

                    //cümleler arası boşlukları ayır
                    val correctedText = getContentFromSpaces(result)

                    //kelimeler arası boşlukları ayır
                    getWordsFromSpaces(correctedText)

                    //kelimeleri harfe çevir ve arama işlemini yap
                    translateWordToLetterAndSearch
                }
            }
        }
        arananSozcukListesi?.sort()
    }

    private val translateWordToLetterAndSearch: Unit
        private get() {
            for (j in tmpArray.indices) {
                val stringBuilder = StringBuilder()
                var sayac = 0
                if (tmpArray[j].contains(search_)) {
                    val string = tmpArray[j]
                    charArrayList = string.toCharArray()
                    for (r in charArrayList.indices) {
                        val char_ = charArrayList[r].toString()
                        for (p in charArraySearchList.indices) {
                            charSearch_ = charArraySearchList[p].toString()
                            if (char_ == charSearch_ && sayac == p && sayac != charArraySearchList.size) {
                                sayac++
                            }
                            if (char_ == "'") sayac = 0
                        }
                        if (sayac == charArraySearchList.size && char_ != (charSearch_)) {
                            stringBuilder.append(charArrayList[r])
                        }
                    }
                    if (!arananSozcukListesi!!.contains(stringBuilder.toString()))
                        arananSozcukListesi!!.add(stringBuilder.toString())

                }
            }
        }

    private fun getWordsFromSpaces(correctedText: Array<String?>) {
        if (correctedText.size > 0) for (j in correctedText.indices) {
            if (correctedText[j]!!.contains(" ") || correctedText[j]!!.contains("\n")) {

                words = ArrayList()
                if (correctedText[j]!!.contains(" "))
                    words =
                        correctedText[j]!!.split(" ") as ArrayList<String>
                else if (correctedText[j]!!.contains("\n"))
                    words = correctedText[j]!!.split("\n") as ArrayList<String>
                if (words!!.size > 0) for (t in words!!.indices) {
                    if (words!![t]!!.trim { it <= ' ' } != "") {
                        tmpArray.add(words!![t]!!.trim { it <= ' ' })
                    }
                }
            } else {
                if (words != null) for (t in words!!.indices) if (words!![t] != "") tmpArray.add(
                    words!![t]
                )
            }
        }
    }

    private fun getContentFromSpaces(result: String?): Array<String?> {
        var correctedText = arrayOfNulls<String>(0)
        if (result != null && result.length > 0) {
            if (result.contains("\n")) correctedText =
                result.split("\n").toTypedArray() else if (result.contains(" ")) correctedText =
                result.split(" ").toTypedArray()
        }
        return correctedText
    }

    private fun getUrlContent(file: String, result: String?): String? {
        var result = result
        var reader: DataInputStream? = null
        try {
            reader = DataInputStream(FileInputStream(file))
            var nBytesToRead = 0
            try {
                nBytesToRead = reader.available()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (nBytesToRead > 0) {
                val bytes = ByteArray(nBytesToRead)
                try {
                    reader.read(bytes)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                result = String(bytes)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return result
    }

    //bulunan kelimeleri dosya içerisine yazdır işlemi
    fun createSearchTextFile(text: ArrayList<String>?) {
        val file = File(Environment.getExternalStorageDirectory().toString() + "/" + "SearchInFile")
        if (!file.isDirectory) {
            file.mkdir()
        }
        file.delete() //dosya her aramada temizle
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            val out = BufferedWriter(FileWriter(file, true).append("\n"))
            try {
                for (i in text!!.indices) out.write(
                    """
                        ${text[i]}
                        
                        """.trimIndent()
                )
            } catch (e: IOException) {
                println("Exception")
            } finally {
                out.close()
            }
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
}