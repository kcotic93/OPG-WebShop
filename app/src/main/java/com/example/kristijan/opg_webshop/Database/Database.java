package com.example.kristijan.opg_webshop.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.kristijan.opg_webshop.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME="opgDB.db";
    private static final int DB_VER=1;

    public Database(Context context) {
        super(context, DB_NAME,null,DB_VER);
    }

    public List<Order> getCarts()
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String [] sqlSelect = {"ID","ProductName", "ProductId","Quantity","Price","Discount","Image"};
        String sqlTable = "Orders";

        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, null, null,
                null, null, null);

        final List<Order> result = new ArrayList<>();
       if( c.moveToFirst())
       {
           do {

               result.add(
                       new Order(c.getInt(c.getColumnIndex("ID")),
                       c.getString(c.getColumnIndex("ProductId")),
                       c.getString(c.getColumnIndex("ProductName")),
                       c.getString(c.getColumnIndex("Quantity")),
                       c.getString(c.getColumnIndex("Price")),
                       c.getString(c.getColumnIndex("Discount")),
                       c.getString(c.getColumnIndex("Image"))));
           }while (c.moveToNext());
       }
        return result;
    }

    public void addToCart(Order order)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Orders(ProductId,ProductName,Quantity,Price,Discount,Image) VALUES('%s','%s','%s','%s','%s','%s')",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());

        db.execSQL(query);
    }

    public void cleanCart()
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Orders");
        db.execSQL(query);
    }

    public void deleteFromCart(int ID)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Orders WHERE ID ='%d';",ID);
        db.execSQL(query);
    }

}
