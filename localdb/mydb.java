package com.sabzilana.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sabzilana.model.CartData;

import java.util.ArrayList;

/**
 * Created by welcome on 12-10-2016.
 */
public class SabzilanaDB extends SQLiteOpenHelper {
    final static String DB_TABLE_WISHLIST = "wish_list";
    final static String DB_TABLE_CARTLIST = "cart_list";
    final static String WISHLIST_productID = "productID",
            WISHLIST_name = "name",
            WISHLIST_image = "image",
            WISHLIST_price = "price",
            WISHLIST_mrp = "mrp",
            WISHLIST_weight = "weight",
            WISHLIST_caption = "caption";
    final static String CARTLIST_productID = "productID",
            CARTLIST_priceID = "priceID",
            CARTLIST_name = "name",
            CARTLIST_image = "image",
            CARTLIST_price = "price",
            CARTLIST_mrp = "mrp",
            CARTLIST_quantity = "quantity",
            CARTLIST_weight = "weight",
            CARTLIST_caption = "caption";
    final static int DB_VERSION = 8;
    static String DB_DBNAME;
    Context con;
    SabzilanaDB mLocalDecorntDB;
    /**/ SQLiteDatabase sql;

    String StrQueryWishList = "create table " + DB_TABLE_WISHLIST + "(id INTEGER PRIMARY KEY AUTOINCREMENT,productID string" +
            ",name string,image string,price string,mrp string,caption string)";

    String StrQueryCartList = "create table " + DB_TABLE_CARTLIST + "(id INTEGER PRIMARY KEY AUTOINCREMENT,productID string" +
            ",priceID string,name string,image string,price string,mrp string,quantity string,weight string,caption string)";

    public SabzilanaDB(Context context, String localDbUserIdName) {
        super(context, localDbUserIdName, null, DB_VERSION);
        con = context;
        DB_DBNAME = localDbUserIdName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StrQueryWishList);
        db.execSQL(StrQueryCartList);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + DB_TABLE_WISHLIST);
        db.execSQL("drop table if exists " + DB_TABLE_CARTLIST);
        onCreate(db);
    }

    public void OpenDB() {
        mLocalDecorntDB = new SabzilanaDB(con, DB_DBNAME);
    }

    public void CloseDB() {
        sql.close();
    }


    public void InsertWishListData(String productID, String name, String image, String price, String mrp,String caption) {
        sql = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(WISHLIST_productID, productID);
        cv.put(WISHLIST_name, name);
        cv.put(WISHLIST_image, image);
        cv.put(WISHLIST_price, price);
        cv.put(WISHLIST_mrp, mrp);
        cv.put(WISHLIST_caption, caption);
        sql.insert(DB_TABLE_WISHLIST, null, cv);

        /*Log.e("productID",productID);
        Log.e("name",name);
        Log.e("image",image);
        Log.e("price",price);
        Log.e("mrp",mrp);
        Log.e("caption",caption);*/

    }

    public void InsertCartListData(String productID, String priceID, String name, String image, String price, String mrp, int productquantity, String weight, String caption) {
        sql = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CARTLIST_productID, productID);
        cv.put(CARTLIST_priceID, priceID);
        cv.put(CARTLIST_name, name);
        cv.put(CARTLIST_image, image);
        cv.put(CARTLIST_price, price);
        cv.put(CARTLIST_mrp, mrp);
        cv.put(CARTLIST_quantity, productquantity);
        cv.put(CARTLIST_weight, weight);
        cv.put(CARTLIST_caption, caption);

        sql.insert(DB_TABLE_CARTLIST, null, cv);

        /*Log.e("productID",productID);
        Log.e("priceID",priceID);
        Log.e("name",name);
        Log.e("image",image);
        Log.e("price",price);
        Log.e("mrp",mrp);
        Log.e("productquantity",String.valueOf(productquantity));
        Log.e("weight",weight);
        Log.e("caption",caption);*/

    }

    public Cursor ShowTableWishList() {
        sql = this.getReadableDatabase();
        String w[] = new String[]{"id", WISHLIST_productID, WISHLIST_name, WISHLIST_image, WISHLIST_price
                , WISHLIST_mrp,WISHLIST_caption};
        Cursor cn = sql.query(DB_TABLE_WISHLIST, w, null, null, null, null, "id" + " ASC");
        return cn;
    }

    public Cursor ShowTableCartList() {
        sql = this.getReadableDatabase();
        String w[] = new String[]{"id", CARTLIST_productID, CARTLIST_priceID, CARTLIST_name,
                CARTLIST_image, CARTLIST_price, CARTLIST_mrp, CARTLIST_quantity, CARTLIST_weight, CARTLIST_caption};
        Cursor cn = sql.query(DB_TABLE_CARTLIST, w, null, null, null, null, "id" + " ASC");
        return cn;
    }

    public void DeleteWishListByID(String productID) {
        try {
            sql = this.getReadableDatabase();
            sql.delete(DB_TABLE_WISHLIST, WISHLIST_productID + "='" + productID + "'", null);
          //  Log.e("DeleteWishListByID: ",productID);
        } catch (Exception e) {

        }

    }

    public void DeleteCartListByPriceID(String priceId) {
        try {
            sql = this.getReadableDatabase();
            sql.delete(DB_TABLE_CARTLIST, CARTLIST_priceID + "='" + priceId + "'", null);
        //    Log.e("CARTLIST_priceID: ",priceId);
            sql.close();
        } catch (Exception e) {
        }
    }

    public void DeleteWishlistItems(String productId) {
        try {
            sql = this.getReadableDatabase();
            sql.delete(DB_TABLE_WISHLIST, WISHLIST_productID + "='" + productId + "'", null);
          //  Log.e("WISHLIST_productID: ",productId);
            sql.close();
        } catch (Exception e) {
        }
    }

    public boolean ifExists(String wishlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + CARTLIST_productID + " FROM " + DB_TABLE_CARTLIST + " WHERE " + CARTLIST_productID + "= '" + wishlist + "'";
        cursor = db.rawQuery(checkQuery, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean ifExistsPriceId(String PriceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + CARTLIST_priceID + " FROM " + DB_TABLE_CARTLIST + " WHERE " + CARTLIST_priceID + "= '" + PriceId + "'";
        cursor = db.rawQuery(checkQuery, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        // String QUERY = "DELETE FROM "+ WISHLIST_TABLE_NAME ;
        //  db.rawQuery(QUERY, null);
        db.delete(DB_TABLE_CARTLIST, null, null);
        System.out.println("Deleted");

        db.close();
    }

    public void UpdateQuantityInDB(String productID, int total) {

        sql = this.getWritableDatabase();

        try {
            String strSQL = "UPDATE " + DB_TABLE_CARTLIST + " SET quantity = " + total + " WHERE productID = '" + productID + "'";

            sql.execSQL(strSQL);
        } catch (Exception e) {
            Log.e("aa", e.getMessage());
        }
    }

    public int getQuentyty(String strproductID) {
        int strReadUnread = 0;
        try {
            String query = "SELECT * FROM " + DB_TABLE_CARTLIST + " WHERE priceID = '" + strproductID + "'";
            Cursor cursor = sql.rawQuery(query, null);
            cursor.moveToFirst();
            strReadUnread = cursor.getInt(7);
            cursor.close();
        } catch (Exception e) {
            return 0;
        }


        return strReadUnread;

    }


    public void UpdateQuantityByPriceIdInDB(String priceID, int total) {

        sql = this.getWritableDatabase();

        try {
            String strSQL = "UPDATE " + DB_TABLE_CARTLIST + " SET quantity = " + total + " WHERE priceID = '" + priceID + "'";

           /* Log.e("Update_priceID: ",priceID);
            Log.e("Update_Qty: ", String.valueOf(total));*/
            sql.execSQL(strSQL);
            sql.close();
        } catch (Exception e) {
            Log.e("aa", e.getMessage());
        }
    }

    public ArrayList<String> getAllProduct()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> friendList = null;
        try{
            friendList = new ArrayList<String>();
            String QUERY = "SELECT * FROM "+ DB_TABLE_WISHLIST ;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    CartData friendList1 = new CartData("","","","","","",0,"","");
                    friendList1.setProductID(cursor.getString(1));
                    friendList.add(friendList1.getProductID());
                }
            }

            db.close();

        }catch (Exception e){
            Log.e("error",e+"");
        }
        return friendList;
    }


}
