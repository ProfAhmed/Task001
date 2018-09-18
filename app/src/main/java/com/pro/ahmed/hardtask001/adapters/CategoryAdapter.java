package com.pro.ahmed.hardtask001.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pro.ahmed.hardtask001.R;
import com.pro.ahmed.hardtask001.fragments.SubCategoriesFragment;
import com.pro.ahmed.hardtask001.models.ModelCategory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context mContext;
    List<ModelCategory> categories;
    SharedPreferences prefs;
    final String LangKey = "lang";
    String checkLang;


    public CategoryAdapter(List<ModelCategory> categories, Context mContext) {
        this.categories = categories;
        this.mContext = mContext;
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        checkLang = prefs.getString(LangKey, "en");
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ModelCategory category = categories.get(position);
        if (checkLang.equals("ara")) { // to get Arabic data
            holder.tvProductName.setText(category.getTitleAr());
        } else {
            holder.tvProductName.setText(category.getTitleEn());
        }
        holder.tvProductCount.setText("( " + category.getProductCount() + " )");
        Glide.with(mContext)
                .load(category.getPhoto())
                .into(holder.ivCustomList);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivCustomList)
        ImageView ivCustomList;
        @BindView(R.id.tvProductName)
        TextView tvProductName;
        @BindView(R.id.tvProductCount)
        TextView tvProductCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // go to sub categories
            ModelCategory category = categories.get(getAdapterPosition());
            AppCompatActivity activity = (AppCompatActivity) mContext;
            Fragment fragment = SubCategoriesFragment.newInstance(category.getId(), category.getTitleEn(), category.getTitleAr());
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (checkLang.equals("ara")) { // start from right to left
                fragmentTransaction.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right, R.anim.in_from_right, R.anim.out_to_left);
            } else { // start from left to right
                fragmentTransaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            }
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
