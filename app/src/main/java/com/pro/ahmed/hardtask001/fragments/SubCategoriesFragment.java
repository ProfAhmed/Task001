package com.pro.ahmed.hardtask001.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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

import butterknife.BindView;
import butterknife.ButterKnife;


public class SubCategoriesFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.toolbarSubCategoriesFragment)
    Toolbar toolbar;
    @BindView(R.id.rvSubCategories)
    RecyclerView rvSubCategories;
    @BindView(R.id.tvAppBarSubCategories)
    TextView tvAppBarSubCategories;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivChangeLang)
    ImageView ivChangeLang;

    // constants for get argument
    private static final String ARG_ID = "category_id";
    private static final String ARG_CATEGORY_TITLE_EN = "category_nameEn";
    private static final String ARG_CATEGORY_TITLE_AR = "category_nameAr";
    private final String LangKey = "lang";


    List<ModelCategory> categories; // to store webservice data

    SharedPreferences prefs; // to get selected Lang
    SharedPreferences.Editor editor; // to set Lang in preference

    // declare variables to store arguments data
    private int mId;
    private String titleEn;
    private String titleAR;
    private String checkLang;

    public SubCategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static SubCategoriesFragment newInstance(int mId, String titleEn, String titleAr) {
        SubCategoriesFragment fragment = new SubCategoriesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, mId);
        args.putString(ARG_CATEGORY_TITLE_EN, titleEn);
        args.putString(ARG_CATEGORY_TITLE_AR, titleAr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getInt(ARG_ID);
            titleEn = getArguments().getString(ARG_CATEGORY_TITLE_EN);
            titleAR = getArguments().getString(ARG_CATEGORY_TITLE_AR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_categories, container, false);
        ButterKnife.bind(this, view);
        init();
        sendSubCategoriesRequest();
        return view;
    }

    private void init() {
        categories = new ArrayList<>();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        //  ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //  ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false); // hide App Title

        // setHasOptionsMenu(true);
        checkLang = prefs.getString(LangKey, "en");
        if (checkLang.equals("ara")) {
            tvAppBarSubCategories.setText(titleAR);
        } else {
            tvAppBarSubCategories.setText(titleEn);
        }
        GridLayoutManager llm = new GridLayoutManager(getActivity(), 2);
        rvSubCategories.setHasFixedSize(true);
        rvSubCategories.setLayoutManager(llm);
        ivBack.setOnClickListener(this);
        ivChangeLang.setOnClickListener(this);
    }

    private void sendSubCategoriesRequest() {

        final String url = "http://souq.hardtask.co/app/app.asmx/GetCategories?categoryId=" + mId + "&countryId=1"; // subCategories webservice url

        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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

                            if (response.length() == 0) {
                                if (checkLang.equals("ara")) {

                                    Toast.makeText(getActivity(), "لا يوجد منتجات حاليا", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getActivity(), "no sub categories here", Toast.LENGTH_SHORT).show();
                                }
                            }
                            CategoryAdapter adapter = new CategoryAdapter(categories, getActivity());
                            rvSubCategories.setAdapter(adapter);

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                // implement back button
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                break;
            case R.id.ivChangeLang:

                checkLang = prefs.getString(LangKey, "en"); // get saved language in preference
                if (checkLang.equals("ara")) { // set english if current language is arabic
                    editor.putString(LangKey, "en");
                    editor.commit();
                    Toast.makeText(getActivity(), "Language is English ", Toast.LENGTH_SHORT).show();
                } else {// set arabic if current language is english
                    editor.putString(LangKey, "ara");
                    editor.commit();
                    Toast.makeText(getActivity(), "اللغة العربية ", Toast.LENGTH_SHORT).show();

                }
        }
    }
}
