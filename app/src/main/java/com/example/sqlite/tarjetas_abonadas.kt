package com.example.sqlite

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.*

class tarjetas_abonadas : AppCompatActivity() {
    var tl_tarjetas:TableLayout?=null
    var tv_cuenta_tar:TextView?=null
    var tv_nom_tar:TextView?=null
    var tv_fec_tar:TextView?=null
    var tv_abo_tar:TextView?=null
    var tv_sal_tar:TextView?=null

    var tv_comision:TextView?=null
    var tv_total:TextView?=null
    var txt_gastos:EditText?=null
    var tv_entregar:TextView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjetas_abonadas)

        tv_cuenta_tar=findViewById(R.id.tv_cuenta_tar)
        tv_nom_tar=findViewById(R.id.tv_nom_tarj)
        tv_fec_tar=findViewById(R.id.tv_fec_tarj)
        tv_abo_tar=findViewById(R.id.tv_abo_tarj)
        tv_sal_tar=findViewById(R.id.tv_sal_tarj)
        tl_tarjetas=findViewById(R.id.tl_tarjetas)

        tv_comision=findViewById(R.id.tv_comision)
        tv_total=findViewById(R.id.tv_total)
        txt_gastos= findViewById(R.id.txt_gastos)
        tv_entregar= findViewById(R.id.tv_entregar)


        Cargarcobrado()
        CargarTarjetas()

    }

    /////////////////////////////////////menu////////////////////////
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tarjetas_abonadas,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.corte -> Firmar()
            R.id.posponer->Posponer()
            R.id.eliminar_re->EliminarClientesPagados()
            R.id.eliminar_servidor->EnviarClientesPagados()
           /* R.id.home_clie -> HomeBack()
            R.id.Ver_cobrado-> Vercobrado()*/


        }
        return super.onOptionsItemSelected(item)
    }

    ////////////////////////////////////////// menu ////////


    fun CargarTarjetas(){
        tl_tarjetas?.removeAllViews()
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        var query = "select cast(substr( a.no_cuenta ,4 ) as int) as numero, a.folio, a.no_cuenta,c.nombre, a.fecha, a.abono, a.saldo from  abonos a, clientes c where a.fecha = date('now','localtime') and a.no_cuenta = c.cuenta   order by numero asc"
        val fila = BaseDatos.rawQuery(query,null)
        if(fila.moveToFirst()==true){
            fila.moveToFirst()

            do {
                val registro = LayoutInflater.from(this).inflate(R.layout.item_table_tarjetas,null,false)
                val tv_cuenta =registro.findViewById<View>(R.id.tv_cuenta_tar) as TextView
                val tv_nombre =registro.findViewById<View>(R.id.tv_nom_tarj) as TextView
                val tv_fecha =registro.findViewById<View>(R.id.tv_fec_tarj) as TextView
                val tv_abono =registro.findViewById<View>(R.id.tv_abo_tarj) as TextView
                val tv_saldo =registro.findViewById<View>(R.id.tv_sal_tarj) as TextView


                tv_cuenta.setText(fila.getString(2))
                tv_nombre.setText(fila.getString(3))
                tv_fecha.setText(fila.getString(4))
                tv_abono.setText(fila.getString(5))
                tv_saldo.setText(fila.getString(6))


                // contadorPagosVacios = 0
                tl_tarjetas?.addView(registro)

            }while (fila.moveToNext())
        } else{

            Toast.makeText(this,"No hay abonos", Toast.LENGTH_SHORT).show()

        }
    }

    fun Cargarcobrado(){
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        var query = "select sum(abono) total,  sum(abono) * 0.15 comision  from abonos where fecha = date('now','localtime')"
        var fila= BaseDatos.rawQuery(query,null)
        if (fila.moveToFirst()==true){

            //var Totalcobrado = fila.getString(0)
            //var Comision = fila.getString(1)

            tv_total?.setText(fila.getString(0))
            tv_comision?.setText(fila.getString(1))



        }

    }

    fun clickobtenerCodigoAbono(view: View){
        ResetColorAbono()
        view.setBackgroundColor(Color.GRAY)
        val registro = view as TableRow

        val ControlCuenta = registro.getChildAt(0) as TextView



        val Cuenta = ControlCuenta.text.toString()

        Toast.makeText(this,"el numero de cuenta es $Cuenta",Toast.LENGTH_SHORT).show()



    }

    fun ResetColorAbono(){
        for (i in 0 .. tl_tarjetas!!.childCount){
            val registros = tl_tarjetas?.getChildAt(i)
            registros?.setBackgroundColor(Color.WHITE)
        }

    }

    fun Firmar(){

       // val date = getCurrentDateTime()
      //  val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")


    }

    fun corte(view: View){
       var total= tv_total?.text.toString().toInt()
       var comision = tv_comision?.text.toString().toFloat()
        var gastos = 0
        if(txt_gastos?.text.toString().isEmpty())
        {}else{

            gastos = txt_gastos?.text.toString().toInt()
        }

        var entregar = total - (comision + gastos)
        tv_entregar?.setText(entregar.toString())
        Toast.makeText(this,"Dinero a entregar $entregar",Toast.LENGTH_SHORT).show()


    }


    fun Posponer(){
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        val fila = BaseDatos.rawQuery("select count(*) from fechar where fecha = date('now','localtime') and estado =  'por_ver'", null)
        if(fila.moveToFirst()==true){
            var valor = fila.getString(0).toInt()
            if(valor >0){
                val resultado = BaseDatos.rawQuery("insert into fechar(cuenta,fecha,estado) select cuenta,date('now','localtime','+1 day') fecha, 'reprogramado' from fechar where fecha = date('now','localtime') and estado =  'por_ver'",null)
                if(resultado.moveToFirst()==true){
                }

                val res =BaseDatos.rawQuery("update fechar set estado = 'ver_manana' where fecha = date('now','localtime') and estado = 'por_ver' ", null)
                if(res.moveToFirst()==true){
                }
                Toast.makeText(this,"Clientes pospuestos para mañana",Toast.LENGTH_SHORT).show()
            }else{Toast.makeText(this,"No quedaron clientes por ver",Toast.LENGTH_SHORT).show() }
        }else {Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()}

    }

    fun EliminarClientesPagados(){



        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        val fila = BaseDatos.rawQuery("select count(*) from abonos where saldo = 0 and fecha <> date('now','localtime')",null)
        if(fila.moveToFirst()==true){
            var validar = fila.getString(0).toInt()
            if(validar>0){


               val fila1 = BaseDatos.rawQuery("insert into clientes_pagados (cuenta,nombre,apeellidoP,apellidoM,direccion,colonia,ciudad,articulo) select c.cuenta, c.nombre,c.apeellidoP,c.apellidoM,c.direccion,c.colonia,c.ciudad,c.articulo from clientes c, abonos a where c.cuenta = a.no_cuenta and a.saldo = 0 and a.fecha <> date('now','localtime')", null)
                if(fila1.moveToFirst()){

                }
               // val eliminarcl =  BaseDatos.rawQuery("delete from clientes where cuenta in ( select c.cuenta from clientes c, abonos a where  a.no_cuenta = c.cuenta and a.saldo = 0 and a.fecha <> date('now','localtime') )",null)
                //val eliminarabo = BaseDatos.rawQuery("delete from abonos where no_cuenta in (select no_cuenta from abonos where saldo  = 0 and fecha <> date('now','localtime'))",null)

                BaseDatos.delete("clientes","cuenta in (select c.cuenta from clientes c, abonos a where  a.no_cuenta = c.cuenta and a.saldo = 0 and a.fecha <> date('now','localtime') )",null)
                BaseDatos.delete("abonos","no_cuenta in (select no_cuenta from abonos where saldo  = 0 and fecha <> date('now','localtime'))",null)
                Toast.makeText(this,"Registros eliminados",Toast.LENGTH_SHORT).show()
            }else{Toast.makeText(this,"No hay registro por eliminar",Toast.LENGTH_SHORT).show()}
        }
    }

    fun EnviarClientesPagados(){

        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase

        val fila = BaseDatos.rawQuery("Select cuenta, nombre, apeellidoP,apellidoM,direccion,colonia,ciudad,articulo from clientes_pagados",null)
        if (fila.moveToFirst()== true){
            do {
                val cuenta = fila.getString(0)
                val nombre = fila.getString(1)
                val apellido = fila.getString(2)
                val apellidoM = fila.getString(3)
                val direccion = fila.getString(4)
                val colonia = fila.getString(5)
                val ciudad = fila.getString(6)
                val articulo = fila.getString(7)


                val queue = Volley.newRequestQueue(this)
                val url= "http://192.168.1.72/promociones/includes/prueba.php"
                var resultadoPost = object : StringRequest(Request.Method.POST,url,
                    Response.Listener <String> { response ->
                        var resul = response.toInt()
                       // Toast.makeText(this,"Registros eliminados $resul",Toast.LENGTH_SHORT).show()

                        if(resul == 1){ // si el servidor me regresa un 1 significa que no hubo error en el servidor
                            val regis = BaseDatos.delete("clientes_pagados","cuenta = '$cuenta'",null)
                            if (regis == 1){ // si me devuelve 1 significa que se elimino de la bd local
                                Toast.makeText(this,"Registros eliminados $resul",Toast.LENGTH_SHORT).show()

                            }
                        }else{
                            Toast.makeText(this,"Ocurrio un erorr en el servidor",Toast.LENGTH_SHORT).show()
                        }



                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"Error al insertar $error",Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()
                        parametros.put("cuenta",cuenta)
                        parametros.put("nombre",nombre)
                        parametros.put("apellido",apellido)
                        parametros.put("apellidoM",apellidoM)
                        parametros.put("direccion",direccion)
                        parametros.put("colonia",colonia)
                        parametros.put("ciudad",ciudad)
                        parametros.put("articulo",articulo)




                        return parametros
                    }
                }

                queue.add(resultadoPost)




            } while (fila.moveToNext())
        }




    }

}