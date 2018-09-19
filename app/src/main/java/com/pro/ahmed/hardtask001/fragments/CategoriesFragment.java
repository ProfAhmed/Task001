package com.pro.ahmed.hardtask001.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.pro.ahmed.hardtask001.R;
import com.pro.ahmed.hardtask001.adapters.CategoryAdapter;
import com.pro.ahmed.hardtask001.models.ModelCategory;
import com.pro.ahmed.hardtask001.patterns.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesFragment extends Fragment {

    @BindView(R.id.rvCategories)
    RecyclerView rvCategories;
    List<ModelCategory> categories;
    private ProgressDialog progressDialog;
    private SharedPreferences prefs;

    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, view);
        init();
        sendCategoriesRequest();
        return view;
    }

    private void init() {
        categories = new ArrayList<>();
        GridLayoutManager llm = new GridLayoutManager(getActivity(), 2);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String checkLang = prefs.getString("lang", "en");
        progressDialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (checkLang.equals("ara")) {
            progressDialog.setMessage("من فضلك انتظر,جارى التحميل...");
        } else {
            progressDialog.setMessage("Loading,Please Wait...");
        }
        progressDialog.show(); // start dialog
        rvCategories.setHasFixedSize(true);
        rvCategories.setLayoutManager(llm);
    }

    private void sendCategoriesRequest() {

        final String url = "http://souq.hardtask.co/app/app.asmx/GetCategories?categoryId=0&countryId=1"; // categories webservice url


        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        progressDialog.dismiss(); // cancel dialog
                        // Process the JSON
                        try {
                            // Loop through the array elements
                            for (int i = 0; i < response.length(); i++) {
                                // Get current json object
                                JSONObject categoryOBJ = response.getJSONObject(i);
                                String titleEn = categoryOBJ.getString("TitleEN");
                                String titleAr = categoryOBJ.getString("TitleAR");
                                String photo = categoryOBJ.getString("Photo");
                                int productCount = categoryOBJ.getInt("ProductCount");
                                int id = categoryOBJ.getInt("Id");
                                ModelCategory category = new ModelCategory(titleEn, titleAr, photo, productCount, id);
                                categories.add(category);
                            }
                            CategoryAdapter adapter = new CategoryAdapter(categories, getActivity());
                            rvCategories.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // show error message
                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }
}
