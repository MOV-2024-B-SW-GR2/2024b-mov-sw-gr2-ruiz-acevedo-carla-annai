package com.example.a05_proyecto.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.a05_proyecto.R
import com.example.a05_proyecto.adapter.DashboardFragmentAdapter
import com.example.a05_proyecto.model.Restaurant
import com.example.a05_proyecto.utils.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dashboardAdapter: DashboardFragmentAdapter
    private lateinit var etSearch: EditText
    private lateinit var progressDialog: RelativeLayout
    private lateinit var rlNoRestaurantFound: RelativeLayout

    private val restaurantInfoList = arrayListOf<Restaurant>()

    private val ratingComparator = Comparator<Restaurant> { rest1, rest2 ->
        val rating1 = rest1.restaurantRating.toDoubleOrNull() ?: 0.0
        val rating2 = rest2.restaurantRating.toDoubleOrNull() ?: 0.0
        rating2.compareTo(rating1)
    }

    private val costComparator = Comparator<Restaurant> { rest1, rest2 ->
        val cost1 = rest1.costForOne.toDoubleOrNull() ?: 0.0
        val cost2 = rest2.costForOne.toDoubleOrNull() ?: 0.0
        cost1.compareTo(cost2)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewDashboard)
        etSearch = view.findViewById(R.id.etSearch)
        progressDialog = view.findViewById(R.id.dashboardProgressDialog)
        rlNoRestaurantFound = view.findViewById(R.id.noRestaurantFound)

        rlNoRestaurantFound.visibility = View.INVISIBLE

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        dashboardAdapter = DashboardFragmentAdapter(requireContext(), restaurantInfoList)
        recyclerView.adapter = dashboardAdapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(strTyped: Editable?) {
                filterRestaurants(strTyped.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    private fun filterRestaurants(query: String) {
        rlNoRestaurantFound.visibility = View.INVISIBLE
        val filteredList = ArrayList(restaurantInfoList.filter {
            it.restaurantName.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
        })

        if (filteredList.isEmpty()) {
            rlNoRestaurantFound.visibility = View.VISIBLE
        }

        dashboardAdapter.filterList(filteredList)
    }

    private fun fetchData() {
        if (ConnectionManager().checkConnectivity(requireContext())) {
            progressDialog.visibility = View.VISIBLE

            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    progressDialog.visibility = View.GONE
                    parseResponse(response)
                },
                Response.ErrorListener {
                    progressDialog.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return hashMapOf(
                        "Content-type" to "application/json",
                        "token" to "13714ab03e5a4d"
                    )
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            showNoInternetDialog()
        }
    }

    private fun parseResponse(response: JSONObject) {
        try {
            val responseData = response.getJSONObject("data")
            if (responseData.getBoolean("success")) {
                val data = responseData.getJSONArray("data")
                restaurantInfoList.clear()
                for (i in 0 until data.length()) {
                    val restaurantJsonObject = data.getJSONObject(i)
                    val restaurant = Restaurant(
                        restaurantJsonObject.getString("id"),
                        restaurantJsonObject.getString("name"),
                        restaurantJsonObject.getString("rating"),
                        restaurantJsonObject.getString("cost_for_one"),
                        restaurantJsonObject.getString("image_url")
                    )
                    restaurantInfoList.add(restaurant)
                }
                dashboardAdapter.notifyDataSetChanged()
            }
        } catch (e: JSONException) {
            Toast.makeText(requireContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoInternetDialog() {
        val alterDialog = AlertDialog.Builder(requireContext())
        alterDialog.setTitle("Sin Internet")
        alterDialog.setMessage("No se puede establecer la conexión a Internet.")
        alterDialog.setPositiveButton("Abrir Configuración") { _, _ ->
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
        alterDialog.setNegativeButton("Salir") { _, _ ->
            ActivityCompat.finishAffinity(requireActivity())
        }
        alterDialog.setCancelable(false)
        alterDialog.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }


    override fun onResume() {
        super.onResume()
        if (restaurantInfoList.isEmpty()) fetchData()
    }
}