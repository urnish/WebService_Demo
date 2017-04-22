package com.nationalhandloomcorp.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nationalhandloomcorp.R;
import com.nationalhandloomcorp.adapter.ProductListAdapter;
import com.nationalhandloomcorp.dbhandler.DBHandler;
import com.nationalhandloomcorp.model.ProductlistModel;
import com.nationalhandloomcorp.util.Constant;
import com.nationalhandloomcorp.util.Global;
import com.nationalhandloomcorp.util.MyUpdateWishList;
import com.nationalhandloomcorp.util.RequestMethod;
import com.nationalhandloomcorp.util.RestClient;
import com.nationalhandloomcorp.util.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity implements MyUpdateWishList {
    Context context;
    Global global;
    ProgressDialog progressDialog;
    ArrayList<ProductlistModel> productList = new ArrayList<ProductlistModel>();
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerView;
    ProgressBar progressBar1;
    TextView tvSortbyPrice, tvfilter, txtToastCountMsg;
    int current_page = 0;
    boolean isProgressdialog = true;
    DBHandler dbHandler;
    String subCatId = "";
    String subCatName = "";
    int dbWishSize = 0;
    Dialog dialog;
    RadioButton rblowtohigh, rbhightolow, rbnameatoz, rbnameztoa;
    Button btngoback;
    String filtertype = "";
    int visibleItemCount, totalItemCount, pastVisiblesItems;
    String itemCount = "";
    LinearLayout linearShowToastMsg;
    private ProductListAdapter gridviewAdapter;
    private LinearLayout lyt_not_found;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);
        context = this;
        global = new Global(context);
        dbHandler = new DBHandler(context);
        dbWishSize = dbHandler.getAllProduct().size();
        progressDialog = new ProgressDialog(context);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            subCatId = getIntent().getExtras().getString("subCatId");
            subCatName = getIntent().getExtras().getString("subCatName");
        }

        initToolbar();
        initComp();
        Tools.systemBarLolipop(this);

        if (global.isNetworkAvailable()) {
            new loadProductList().execute();
        } else {
            retryInternet();
        }

        mLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                linearShowToastMsg.setVisibility(View.VISIBLE);

               // int ThisvisibleItemCount = recyclerView.computeHorizontalScrollOffset();
                 int ThisvisibleItemCount = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                if(ThisvisibleItemCount!=-1){
                    txtToastCountMsg.setText("Showing " + String.valueOf(ThisvisibleItemCount+"/" + itemCount+" items"));

                }


                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        linearShowToastMsg.setVisibility(View.GONE);
                    }
                }, 3000);
                if (dy > 0){

                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();


                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                            if (global.isNetworkAvailable()) {
                                loading = false;
                                progressBar1.setVisibility(View.VISIBLE);
                                 new loadProductList().execute();




                            } else {
                                Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();
                            }


                            Log.v("...", "Last Item Wow !");

                        }
                    }

                }
            }
        });

        gridviewAdapter = new ProductListAdapter(ProductListActivity.this, ProductListActivity.this, productList);
        recyclerView.setAdapter(gridviewAdapter);
        recyclerView.getRecycledViewPool().clear();

        gridviewAdapter.setOnItemClickListener(new ProductListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {

                if (which == 0) {
                    Intent it = new Intent(context, ProductDetailActivity.class);
                    it.putExtra("subCatId", productList.get(position).getProductID());
                    startActivity(it);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);

                } else if (which == 1) {

                }

            }
        });

        tvSortbyPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (productList.size() == 0) {
                    Toast.makeText(context, "No Products Found", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = new BottomSheetDialog(ProductListActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.layout_filter);

                    dialog.show();

                    rblowtohigh = (RadioButton) dialog.findViewById(R.id.rblowtohigh);
                    rbhightolow = (RadioButton) dialog.findViewById(R.id.rbhightolow);
                    rbnameatoz = (RadioButton) dialog.findViewById(R.id.rbnameatoz);
                    rbnameztoa = (RadioButton) dialog.findViewById(R.id.rbnameztoa);

                    rblowtohigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            filtertype = "price_l_h";
                            current_page = 0;
                            productList.clear();
                            progressDialog.show();
                            recyclerView.scrollToPosition(1);
                            new loadProductList().execute();

                            dialog.dismiss();
                        }
                    });

                    rbhightolow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            filtertype = "price_h_l";
                            current_page = 0;
                            productList.clear();
                            progressDialog.show();
                            recyclerView.scrollToPosition(1);
                            new loadProductList().execute();

                            dialog.dismiss();
                        }
                    });


                    rbnameatoz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            filtertype = "name_a_z";
                            current_page = 0;
                            productList.clear();
                            progressDialog.show();
                            recyclerView.scrollToPosition(1);
                            new loadProductList().execute();

                            dialog.dismiss();
                        }
                    });

                    rbnameztoa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            filtertype = "name_z_a";
                            current_page = 0;
                            productList.clear();
                            progressDialog.show();
                            recyclerView.scrollToPosition(1);
                            new loadProductList().execute();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

            }
        });

        tvfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibleItemCount = mLayoutManager.getChildCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= 0) {
                    filtertype = "";
                    current_page = 0;
                    productList.clear();
                    progressDialog.show();
                    recyclerView.scrollToPosition(1);
                    new loadProductList().execute();

                    /*recyclerView.scrollToPosition(1);
                    gridviewAdapter.notifyDataSetChanged();*/

                }
            }
        });

        linearShowToastMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerView.scrollToPosition(0);
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_gray);

        TextView textView = (TextView) toolbar.findViewById(R.id.eshop);
        ImageView searchclick = (ImageView) toolbar.findViewById(R.id.searchclick);
        ImageView homeclick = (ImageView) toolbar.findViewById(R.id.icHome);
        homeclick.setVisibility(View.VISIBLE);
        homeclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, MainActivity.class);
                startActivity(it);
            }
        });
        textView.setText(subCatName);
        searchclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, SearchActivity.class);
                startActivity(it);
            }
        });

        setSupportActionBar(toolbar);


    }

    private void initComp() {
        tvSortbyPrice = (TextView) findViewById(R.id.tvSortbyPrice);
        tvfilter = (TextView) findViewById(R.id.tvfilter);
        txtToastCountMsg = (TextView) findViewById(R.id.txtToastCountMsg);
        btngoback = (Button) findViewById(R.id.btngoback);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerProductList);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        linearShowToastMsg = (LinearLayout) findViewById(R.id.linearShowToastMsg);
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    new loadProductList().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }


    @Override
    public void updateWishCount(int Count) {
      /*  dbWishSize = dbWishSize + Count;
        Dashboard.tvitemCounter.setText(String.valueOf(dbWishSize));*/
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            // Toast.makeText(MainCategoriesActivity.this, "BackWorking", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    private class loadProductList extends AsyncTask<String, Void, String> {

        JSONObject jsonObjectList;
        String resMessage = "";
        String resCode = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (isProgressdialog) {
                isProgressdialog = false;
                progressDialog.show();
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strProductList = Constant.BASE_URL + Constant.PRODUCT_LIST_URL + subCatId + "&type=" + filtertype + "&pagecode=" + current_page++; //+subCatId+"&pagecode=0"

            Log.d("strProductList", strProductList);

            try {
                RestClient restClient = new RestClient(strProductList);
                try {
                    restClient.Execute(RequestMethod.GET);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String ProductList = restClient.getResponse();
                Log.e("ProductList", ProductList);

                if (ProductList != null && ProductList.length() != 0) {
                    jsonObjectList = new JSONObject(ProductList);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            itemCount = jsonObjectList.getString("product_count");
                            JSONArray jsonArray = jsonObjectList.getJSONArray("product_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        ProductlistModel myProductList = new ProductlistModel();
                                        myProductList.setProductID(jsonObjectList.getString("productID"));
                                        myProductList.setName(jsonObjectList.getString("name"));
                                        myProductList.setImage(jsonObjectList.getString("image"));
                                        myProductList.setDiscount(jsonObjectList.getString("discount"));
                                        myProductList.setPrice(jsonObjectList.getString("price"));
                                        myProductList.setMrp(jsonObjectList.getString("mrp"));
                                        myProductList.setStatus(jsonObjectList.getString("status"));
                                        productList.add(myProductList);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            //  gridviewAdapter.notifyDataSetChanged();
            //  gridviewAdapter.notifyItemChanged(0, productList.size());
            if (productList.size() == 0) {
                // recyclerView.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
                btngoback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

            } else {
                lyt_not_found.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                //   lyt_not_found.setVisibility(View.GONE);
            }
            if (!resMessage.equalsIgnoreCase("No data found.")) {
                progressDialog.dismiss();
                gridviewAdapter.notifyDataSetChanged();

                //   recyclerView.scrollToPosition(productList.size());

                progressBar1.setVisibility(View.GONE);
                loading = true;
            } else {

                progressBar1.setVisibility(View.GONE);
                progressDialog.dismiss();
                loading = false;

            }
        }
    }

}
