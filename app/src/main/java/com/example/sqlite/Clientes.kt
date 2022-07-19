package com.example.sqlite

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.Control
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*

class Clientes : AppCompatActivity() {
    var txt_nom_bus: EditText? =null
    var sp_bus: Spinner?=null

    var tl_clientes: TableLayout?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        txt_nom_bus = findViewById(R.id.txt_nom_bus)
        sp_bus = findViewById(R.id.sp_bus)
        tl_clientes = findViewById(R.id.tl_clientes)

        var listCampos = arrayOf("Nombre","Ciudad")
        var adaptador : ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_spinner_item,listCampos)
        sp_bus?.adapter=adaptador




    }


    fun Buscar(view: View){

        //Toast.makeText(this,listaBuscar, Toast.LENGTH_LONG).show()
        LlenartablaClie()




    }



    fun LlenartablaClie(){
        tl_clientes?.removeAllViews()
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        val listaBuscar = sp_bus?.selectedItem.toString()
        var query =""
        var bus = txt_nom_bus?.text.toString()
        if(!bus.isEmpty()){
            if (listaBuscar=="Ciudad"){
                query = "select cast(substr(c.cuenta ,4 ) as int) as numero, c.nombre, c.apeellidoP, c.ciudad,c.articulo, a.saldo  from clientes c, abonos a  where c.ciudad  = '$bus' and c.cuenta = a.no_cuenta group by a.no_cuenta order by numero asc "
            } else{
                query ="select cast(substr(c.cuenta ,4 ) as int) as numero, c.nombre, c.apeellidoP, c.ciudad,c.articulo, a.saldo  from clientes c, abonos a  where c.nombre like '%$bus%' and c.cuenta = a.no_cuenta group by a.no_cuenta order by numero asc "
            }
            var fila = BaseDatos.rawQuery(query,null)
            if(fila.moveToFirst()==true){
                fila.moveToFirst()

                do {
                    val registro = LayoutInflater.from(this).inflate(R.layout.item_table_clientes,null,false)
                    val tvc_cuenta =registro.findViewById<View>(R.id.tvc_cuenta) as TextView
                    val tvc_nombre =registro.findViewById<View>(R.id.tvc_nombre) as TextView
                    val tvc_apellido =registro.findViewById<View>(R.id.tvc_apellido) as TextView
                    val tvc_ciudad =registro.findViewById<View>(R.id.tvc_ciudad) as TextView
                    val tvc_articulo =registro.findViewById<View>(R.id.tvc_articulo) as TextView
                    val tvc_saldo =registro.findViewById<View>(R.id.tvc_saldo) as TextView


                    tvc_cuenta.setText(fila.getString(0))
                    tvc_nombre.setText(fila.getString(1))
                    tvc_apellido.setText(fila.getString(2))
                    tvc_ciudad.setText(fila.getString(3))
                    tvc_articulo.setText(fila.getString(4))
                    tvc_saldo.setText(fila.getString(5))

                    tl_clientes?.addView(registro)

                }while (fila.moveToNext())
            } else{

                Toast.makeText(this,"No hay registros",Toast.LENGTH_LONG).show()

            }


        }else {Toast.makeText(this,"Campo vacio",Toast.LENGTH_LONG).show()}

    }

    fun clickobtenerCodigo(view: View){
        ResetColor()
        view.setBackgroundColor(Color.GRAY)
        val registro = view as TableRow
        val ControlCuenta = registro.getChildAt(0) as TextView
        val Controlnombre = registro.getChildAt(1) as TextView
        val Controlapellido = registro.getChildAt(2) as TextView

        val cuenta = ControlCuenta.text.toString()
        val nombre = Controlnombre.text.toString()
        val apellido = Controlapellido.text.toString()
        val NombreApellido = nombre + " " + apellido

        var intent = Intent(this,MainActivity::class.java)
        intent.putExtra("cuenta", cuenta)
        intent.putExtra("nombre",NombreApellido)
        startActivity(intent)

        Toast.makeText(this,cuenta,Toast.LENGTH_SHORT).show()

    }

    fun ResetColor(){
        for (i in 0 .. tl_clientes!!.childCount){
            val registros = tl_clientes?.getChildAt(i)
            registros?.setBackgroundColor(Color.WHITE)
        }

    }

    fun CargarClientes(){
        tl_clientes?.removeAllViews()
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        val listaBuscar = sp_bus?.selectedItem.toString()
        var query =""
        var bus = txt_nom_bus?.text.toString()

            if (listaBuscar=="Ciudad"){
                query = "select c.cuenta, c.nombre, c.apeellidoP, c.ciudad,c.articulo, a.saldo  from clientes c, abonos a  where c.ciudad  = '$bus' and c.cuenta = a.no_cuenta group by a.no_cuenta "
            } else{
                query ="select c.cuenta, c.nombre, c.apeellidoP, c.ciudad,c.articulo, a.saldo  from clientes c, abonos a  where c.dias and c.cuenta = a.no_cuenta group by a.no_cuenta "
            }
            var fila = BaseDatos.rawQuery(query,null)
            if(fila.moveToFirst()==true){
                fila.moveToFirst()

                do {
                    val registro = LayoutInflater.from(this).inflate(R.layout.item_table_clientes,null,false)
                    val tvc_cuenta =registro.findViewById<View>(R.id.tvc_cuenta) as TextView
                    val tvc_nombre =registro.findViewById<View>(R.id.tvc_nombre) as TextView
                    val tvc_apellido =registro.findViewById<View>(R.id.tvc_apellido) as TextView
                    val tvc_ciudad =registro.findViewById<View>(R.id.tvc_ciudad) as TextView
                    val tvc_articulo =registro.findViewById<View>(R.id.tvc_articulo) as TextView
                    val tvc_saldo =registro.findViewById<View>(R.id.tvc_saldo) as TextView


                    tvc_cuenta.setText(fila.getString(0))
                    tvc_nombre.setText(fila.getString(1))
                    tvc_apellido.setText(fila.getString(2))
                    tvc_ciudad.setText(fila.getString(3))
                    tvc_articulo.setText(fila.getString(4))
                    tvc_saldo.setText(fila.getString(5))

                    tl_clientes?.addView(registro)

                }while (fila.moveToNext())
            } else{

                Toast.makeText(this,"No hay registros",Toast.LENGTH_LONG).show()

            }



    }

}