package com.ark.automobile.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TimePicker
import com.ark.automobile.R
import com.ark.automobile.adapters.DetailsAdapter
import com.ark.automobile.adapters.FeaturesAdapter
import com.ark.automobile.commoners.AppUtils
import com.ark.automobile.commoners.BaseActivity
import com.ark.automobile.commoners.K
import com.ark.automobile.models.Car
import com.ark.automobile.utils.*
import com.ark.automobile.utils.PreferenceHelper.get
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.ionicons_typeface_library.Ionicons
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_car.*
import okhttp3.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import timber.log.Timber
import java.io.IOException
import java.util.*

class CarActivity : BaseActivity(), ImageListener, View.OnClickListener {
    private lateinit var car: Car
    private lateinit var featuresAdapter: FeaturesAdapter
    private lateinit var detailsAdapter: DetailsAdapter
    private lateinit var datePicker: DatePickerDialog
    private lateinit var bookDate: EditText
    private lateinit var bookTime: EditText
    private lateinit var bookingView: View
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car)
        prefs = PreferenceHelper.defaultPrefs(this)

        car = intent.getSerializableExtra(K.CAR) as Car

        initViews()
        loadCarInfo()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        toolbarTitle()
        setupFeatures()
        setupDetails()

        carousel.pageCount = car.images.size
        carousel.setImageListener(this)

        if (car.sellerId == getUid()) isMyCar() else notMyCar()

        contactSeller.setOnClickListener(this)
        testDrive.setOnClickListener(this)
        delete.setOnClickListener(this)

        sellerName.setDrawable(AppUtils.setDrawable(this, FontAwesome.Icon.faw_user2, R.color.secondaryText, 15))
        sellerPhone.setDrawable(AppUtils.setDrawable(this, Ionicons.Icon.ion_android_call, R.color.secondaryText, 15))
        sellerLocation.setDrawable(AppUtils.setDrawable(this, Ionicons.Icon.ion_location, R.color.secondaryText, 15))
        sellerEmail.setDrawable(AppUtils.setDrawable(this, Ionicons.Icon.ion_email, R.color.secondaryText, 15))


    }

    private fun toolbarTitle() {
        toolbarLayout.title = ""
        appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var showTitle = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBar.totalScrollRange
                }

                if (scrollRange + verticalOffset == 0) {
                    toolbarLayout.setCollapsedTitleTextColor(Color.WHITE)
                    toolbarLayout.title = "${car.make} ${car.model}"
                    showTitle = true
                } else if (showTitle) {
                    toolbarLayout.title = ""
                    showTitle = false

                }
            }
        })
    }

    private fun setupDetails() {
        detailsRv.setHasFixedSize(true)
        detailsRv.layoutManager = LinearLayoutManager(this)
        detailsRv.addItemDecoration(RecyclerFormatter.SimpleDividerItemDecoration(this))
        detailsAdapter = DetailsAdapter(this)
        detailsRv.adapter = detailsAdapter
    }

    private fun setupFeatures() {
        featuresRv.setHasFixedSize(true)
        featuresRv.layoutManager = LinearLayoutManager(this)
        featuresAdapter = FeaturesAdapter(this)
        featuresRv.adapter = featuresAdapter
    }

    private fun loadCarInfo() {
        description.text = car.description
        detailsAdapter.addDetails(car.details)
        featuresAdapter.addFeatures(car.features)
        sellerName.text = car.sellerName
        sellerPhone.text = car.phone
        sellerLocation.text = car.location
        sellerEmail.text = car.email
    }

    override fun setImageForPosition(position: Int, imageView: ImageView?) {
        val keys = car.images.keys.toList()

        imageView!!.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.loadUrl(car.images[keys[position]]!!)
    }

    // Show delete button & hide other options
    private fun isMyCar() {
        testDrive.hideView()
        contactSeller.hideView()
        delete.showView()
    }

    // Hide delete button and show other options
    private fun notMyCar() {
        delete.hideView()
        testDrive.showView()
        contactSeller.showView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
                   R.id.testDrive -> {
                alert("Book test drive?") {
                    positiveButton("BOOK") {
                        datePicker.show()
                    }

                    negativeButton("CANCEL") {}
                }.show()
            }

            R.id.delete -> {
                alert("Delete ${car.make} ${car.model}") {
                    positiveButton("DELETE") {

                        getFirestore().collection(K.CARS).document(car.id!!).delete()
                                .addOnSuccessListener {
                                    toast("${car.make} ${car.model} deleted")
                                    finish()
                                    AppUtils.animateEnterLeft(this@CarActivity)
                                }
                    }
                    negativeButton("CANCEL") {}
                }.show()
            }

        }
    }







    private fun book() {
        showLoading("Booking test drive...")
        val client = OkHttpClient()

        val urlBuilder = HttpUrl.parse(K.BOOK_TEST_DRIVE_URL)?.newBuilder()
        urlBuilder?.addQueryParameter("car_id", car.id)
        urlBuilder?.addQueryParameter("car_name", "${car.make} ${car.model}")
        urlBuilder?.addQueryParameter("booker_id", getUid())
        urlBuilder?.addQueryParameter("booker_name", prefs[K.NAME])
        urlBuilder?.addQueryParameter("seller_id", car.sellerId)
        urlBuilder?.addQueryParameter("seller_name", car.sellerName)
        urlBuilder?.addQueryParameter("image_url", car.image)
        urlBuilder?.addQueryParameter("time", System.currentTimeMillis().toString())

        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
                runOnUiThread {
                    hideLoading()
                    toast("Error making order. Please try again")
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                runOnUiThread {
                    hideLoading()
                }

                val res = response?.body()?.string()
                Timber.e("RESULTS: $res")
                if (response!!.isSuccessful) {
                    try {
                        val gson = Gson()
                        val json = gson.fromJson(res, JsonObject::class.java)
                        val resultCode = json["resultCode"].asInt
                        runOnUiThread {
                            when (resultCode) {
                                -1 -> {
                                    alert(json["description"].asString, "Failed").show()
                                }
                                0 -> {
                                    alert(json["description"].asString, "Success").show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.animateEnterLeft(this)
    }


}
