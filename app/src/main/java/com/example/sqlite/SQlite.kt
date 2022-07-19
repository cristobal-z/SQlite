package com.example.sqlite
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQlite(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(p0: SQLiteDatabase?) {
       p0?.execSQL("create table clientes (cuenta text primary key, fecha text,nombre text,apeellidoP text,apellidoM text, direccion text, entre text,colonia text,ciudad text,pagara text,monto int,telefono text,dias_pago text,primer_pago text,cantidad int, articulo text,precio int,anticipo int, saldo int,referencias text)")

        p0?.execSQL("create table abonos (folio integer primary key autoincrement, no_cuenta text, fecha text, abono int, saldo int,inspecion text)")

        p0?.execSQL("create table fechar (id integer primary key autoincrement, cuenta text, fecha text, estado text)")

        p0?.execSQL("create table clientes_pagados (id integer primary key autoincrement, cuenta text, nombre text,apeellidoP text,apellidoM text, direccion text,colonia text,ciudad text, articulo text, estado text)")




    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }


}