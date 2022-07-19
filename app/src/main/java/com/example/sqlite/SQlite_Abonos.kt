package com.example.sqlite
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQlite_Abonos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("create table abonos(no_cuenta text primary key,fecha text,abono int, saldo int)",null)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }


}