package com.ark.automobile.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.ark.automobile.R

class SecondActivity : AppCompatActivity() {
    var btnsearch = findViewById(R.id.btbsearch) as Button
    var btnsell = findViewById(R.id.btnsell) as Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)


        btnsearch.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }

        btnsell.setOnClickListener {
            val intent = Intent(this, AddCarActivity::class.java)
            startActivity(intent);
        }


    }
}
