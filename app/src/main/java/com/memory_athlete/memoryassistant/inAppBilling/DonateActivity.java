package com.memory_athlete.memoryassistant.inAppBilling;

import static org.solovyev.android.checkout.ResponseCodes.ITEM_ALREADY_OWNED;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.R;

import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.Sku;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DonateActivity extends AppCompatActivity {
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    private ActivityCheckout mCheckout;
    private InventoryCallback mInventoryCallback;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.theme(this, DonateActivity.this);
        setContentView(R.layout.activity_donate);
        ButterKnife.bind(this);
        setTitle("Donate");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        final Adapter adapter = new Adapter();
        mInventoryCallback = new InventoryCallback(adapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(adapter);
        final Billing billing = CheckoutApplication.get(this).getBilling();
        mCheckout = Checkout.forActivity(this, billing);
        mCheckout.start();
        reloadInventory();
    }

    private static String[] getInAppSkus() {
        if (BuildConfig.DEBUG) return new String[]{"1_1", "2_5", "3_20", "4_100", "5_500",
                "test_1_50", "test_2_105", "test_3_205"};
        return new String[]{"1_1", "2_5", "3_20", "4_100", "5_500"};
    }

    private void reloadInventory() {
        final Inventory.Request request = Inventory.Request.create();
        // load purchase info
        request.loadAllPurchases();
        // load SKU details
        request.loadSkus(ProductTypes.IN_APP, getInAppSkus());
        mCheckout.loadInventory(request, mInventoryCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCheckout.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.thanks, Toast.LENGTH_LONG).show();
            mFirebaseAnalytics.logEvent("donated", null);
        }
    }

    @Override
    protected void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }

    private void purchase(Sku sku) {
        final RequestListener<Purchase> listener = makeRequestListener();
        mCheckout.startPurchaseFlow(sku, null, listener);
    }

    /**
     * @return {@link RequestListener} that reloads inventory when the action is finished
     */
    private <T> RequestListener<T> makeRequestListener() {
        return new RequestListener<T>() {
            @Override
            public void onSuccess(@Nonnull T result) {
                reloadInventory();
            }

            @Override
            public void onError(int response, @Nonnull Exception e) {
                if(response == ITEM_ALREADY_OWNED)
                    Toast.makeText(getApplicationContext(),
                            R.string.already_purchased,
                            Toast.LENGTH_SHORT).show();
                reloadInventory();
            }
        };
    }

    private void consume(final Purchase purchase) {
        mCheckout.whenReady(new Checkout.EmptyListener() {
            @Override
            public void onReady(@Nonnull BillingRequests requests) {
                requests.consume(purchase.token, makeRequestListener());
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.sku_title)
        TextView mTitle;
        @BindView(R.id.sku_description)
        TextView mDescription;
        @BindView(R.id.sku_price)
        TextView mPrice;

        private final Adapter mAdapter;

        @Nullable
        private Sku mSku;

        ViewHolder(View view, Adapter adapter) {

            super(view);
            mAdapter = adapter;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        private static void strikeThrough(TextView view, boolean strikeThrough) {
            int flags = view.getPaintFlags();
            if (strikeThrough) {
                flags |= Paint.STRIKE_THRU_TEXT_FLAG;
            } else {
                flags &= ~Paint.STRIKE_THRU_TEXT_FLAG;
            }
            view.setPaintFlags(flags);
        }

        void onBind(Sku sku, boolean purchased) {
            mSku = sku;
            mTitle.setText(getTitle(sku));
            mDescription.setText(sku.description);
            strikeThrough(mTitle, purchased);
            strikeThrough(mDescription, purchased);
            mPrice.setText(sku.price);
        }

        /**
         * @return SKU title without application name that is automatically added by Play Services
         */
        private String getTitle(Sku sku) {
            final int i = sku.title.indexOf("(");
            if (i > 0) {
                return sku.title.substring(0, i);
            }
            return sku.title;
        }

        @Override
        public void onClick(View v) {
            if (mSku == null) {
                return;
            }
            mAdapter.onClick(mSku);
        }
    }

    /**
     * Updates {@link Adapter} when {@link Inventory.Products} are loaded.
     */
    private static class InventoryCallback implements Inventory.Callback {
        private final Adapter mAdapter;

        InventoryCallback(Adapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onLoaded(@Nonnull Inventory.Products products) {
            final Inventory.Product product = products.get(ProductTypes.IN_APP);
            if (!product.supported) {
                // billing is not supported, user can't purchase anything
                return;
            }
            mAdapter.update(product);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private final LayoutInflater mInflater = LayoutInflater.from(DonateActivity.this);
        private Inventory.Product mProduct = Inventory.Products.empty().get(ProductTypes.IN_APP);

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = mInflater.inflate(R.layout.sku, parent, false);
            return new ViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Sku sku = mProduct.getSkus().get(position);
            holder.onBind(sku, mProduct.isPurchased(sku));
        }

        @Override
        public int getItemCount() {
            return mProduct.getSkus().size();
        }

        void update(Inventory.Product product) {
            mProduct = product;
            notifyDataSetChanged();
        }

        void onClick(Sku sku) {
            mFirebaseAnalytics.logEvent("clicked_to_donate", null);
            final Purchase purchase = mProduct.getPurchaseInState(sku, Purchase.State.PURCHASED);
            Timber.v("purchase =%s", purchase);
            if (purchase != null) consume(purchase);
            else purchase(sku);
        }
    }
}