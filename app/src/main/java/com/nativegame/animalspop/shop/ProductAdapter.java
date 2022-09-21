package com.nativegame.animalspop.shop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;

import java.util.ArrayList;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context mContext;
    private final ArrayList<Product> mProductList;

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        mContext = context;
        mProductList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_recycler_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        // Init the view holder
        Product product = mProductList.get(position);
        viewHolder.mTxtProductDescription.setText(product.getDescription());
        viewHolder.mImageProduct.setImageResource(product.getDrawableResId());
        viewHolder.mBtnProduct.setBackgroundResource(product.getButtonResId());
        viewHolder.mBtnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the confirm dialog
                showDialog(product);
            }
        });
        Utils.createPopUpEffect(viewHolder.itemView, position);
        Utils.createButtonEffect(viewHolder.mBtnProduct);
    }

    public void showDialog(Product product) {
        // Override this method to show the dialog
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageProduct;
        private final TextView mTxtProductDescription;
        private final ImageButton mBtnProduct;

        public ProductViewHolder(@NonNull View view) {
            super(view);
            mTxtProductDescription = view.findViewById(R.id.txt_product);
            mImageProduct = view.findViewById(R.id.image_product);
            mBtnProduct = view.findViewById(R.id.btn_product);
        }

    }

}
