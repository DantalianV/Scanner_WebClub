package com.dantalian.scanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Toast
import com.dantalian.scanner.databinding.ActivityMainBinding
import com.dantalian.scanner.databinding.ActivityTextAnaylisisBinding
import java.util.regex.Pattern

class TextAnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextAnaylisisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextAnaylisisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val text = intent.getStringExtra("text")

        binding.allText.text = text

        val urlList = getURLs(text)
        val phoneList = getPhoneNumbers(text)
        var temp = urlList.size

        val arrayAdapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, urlList + phoneList)
        binding.list.adapter = arrayAdapter

        binding.capture.setOnClickListener {

        }

        binding.copy.setOnClickListener { copyToClipBoard(text!!) }

        binding.list.setOnItemClickListener { _, _, position, _ ->

            if(urlList.contains(arrayAdapter.getItem(position))) {
                val url = arrayAdapter.getItem(position)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }

            else {
                val num = arrayAdapter.getItem(position)
                val i = Intent(Intent.ACTION_DIAL)
                i.data = Uri.parse("tel:$num")
                startActivity(i)
            }
        }
    }

    private fun getPhoneNumbers(text: String?): ArrayList<String> {
        val regex = ("(\\+\\d{1,2}\\s?)?1?\\-?\\.?\\s?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}")

        val list: ArrayList<String> = arrayListOf()
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(text!!)

        while(matcher.find())
            list.add(matcher.group(0)!!)

        return list
    }

    private fun getURLs(text: String?): ArrayList<String> {
        val regex = ("((http|https)://)(www.)?"
                + "[a-zA-Z0-9@:%._\\+~#?&//=]"
                + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%"
                + "._\\+~#?&//=]*)")

        val list: ArrayList<String> = arrayListOf()
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(text!!)

        while(matcher.find())
           list.add(matcher.group(0)!!)

        return list
    }

    private fun copyToClipBoard(text: String) {
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied", text)
        clipBoard.setPrimaryClip(clipData)
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }
}