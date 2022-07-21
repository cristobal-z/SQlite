package com.example.sqlite

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.contentValuesOf
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.*
import kotlin.collections.HashMap

class MainActivity2 : AppCompatActivity() {

    var tv_nom:TextView?=null
    var txt_fecha_abo:EditText?=null
    var dp_fecha_abo: DatePicker?=null
   // var tv_cuenta:TextView?=null
    var txt_abo_pago:EditText?=null
    var txt_saldo_abo: EditText?= null


    var tlabonos:TableLayout?=null

    var cuenta:String? = null  // numero de cuenta
    var NombreClie:String?=null


    var contadorPagosVacios = 0

    var DiaDeSemana:String? = null
    var folioCuentaAbono:String? = null




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        tv_nom=findViewById(R.id.tv_nom)
        txt_fecha_abo=findViewById(R.id.txt_fecha_abo)
        dp_fecha_abo=findViewById(R.id.dp_fecha_abo)
        txt_abo_pago=findViewById(R.id.txt_abo_pago)
        txt_saldo_abo = findViewById(R.id.txt_saldo_abo)


        tlabonos=findViewById(R.id.tlabonos) // tabla

        //tv_cuenta=findViewById(R.id.tv_cuenta)

        cuenta = intent.getStringExtra("cuenta").toString()
        NombreClie = intent.getStringExtra("nombre").toString()



       // tv_cuenta?.setText(cuenta)
        tv_nom?.setText(NombreClie)

        // asignar el valor al editex txt_fecha
        txt_fecha_abo?.setText(GetFecha())


        // funcion para mostrar el calendario
        dp_fecha_abo?.setOnDateChangedListener{
                dp_fecha,anio,mes,dia->
            txt_fecha_abo?.setText(GetFecha())
            dp_fecha_abo?.visibility= View.GONE


        }

         ObtenerDia()

         LlenarTabla()


    }

    //////////////

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_abonos,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // items de la barra superior
        when(item.itemId){
            R.id.menu_editar -> Editarsaldo()
            R.id.next-> Next()
            R.id.menu_ver -> VerclienteInf()
            R.id.Buscar-> Buscar()
            R.id.menu_siguiente -> VerManana()
            R.id.fechar_abo-> Fechar()


        }
        return super.onOptionsItemSelected(item)
    }

    //////////////


    fun ObtenerDia(){
        val c = Calendar.getInstance()
        var dia = c.get(Calendar.DAY_OF_WEEK)

          if (dia == Calendar.SUNDAY){
              DiaDeSemana = "Domingo"
             // Toast.makeText(this, "dssd  $dia", Toast.LENGTH_LONG).show()
          }else if(dia == Calendar.MONDAY){
              DiaDeSemana = "Lunes"
          }else if(dia == Calendar.TUESDAY){
              DiaDeSemana = "Martes"
          }else if(dia == Calendar.WEDNESDAY){
              DiaDeSemana = "Miercoles"
          }else if(dia == 5){
              DiaDeSemana = "Jueves"
            //  Toast.makeText(this, "Hoy es jueves", Toast.LENGTH_LONG).show()
          }else if(dia == Calendar.FRIDAY){
              DiaDeSemana = "Viernes"
          }else if(dia == Calendar.SATURDAY){
              DiaDeSemana = "Sabado"
          }


    }


    fun GetFecha():String{
        var dia= dp_fecha_abo?.dayOfMonth.toString().padStart(2,'0')
        var mes=(dp_fecha_abo!!.month+1).toString().padStart(2,'0')
        var anio= dp_fecha_abo?.year.toString()
        return anio+"-"+mes+"-"+dia
    }

    fun MostrarCalendario(view: View){
        dp_fecha_abo?.visibility=View.VISIBLE
    }



    fun Abonar(view: View){
        var con = SQlite(this,"promociones",null,1)
        var BaseDatos = con.writableDatabase

        var no_cuenta = cuenta.toString()
        var fecha = txt_fecha_abo?.text.toString()
        var abono = txt_abo_pago?.text.toString()
        var saldo = txt_saldo_abo?.text.toString()


        if(!fecha.isEmpty() && !abono.isEmpty() && saldo.isEmpty()) {

            var AbonoEntero = abono.toInt()
            var sal = 0


            if (contadorPagosVacios==0){
                sal = BuscarSaldo(AbonoEntero)
            }else{
                sal = ObtenerSaldoP(AbonoEntero)
            }

            if (sal== -2){ // valida si ya esta pagada la cuenta

                Toast.makeText(this,"Esta cuenta esta en 0",Toast.LENGTH_LONG).show()
            }else{
                // sino esta pagada continua

                if(sal == -1){ // pregunta  si es menor a 0 significa que el abono es mayor que el saldp

                    Toast.makeText(this,"El abono no puede ser mayor al saldo actual",Toast.LENGTH_LONG).show()

                }else {


                    var registro = ContentValues()

                    registro.put("no_cuenta",no_cuenta)
                    registro.put("fecha",fecha)
                    registro.put("abono",abono)
                    registro.put("saldo",sal)

                    // subir al servidor
                    // antes validar si tiene abonos pendientes por sincronizar

                    val url= "http://192.168.1.72/promociones/includes/insertarAbonos.php"
                    val queue= Volley.newRequestQueue(this)
                    var resultadoPost = object : StringRequest(Request.Method.POST,url,
                        Response.Listener <String> { response ->
                            var respuesta = response.toInt()
                            if(respuesta == 0){ // si el 0 significa que si hay conexion al servidor pero no se guardo en el servidor
                                registro.put("inspecion","sin sincronizar") // se agrega un nuevo parametro segun el estado del servidor

                                BaseDatos.insert("abonos","folio",registro)
                                Toast.makeText(this,"Insertado de manera local",Toast.LENGTH_LONG).show()
                                BuscarFechado()

                                LlenarTabla()

                            }else if(respuesta == 1){ // se inserto en el servidor de manera exitosa

                                registro.put("inspecion","sincronizado") // se agrega un nuevo parametro segun el estado del servidor

                                BaseDatos.insert("abonos","folio",registro)
                                Toast.makeText(this,"Insertado $sal",Toast.LENGTH_LONG).show()
                                BuscarFechado()

                                LlenarTabla()
                            }
                            //  Toast.makeText(this,"Abonos insertados",Toast.LENGTH_SHORT).show()
                        }, Response.ErrorListener { error ->

                            // significa que no hay conexion con el servidor y se guardara de manera local
                            registro.put("inspecion","sin sincronizar") // se agrega un nuevo parametro segun el estado del servidor

                            BaseDatos.insert("abonos","folio",registro)
                            Toast.makeText(this,"Insertado de manera local",Toast.LENGTH_LONG).show()
                            BuscarFechado()

                            LlenarTabla()

                            Toast.makeText(this,"Error al insertar $error",Toast.LENGTH_SHORT).show()
                        }) {
                        override fun getParams(): MutableMap<String, String>? {
                            val parametros = java.util.HashMap<String, String>()

                            parametros.put("no_cuenta",no_cuenta)
                            parametros.put("fecha",fecha)
                            parametros.put("abono",abono)
                            parametros.put("saldo",sal.toString())


                            return parametros
                        }
                    }

                    queue.add(resultadoPost)

                }
            }


        }
        else if(!fecha.isEmpty() && !abono.isEmpty() && !saldo.isEmpty()){

            var registro = ContentValues()

            registro.put("no_cuenta",no_cuenta)
            registro.put("fecha",fecha)
            registro.put("abono",abono)
            registro.put("saldo",saldo)

            BaseDatos.insert("abonos","folio",registro)
            Toast.makeText(this,"Insertado",Toast.LENGTH_LONG).show()
            BuscarFechado()
            LlenarTabla()
        }

    }

    fun Fechar(){
        var con = SQlite(this,"promociones",null,1)
        var BaseDatos = con.writableDatabase
        var fecha = txt_fecha_abo?.text.toString()

        val fila0= BaseDatos.rawQuery("select count(*) from fechar where cuenta = '$cuenta' and fecha = '$fecha' order by id asc limit 1",null)
        if(fila0.moveToFirst()== true){
            var resultado = fila0.getString(0).toInt()
            if(resultado == 0){
                var registro = ContentValues()
                registro.put("cuenta",cuenta)
                registro.put("fecha",fecha)
                registro.put("estado","reprogramado")


                BaseDatos.insert("fechar","id",registro)
                //Toast.makeText(this,"Fechado",Toast.LENGTH_SHORT).show()


                var datos2 = ContentValues() // actualizar
                datos2.put("estado","fechado")
                BaseDatos.update("fechar",datos2,"cuenta = '$cuenta' and fecha = date('now','localtime')",null)
               // Toast.makeText(this,"actualidado registro ",Toast.LENGTH_SHORT).show()

                val fila= BaseDatos.rawQuery("select ciudad from clientes where cuenta = '$cuenta'",null)
                if(fila.moveToFirst()== true){
                    var ciudad = fila.getString(0)

                    var insta = MainActivity() // instancia de la clase main donde tengo la funcion donde busco la posicion del spinner para cada ciudad
                    var posicion = insta.AsignarCiuBuscar(ciudad) // ejecuto la funcion y la almaceno en una varible


                    var intent = Intent(this,Cobrar::class.java)
                    intent.putExtra("ciudad",posicion) // mando el inten con la posicion al activity cobrar
                    Toast.makeText(this,"reprogramado para $fecha ",Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }




 }else{
     Toast.makeText(this,"error ya tiene fechas",Toast.LENGTH_SHORT).show()
 }


}else if(fila0.moveToFirst()== false){
 Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
}


}

fun LlenarTabla(){

tlabonos?.removeAllViews()
val con=SQlite(this,"promociones",null,1)
val BaseDatos = con.writableDatabase
val fila = BaseDatos.rawQuery("select folio,fecha,abono,saldo from abonos where no_cuenta = '$cuenta'",null)
if(fila.moveToFirst()==true){
 fila.moveToFirst()

 do {
     val registro =LayoutInflater.from(this).inflate(R.layout.item_table_abonos,null,false)
     val tv_cuenta_abo =registro.findViewById<View>(R.id.tv_cuenta_abo) as TextView
     val tv_fecha_abo =registro.findViewById<View>(R.id.tv_fecha_abo) as TextView
     val tv_abono_abo =registro.findViewById<View>(R.id.tv_abono_abo) as TextView
     val tv_saldo_abo =registro.findViewById<View>(R.id.tv_saldo_abo) as TextView

     tv_cuenta_abo.setText(fila.getString(0))
     tv_fecha_abo.setText(fila.getString(1))
     tv_abono_abo.setText(fila.getString(2))
     tv_saldo_abo.setText(fila.getString(3))

     contadorPagosVacios = 0
     tlabonos?.addView(registro)

 }while (fila.moveToNext())
} else{

 contadorPagosVacios = 1
    //Toast.makeText(this,"No hay abonos de este cliente",Toast.LENGTH_LONG).show()

    SubirAbonos()

}

}


fun BuscarSaldo(abono:Int): Int { // funcion para traer el el saldo del cliente si este ya tiene abonos

val con= SQlite(this,"promociones",null,1)
val BaseDatos = con.writableDatabase

val fila = BaseDatos.rawQuery("select saldo from abonos where no_cuenta = '$cuenta'order by saldo asc limit 1",null)
var nuevoSaldo = 0
 if (fila.moveToFirst()==true){



     nuevoSaldo = fila.getString(0).toInt()
     if(nuevoSaldo==0){
         nuevoSaldo = -2
     }else{
         if(abono>nuevoSaldo){
             nuevoSaldo= -1
         }else{
             nuevoSaldo -= abono
             if(nuevoSaldo== 0){

             }
         }
     }



 }else {Toast.makeText(this,"Error al obtener el ultimo pago $fila",Toast.LENGTH_SHORT).show()}

 return nuevoSaldo

}
fun ObtenerSaldoP(abono: Int):Int{ // funcion para traer el saldo del cliete cuando no tiene abonos

val con= SQlite(this,"promociones",null,1)
val BaseDatos = con.writableDatabase

val fila = BaseDatos.rawQuery("select saldo from clientes where cuenta = '$cuenta'",null)
var nuevoSaldo = 0
if (fila.moveToFirst()==true){
 nuevoSaldo = fila.getString(0).toInt()
 if(abono>nuevoSaldo){
     nuevoSaldo = -1
 }else{
     nuevoSaldo -= abono
 }

}

return  nuevoSaldo
}

fun clickobtenerCodigoAbonos(view: View){ // funcion para actualizar los abonos de la tabla
ResetColor()
view.setBackgroundColor(Color.GRAY)
val registro = view as TableRow

val ControlFolio = registro.getChildAt(0) as TextView
val ControlFecha = registro.getChildAt(1) as TextView
val ControlAbono = registro.getChildAt(2) as TextView
val ControlSaldo = registro.getChildAt(3) as TextView

val folio = ControlFolio.text.toString()
val fecha = ControlFecha.text.toString()
val abono = ControlAbono.text.toString()
val saldo = ControlSaldo.text.toString()

txt_fecha_abo?.setText(fecha)
txt_abo_pago?.setText(abono)
txt_saldo_abo?.setText(saldo)

folioCuentaAbono = folio
Toast.makeText(this,folioCuentaAbono,Toast.LENGTH_SHORT).show()


}

fun ResetColor(){
for (i in 0 .. tlabonos!!.childCount){
 val registros = tlabonos?.getChildAt(i)
 registros?.setBackgroundColor(Color.WHITE)
}

}

fun Editarsaldo(){

val con = SQlite(this,"promociones",null,1)
val BaseDatos= con.writableDatabase

var fecha = txt_fecha_abo?.text.toString()
var abono = txt_abo_pago?.text.toString()
var saldo = txt_saldo_abo?.text.toString()

if (!abono.isEmpty() && !saldo.isEmpty()){
 var registros = ContentValues()

 registros.put("fecha",fecha)
 registros.put("abono",abono)
 registros.put("saldo",saldo)

 val cantidad = BaseDatos.update("abonos",registros,"folio= $folioCuentaAbono",null)
 if (cantidad > 0){
     LlenarTabla()
     Toast.makeText(this,"Registro actualizado",Toast.LENGTH_LONG).show()
 }else{
     Toast.makeText(this,"NO se actualizo",Toast.LENGTH_LONG).show()
 }
}


}

fun BuscarFechado(){ // funcion para buscar en la tabla fechados si tiene una cita

val con = SQlite(this,"promociones",null,1)
val BaseDatos= con.writableDatabase
val fila=  BaseDatos.rawQuery("select count(*) from fechar where cuenta = '$cuenta' and fecha = date('now','localtime') and estado in('por_ver','reprogramado')",null)
if(fila.moveToFirst()==true){
var resultado = fila.getString(0).toInt()
 if (resultado >0){
     var registro= ContentValues()
     registro.put("estado","abonado")
     BaseDatos.update("fechar",registro,"fecha = date('now','localtime') and cuenta = '$cuenta' and estado in ('por_ver','reprogramado')", null)
 }else{

 }
}
}

fun Regresar(view: View){
var intent = Intent(this,Cobrar::class.java)

startActivity(intent)
}

fun Next(){ // funcion para actualizar el estado de la tabla fechar a "para la otra"
val con = SQlite(this,"promociones",null,1)
val BaseDatos= con.writableDatabase
var registro= ContentValues()
registro.put("estado","para_la_otra")

val cantidad = BaseDatos.update("fechar",registro,"fecha = date('now','localtime') and cuenta = '$cuenta' and estado in ('por_ver','reprogramado')",null)
if(cantidad ==0){
 Toast.makeText(this,"Intente de nuevo, ocurrio un error",Toast.LENGTH_SHORT).show()
}

var ciudad ="Oluta" // nombre de la ciudad en la que se esta cobrando
val fila=  BaseDatos.rawQuery("select ciudad from clientes where cuenta = '$cuenta'",null)
if(fila.moveToFirst() == true){
ciudad = fila.getString(0)
}

var intent = Intent(this,Cobrar::class.java) // regresar al activity cobrar

var insta = MainActivity() // instancia de la clase main donde tengo la funcion donde busco la posicion del spinner para cada ciudad
var posicion = insta.AsignarCiuBuscar(ciudad) // ejecuto la funcion y la almaceno en una varible
intent.putExtra("ciudad",posicion) // mando el inten con la posicion al activity cobrar
startActivity(intent)
}


fun VerclienteInf(){

var palabra = cuenta?.split("R1-")!!.toTypedArray()
var palabra1 = palabra[1]

var intent = Intent(this,MainActivity::class.java)
intent.putExtra("cuenta", palabra1)
startActivity(intent)

//Toast.makeText(this,"14",Toast.LENGTH_SHORT).show()
}

fun Buscar(){
var intent = Intent(this,Clientes::class.java)
startActivity(intent)
}

fun VerManana(){
val con = SQlite(this,"promociones",null,1)
val BaseDatos= con.writableDatabase

val fila= BaseDatos.rawQuery("select id from fechar where cuenta = '$cuenta' and fecha = date('now','localtime','+1 day') order by id asc limit 1",null)
if(fila.moveToFirst() == true){
 Toast.makeText(this,"Este cliente ya esta reprogramado",Toast.LENGTH_SHORT).show()

}else if(fila.moveToFirst()== false){
// val con = SQlite(this,"promociones",null,1)
 //val BaseDatos= con.writableDatabase
 val fila= BaseDatos.rawQuery("select ciudad,date('now','localtime','+1 day') from clientes where cuenta ='$cuenta'",null)
 if (fila.moveToFirst()== true){

     var ciudad = fila.getString(0) // ciudad del cliente
     var fecha = fila.getString(1) // fecha del dia siguiente
     var datos= ContentValues()
     datos.put("cuenta",cuenta)
     datos.put("fecha",fecha)
     datos.put("estado","reprogramado")


     BaseDatos.insert("fechar","id",datos) // se INSERTA en la tabla fechar para el dia siguiente

     var datos2 = ContentValues()
     datos2.put("estado","ver_manana")
     BaseDatos.update("fechar",datos2,"cuenta = '$cuenta' and fecha = date('now','localtime')",null)

     var insta = MainActivity() // instancia de la clase main donde tengo la funcion donde busco la posicion del spinner para cada ciudad
     var posicion = insta.AsignarCiuBuscar(ciudad) // ejecuto la funcion y la almaceno en una varible

     var intent = Intent(this,Cobrar::class.java)
     intent.putExtra("ciudad",posicion) // mando el inten con la posicion al activity cobrar
     Toast.makeText(this,"reprogramado para mañana ",Toast.LENGTH_SHORT).show()
     startActivity(intent)


 }

}
}

     fun SubirAbonos(){ // metodo para descargar los abonos del seridor y guardarlos en sqlite


         // validar si existe en el servidor

        // val cuenta = tv_ruta?.text.toString() + txt_cuenta?.text.toString()
         val queue = Volley.newRequestQueue(this)
         val url= "http://192.168.1.72/promociones/includes/prueba.php?ValidarAbonos=$cuenta"

         val jsonObjectRequest = JsonObjectRequest(
             Request.Method.GET,url,null,
             { response ->
                 var validar = response.getString("ValidarAbonos").toInt()

                 if(validar == 0){

                     Toast.makeText(this,"No hay abonos en el servidor y local",Toast.LENGTH_LONG).show()
                 }
                 if(validar >= 1){




                     // descargar los abonos

                     //val cuenta = tv_ruta?.text.toString() + txt_cuenta?.text.toString()
                     val queue = Volley.newRequestQueue(this)
                     val url= "http://192.168.1.72/promociones/includes/prueba.php?Abonos=$cuenta"
                     val jsonObjectRequest = JsonObjectRequest(
                         Request.Method.GET,url,null,
                         { response ->
                             var jsonarray = response.getJSONArray("data") // creo el JsonArray
                             var contador = 0

                             for (i in 0 until jsonarray.length()) {
                                 val jsonobject = jsonarray.getJSONObject(i) //se asigna objeto json en una constante


                                 val con = SQlite(this,"promociones",null,1)
                                 val BaseDatos= con.writableDatabase
                                 val valores = ContentValues()
                                 valores.put("no_cuenta",jsonobject.getString("cuenta"))//obtengo los valores del JsonArray
                                 valores.put("fecha",jsonobject.getString("fecha")) //obtengo los valores del JsonArray
                                 valores.put("abono",jsonobject.getString("abono")) //obtengo los valores del JsonArray
                                 valores.put("saldo",jsonobject.getString("saldo")) //obtengo los valores del JsonArray
                                 val cantidad =  BaseDatos.insert("abonos","folio",valores) // insertar los registros
                                 if(cantidad>0){ // si es 1 significa que se insertó, o hubo un error
                                     contador ++
                                 }
                             }
                             if(contador == jsonarray.length()){ // si el numero de contador es == a la longitud del jsonArray sinigfica que se insertaron todos
                                 Toast.makeText(this, "Abonos sincronizados desde el servidor", Toast.LENGTH_LONG).show()
                                 LlenarTabla()
                             }else{
                                 Toast.makeText(this, "Error, algunos abonos no fueron insertados", Toast.LENGTH_LONG).show()
                             }
                         }, { error ->
                             Toast.makeText(this,"error $error",Toast.LENGTH_LONG).show()
                         }
                     )
                     queue.add(jsonObjectRequest)
                     //nota hay que simprlificar el codigo

                     // fin descargar abonos

                 }

             }, { error ->

                 Toast.makeText(this,"error $error",Toast.LENGTH_LONG).show()
             }
         )
         queue.add(jsonObjectRequest)

         // fin





 }


    fun ValidarAbonos(){




    }








}