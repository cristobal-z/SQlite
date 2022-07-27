package com.example.sqlite

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import org.json.JSONArray
import java.util.*

class MainActivity : AppCompatActivity() {
    var tv_ruta:TextView?=null
    var dp_fecha:DatePicker?=null
    var txt_fecha:EditText?=null
    var txt_cuenta:EditText?=null
    var txt_nombre:EditText?=null
    var txt_apeellidoP:EditText?=null
    var txt_apellidoM:EditText?=null
    var txt_direccion:EditText?=null
    var txt_entre:EditText?=null
    var txt_colonia:EditText?=null
    var txt_pagara:EditText?=null
    var txt_monto:EditText?=null
    var txt_telefono:EditText?=null
    var txt_dias_pago:EditText?=null
    var txt_primer_pago:EditText?=null
    var txt_cantidad:EditText?=null
    var txt_articulo:EditText?=null
    var txt_precio:EditText?=null
    var txt_anticipo:EditText?=null
    var txt_saldo:EditText?=null
    //var txt_ciudad:EditText?=null
    var txt_ref:EditText??=null

    var sp_ciudad: Spinner?=null


    var cuenta_buscar:String? = null  // numero de cuenta


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dp_fecha=findViewById(R.id.dp_fecha)
        tv_ruta=findViewById(R.id.tv_ruta)

        txt_fecha=findViewById(R.id.txt_fecha)
        txt_cuenta=findViewById(R.id.txt_cuenta)
        txt_nombre=findViewById(R.id.txt_nombre)
        txt_apeellidoP=findViewById(R.id.txt_apeellidoP)
        txt_apellidoM=findViewById(R.id.txt_apellidoM)
        txt_direccion=findViewById(R.id.txt_direccion)
        txt_entre=findViewById(R.id.txt_entre)
        txt_colonia=findViewById(R.id.txt_colonia)
        txt_pagara=findViewById(R.id.txt_pagara)
        txt_monto=findViewById(R.id.txt_monto)
        txt_telefono=findViewById(R.id.txt_telefono)
        txt_dias_pago=findViewById(R.id.txt_dias_pago)
        txt_primer_pago=findViewById(R.id.txt_primer_pago)
        txt_cantidad=findViewById(R.id.txt_cantidad)
        txt_articulo=findViewById(R.id.txt_articulo)
        txt_precio=findViewById(R.id.txt_precio)
        txt_anticipo=findViewById(R.id.txt_anticipo)
        txt_saldo=findViewById(R.id.txt_saldo)
        //txt_ciudad=findViewById(R.id.txt_ciudad)
        sp_ciudad= findViewById(R.id.sp_ciudad)
        txt_ref= findViewById(R.id.txt_ref)

        // s agrego esta linea desde la pc
        // codigo spiner_ciudad
        var listCampos = arrayOf("Oluta","Soconusco","Acayucan","Hidalgo","Dehesa","Cuadra","Sayula")
        var adaptador1 : ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_spinner_item,listCampos)
        sp_ciudad?.adapter=adaptador1
        // end

        // prue


        cuenta_buscar = intent.getStringExtra("cuenta").toString() // recibo numero de cuenta



        txt_fecha?.setText(GetFecha()) // asignar el valor al editex txt_fecha



        dp_fecha?.setOnDateChangedListener{  // funcion para mostrar el calendario
            dp_fecha,anio,mes,dia->
            txt_fecha?.setText(GetFecha())
            dp_fecha?.visibility=View.GONE


        }



        ReBus() // funcion para verificar si recibo el numero de cuenta por intent
        CargarQuincenal()




    }





    /////////////////////////////////////menu////////////////////////
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.tabla_principal -> clientes()
            R.id.fec->fech()
            R.id.ver_hoy-> VerHoy()
            R.id.respaldar-> SubirAbonos()
            R.id.descargar-> Descargar()

        }
        return super.onOptionsItemSelected(item)
    }

    ////////////////////////////////////////// menu end  ////////

    fun clientes(){  // funcion llamar activity clientes
        var intent = Intent(this,Clientes::class.java)

        startActivity(intent)

    } /// end

    fun VerHoy(){
        var intent = Intent(this,Cobrar::class.java)

        startActivity(intent)
    }

    // funcion para obtener la fecha
    fun GetFecha():String{
        var dia= dp_fecha?.dayOfMonth.toString().padStart(2,'0')
        var mes=(dp_fecha!!.month+1).toString().padStart(2,'0')
        var anio= dp_fecha?.year.toString()
        return anio+"-"+mes+"-"+dia
    }

    // funcion para mostrar el calendario

    fun MostrarCalendario(view: View){
        dp_fecha?.visibility=View.VISIBLE
    }

    fun Insertar(view: View){

        var cuenta = txt_cuenta?.text.toString()
        cuenta = tv_ruta?.text.toString()+cuenta
        var fecha = txt_fecha?.text.toString()
        //Toast.makeText(this,cuenta,Toast.LENGTH_LONG).show()


        var con = SQlite(this,"promociones",null,1)
        var BaseDatos = con.writableDatabase



        if (fecha.isEmpty() == false && txt_cuenta?.text.toString().isEmpty() == false) {

            val fila = BaseDatos.rawQuery("select count(*) from clientes where cuenta = '$cuenta'",null)


            if (fila.moveToFirst() == true){
                val validar = fila.getString(0).toInt()
                if(validar == 0){

                    //  var cuenta = txt_cuenta?.text.toString()
                    //cuenta = tv_ruta.text.toString()+cuenta
                    var nombre = txt_nombre?.text.toString()
                    var apeellidoP = txt_apeellidoP?.text.toString()
                    var apellidoM = txt_apellidoM?.text.toString()
                    var direccion = txt_direccion?.text.toString()
                    var entre = txt_entre?.text.toString()
                    var colonia = txt_colonia?.text.toString()
                    var pagara = txt_pagara?.text.toString()
                    var monto = txt_monto?.text.toString()
                    var telefono = txt_telefono?.text.toString()
                    var dias_pago = txt_dias_pago?.text.toString()
                    var primer_pago = txt_primer_pago?.text.toString()
                    var cantidad = txt_cantidad?.text.toString()
                    var articulo = txt_articulo?.text.toString()
                    var precio = txt_precio?.text.toString()
                    var anticipo = txt_anticipo?.text.toString()
                    var saldo = txt_saldo?.text.toString()
                    var ciudad = sp_ciudad?.selectedItem.toString()

                    var registro= ContentValues()
                    registro.put("fecha",fecha)
                    registro.put("cuenta",cuenta)
                    registro.put("nombre",nombre)
                    registro.put("apeellidoP",apeellidoP)
                    registro.put("apellidoM",apellidoM)
                    registro.put("direccion",direccion)
                    registro.put("entre",entre)
                    registro.put("colonia",colonia)
                    registro.put("ciudad",ciudad)
                    registro.put("pagara",pagara)
                    registro.put("monto",monto)
                    registro.put("telefono",telefono)
                    registro.put("dias_pago",dias_pago)
                    registro.put("primer_pago",primer_pago)
                    registro.put("cantidad",cantidad)
                    registro.put("articulo",articulo)
                    registro.put("precio",precio)
                    registro.put("anticipo",anticipo)
                    registro.put("saldo",saldo)
                    registro.put("referencias",txt_ref?.text.toString())

                    BaseDatos.insert("clientes",null,registro)


                    validarSubida()
                   // Clear()
                    Toast.makeText(this,"Insertado",Toast.LENGTH_SHORT).show()


                }else{
                    validarSubida()
                    Toast.makeText(this,"Ya existe este numero de cliente $cuenta",Toast.LENGTH_SHORT).show()
                }
            }





        }
    }



    fun Buscar(view: View){

            val con= SQlite(this,"promociones",null,1)
            val BaseDatos = con.writableDatabase

            var cuenta = txt_cuenta?.text.toString()
            cuenta = tv_ruta?.text.toString()+cuenta

            var nombre = txt_nombre?.text.toString()


            if(cuenta.isEmpty() == false ){
                val fila = BaseDatos.rawQuery("select * from clientes where cuenta = '$cuenta'",null)
                if(fila.moveToFirst()==true){


                    var posicion = AsignarCiuBuscar(fila.getString(8)) // metodo para buscar la posicion del spinner

                    txt_fecha?.setText(fila.getString(1))

                    txt_nombre?.setText(fila.getString(2))
                    txt_apeellidoP?.setText(fila.getString(3))
                    txt_apellidoM?.setText(fila.getString(4))
                    txt_direccion?.setText(fila.getString(5))
                    txt_entre?.setText(fila.getString(6))
                    txt_colonia?.setText(fila.getString(7))

                    sp_ciudad?.setSelection(posicion.toInt())
                    //txt_ciudad?.setText(fila.getString(8))

                    txt_pagara?.setText(fila.getString(9))
                    txt_monto?.setText(fila.getString(10))
                    txt_telefono?.setText(fila.getString(11))
                    txt_dias_pago?.setText(fila.getString(12))
                    txt_primer_pago?.setText(fila.getString(13))
                    txt_cantidad?.setText(fila.getString(14))
                    txt_articulo?.setText(fila.getString(15))
                    txt_precio?.setText(fila.getString(16))
                    txt_anticipo?.setText(fila.getString(17))
                    txt_saldo?.setText(fila.getString(18))
                    txt_ref?.setText(fila.getString(19))
                   // BaseDatos.close()
                }else{

                    // consultar al servidor si existe







                    // fin
                    SearchUpload()



                    Toast.makeText(this,"No existe el numero de cuenta: $cuenta",Toast.LENGTH_LONG).show()
                }

            }else{
                txt_cuenta?.setText("")
                Toast.makeText(this,"No hay registro $cuenta",Toast.LENGTH_LONG).show()
            }


        }

    fun Clear(){

            txt_fecha?.setText("")
            txt_cuenta?.setText("")
            txt_nombre?.setText("")
            txt_apeellidoP?.setText("")
            txt_apellidoM?.setText("")
            txt_direccion?.setText("")
            txt_entre?.setText("")
            txt_colonia?.setText("")
            txt_pagara?.setText("")
            txt_monto?.setText("")
            txt_telefono?.setText("")
            txt_dias_pago?.setText("")
            txt_primer_pago?.setText("")
            txt_cantidad?.setText("")
            txt_articulo?.setText("")
            txt_precio?.setText("")
            txt_anticipo?.setText("")
            txt_saldo?.setText("")
            txt_ref?.setText("")
        }

    fun ClearBtn(view: View){
        txt_fecha?.setText("")
        txt_cuenta?.setText("")
        txt_nombre?.setText("")
        txt_apeellidoP?.setText("")
        txt_apellidoM?.setText("")
        txt_direccion?.setText("")
        txt_entre?.setText("")
        txt_colonia?.setText("")
        txt_pagara?.setText("")
        txt_monto?.setText("")
        txt_telefono?.setText("")
        txt_dias_pago?.setText("")
        txt_primer_pago?.setText("")
        txt_cantidad?.setText("")
        txt_articulo?.setText("")
        txt_precio?.setText("")
        txt_anticipo?.setText("")
        txt_saldo?.setText("")
        txt_ref?.setText("")
    }

    fun Pagos(view: View){


        if (txt_saldo?.text.toString().isEmpty()){
            Toast.makeText(this,"Primero seleccione un numero de cuenta",Toast.LENGTH_LONG).show()

        }else{
            var cuenta = txt_cuenta?.text.toString()
            cuenta = tv_ruta?.text.toString()+cuenta
            var nombre = txt_nombre?.text.toString() + " " + txt_apeellidoP?.text.toString()

            var intent = Intent(this,MainActivity2::class.java)
            intent.putExtra("cuenta", cuenta)
            intent.putExtra("nombre",nombre)
            startActivity(intent)
        }

    }

    fun Editar(view: View){
        var cuenta = txt_cuenta?.text.toString()
        cuenta = tv_ruta?.text.toString()+cuenta

        val con = SQlite(this,"promociones",null,1)
        val BaseDatos= con.writableDatabase

        var fecha = txt_fecha?.text.toString()
        var nombre = txt_nombre?.text.toString()
        var apeellidoP = txt_apeellidoP?.text.toString()
        var apellidoM = txt_apellidoM?.text.toString()
        var direccion = txt_direccion?.text.toString()
        var entre = txt_entre?.text.toString()
        var colonia = txt_colonia?.text.toString()
        var ciudad = sp_ciudad?.selectedItem.toString()
        var pagara = txt_pagara?.text.toString()
        var monto = txt_monto?.text.toString()
        var telefono = txt_telefono?.text.toString()
        var dias_pago = txt_dias_pago?.text.toString()
        var primer_pago = txt_primer_pago?.text.toString()
        var cantidad = txt_cantidad?.text.toString()
        var articulo = txt_articulo?.text.toString()
        var precio = txt_precio?.text.toString()
        var anticipo = txt_anticipo?.text.toString()
        var saldo = txt_saldo?.text.toString()

        if (!txt_cuenta?.text.toString().isEmpty()){
            var registro= ContentValues()


            registro.put("fecha",fecha)
            registro.put("cuenta",cuenta)
            registro.put("nombre",nombre)
            registro.put("apeellidoP",apeellidoP)
            registro.put("apellidoM",apellidoM)
            registro.put("direccion",direccion)
            registro.put("entre",entre)
            registro.put("colonia",colonia)
            registro.put("ciudad",ciudad)
            registro.put("pagara",pagara)
            registro.put("monto",monto)
            registro.put("telefono",telefono)
            registro.put("dias_pago",dias_pago)
            registro.put("primer_pago",primer_pago)
            registro.put("cantidad",cantidad)
            registro.put("articulo",articulo)
            registro.put("precio",precio)
            registro.put("anticipo",anticipo)
            registro.put("saldo",saldo)
            registro.put("referencias",txt_ref?.text.toString())

            val cantidad =BaseDatos.update("clientes",registro,"cuenta='$cuenta'",null)
            if(cantidad>0){
                Toast.makeText(this,"Registro actualizado",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"NO se actualizo",Toast.LENGTH_LONG).show()
            }


        }else{
            Toast.makeText(this,"Campo vacio",Toast.LENGTH_LONG).show()
        }


    }


    fun fech(){

        //Toast.makeText(this,"Fechado",Toast.LENGTH_LONG).show()
        var con = SQlite(this,"promociones",null,1)
        var BaseDatos = con.writableDatabase

        var fecha = txt_fecha?.text.toString()
        var cuenta = txt_cuenta?.text.toString()
        cuenta = tv_ruta?.text.toString()+cuenta



        var registro = ContentValues()
        registro.put("cuenta",cuenta)
        registro.put("fecha",fecha)
        registro.put("estado","reprogramado")


        BaseDatos.insert("fechar","estado",registro)
        Toast.makeText(this,"Fechado",Toast.LENGTH_SHORT).show()

    }

    fun ReBus(){
        val con= SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase

        var cuenta = txt_cuenta?.text.toString()
        cuenta = tv_ruta?.text.toString()+cuenta

        if(cuenta_buscar == "null"){

        }else {

            var ruta_cuenta= tv_ruta?.text.toString() + cuenta_buscar.toString()


        val fila = BaseDatos.rawQuery("select * from clientes where cuenta = '$ruta_cuenta'",null)
        if(fila.moveToFirst()==true){


            txt_cuenta?.setText(cuenta_buscar.toString())
            txt_fecha?.setText(fila.getString(1))
            txt_nombre?.setText(fila.getString(2))
            txt_apeellidoP?.setText(fila.getString(3))
            txt_apellidoM?.setText(fila.getString(4))
            txt_direccion?.setText(fila.getString(5))
            txt_entre?.setText(fila.getString(6))
            txt_colonia?.setText(fila.getString(7))


            var posicion = AsignarCiuBuscar(fila.getString(8))
            sp_ciudad?.setSelection(posicion.toInt()) // asignar la posicion al spinner

            txt_pagara?.setText(fila.getString(9))
            txt_monto?.setText(fila.getString(10))
            txt_telefono?.setText(fila.getString(11))
            txt_dias_pago?.setText(fila.getString(12))
            txt_primer_pago?.setText(fila.getString(13))
            txt_cantidad?.setText(fila.getString(14))
            txt_articulo?.setText(fila.getString(15))
            txt_precio?.setText(fila.getString(16))
            txt_anticipo?.setText(fila.getString(17))
            txt_saldo?.setText(fila.getString(18))
            // BaseDatos.close()

        }else{
            Toast.makeText(this,"Error $ruta_cuenta",Toast.LENGTH_SHORT).show()
        }

        }




    }

    fun AsignarCiuBuscar(ciudad: String) : String{
        var posicion = ""

        if (ciudad == "Oluta"){posicion = "0"}
        else if(ciudad == "Soconusco"){posicion = "1"}
        else if(ciudad == "Acayucan"){posicion = "2"}
        else if(ciudad == "Hidalgo"){posicion = "3"}
        else if(ciudad == "Dehesa"){posicion = "4"}
        else if(ciudad == "Cuadra"){posicion = "5"}
        else if(ciudad == "Sayula"){posicion = "6"}

        return posicion
    }

    fun CargarQuincenal(){
        val con= SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase


        var dia= dp_fecha?.dayOfMonth.toString().padStart(2,'0')

        if(dia.toInt()==16 || dia.toInt() == 1){

            var resultado = 0; // validar que no existan registros de clientes quincenal

            val fila0 = BaseDatos.rawQuery("select count(*) from fechar f, clientes c where f.fecha = date('now','localtime') and c.cuenta = f.cuenta and c.pagara like  '%quincenal%'",null)
            if(fila0.moveToFirst()== true){

                resultado = fila0.getString(0).toInt()
                if (resultado >= 1){ // significa que ya hay registros

                   // Toast.makeText(this,"ya hay registros",Toast.LENGTH_LONG).show()

                }else{

                    val fila = BaseDatos.rawQuery("insert into fechar (cuenta,fecha,estado) select  cuenta, date('now','localtime'),'por_ver_quincenal' from clientes where pagara like '%quincenal%'",null)
                    if(fila.moveToFirst()== true){
                        Toast.makeText(this,"Se insertaron los registros",Toast.LENGTH_LONG).show()
                    }

                }
            }

        }

    }


    fun Subir(){


        val url= "http://192.168.1.72/promociones/includes/insertar.php"
        val queue= Volley.newRequestQueue(this)
        var resultadoPost = object : StringRequest(Request.Method.POST,url,
            Response.Listener <String> { response ->
                Toast.makeText(this,"Sincronizado",Toast.LENGTH_SHORT).show()
            }, Response.ErrorListener { error ->
                Toast.makeText(this,"Error $error",Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()
                parametros.put("cuenta",tv_ruta?.text.toString() + txt_cuenta?.text.toString())
                parametros.put("fecha",txt_fecha?.text.toString())
                parametros.put("nombre",txt_nombre?.text.toString())
                parametros.put("apellidoP",txt_apeellidoP?.text.toString())
                parametros.put("apellidoM",txt_apellidoM?.text.toString())
                parametros.put("direccion",txt_direccion?.text.toString())
                parametros.put("entre",txt_entre?.text.toString())
                parametros.put("colonia",txt_colonia?.text.toString())
                parametros.put("ciudad",sp_ciudad?.selectedItem.toString())
                parametros.put("pagara",txt_pagara?.text.toString())
                parametros.put("monto",txt_monto?.text.toString())
                parametros.put("telefono",txt_telefono?.text.toString())
                parametros.put("dias_pago",txt_dias_pago?.text.toString())
                parametros.put("primer_pago",txt_primer_pago?.text.toString())
                parametros.put("cantidad",txt_cantidad?.text.toString())
                parametros.put("articulo",txt_articulo?.text.toString())
                parametros.put("precio",txt_precio?.text.toString())
                parametros.put("anticipo",txt_anticipo?.text.toString())
                parametros.put("saldo",txt_saldo?.text.toString())
                parametros.put("referencias",txt_ref?.text.toString())
                return parametros
            }
        }

        queue.add(resultadoPost)



    }

    fun validarSubida(){

        if (txt_cuenta?.text.toString().isEmpty()){

        }else{

            val cuenta = tv_ruta?.text.toString() + txt_cuenta?.text.toString()
            val queue = Volley.newRequestQueue(this)
            val url= "http://192.168.1.72/promociones/includes/buscar.php?SearchUploadValidar=$cuenta"

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,url,null,
                { response ->
                    var validar = response.getString("SearchUploadValidar").toInt()

                    if(validar == 0){

                       Subir() // significa que no existe
                    }
                    if(validar >= 1){

                        Toast.makeText(this,"Ya existe este usuario",Toast.LENGTH_LONG).show()
                    }

                }, { error ->

                    Toast.makeText(this,"error $error",Toast.LENGTH_LONG).show()
                }
            )
            queue.add(jsonObjectRequest)

            //nota hay que simprlificar el codigo
        }




    }


    fun SubirTodo(){
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        val fila = BaseDatos.rawQuery("select cuenta,fecha, nombre, apeellidoP, apellidoM, direccion, entre, colonia, ciudad, pagara,monto, telefono, dias_pago, primer_pago, cantidad, articulo, precio, anticipo, saldo, referencias from clientes",null)

        if(fila.moveToFirst() == true){
            do {

                // primero metdo para buscar si ya esta sincronizado en mysql
                val cuenta = fila.getString(0)
                val fecha = fila.getString(1)
                val nombre = fila.getString(2)
                val apeellidoP = fila.getString(3)
                val apellidoM = fila.getString(4)
                val direccion = fila.getString(5)
                val entre = fila.getString(6)
                val colonia = fila.getString(7)
                val ciudad = fila.getString(8)
                val pagara = fila.getString(9)
                val monto = fila.getString(10)
                val telefono = fila.getString(11)
                val dias_pago = fila.getString(12)
                val primer_pago = fila.getString(13)
                val cantidad = fila.getString(14)
                val articulo = fila.getString(15)
                val precio = fila.getString(16)
                val anticipo = fila.getString(17)
                val saldo = fila.getString(18)
                val referenci = fila.getString(19)



                val queue = Volley.newRequestQueue(this)
                val url= "http://192.168.1.72/promociones/includes/buscar.php?SearchUploadValidar=$cuenta"


                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET,url,null,
                    { response ->
                        var validar = response.getString("SearchUploadValidar").toInt()

                        if(validar == 0){



                            //metodo para subir la informacin a mysql

                            val url= "http://192.168.1.72/promociones/includes/insertar.php"
                            val queue= Volley.newRequestQueue(this)
                            var resultadoPost = object : StringRequest(Request.Method.POST,url,
                                Response.Listener <String> { response ->


                                }, Response.ErrorListener { error ->
                                    Toast.makeText(this,"Error al insertar $error",Toast.LENGTH_SHORT).show()
                                }) {
                                override fun getParams(): MutableMap<String, String>? {
                                    val parametros = HashMap<String,String>()
                                    parametros.put("cuenta",cuenta)
                                    parametros.put("fecha",fecha)
                                    parametros.put("nombre",nombre)
                                    parametros.put("apellidoP",apeellidoP)
                                    parametros.put("apellidoM",apellidoM)
                                    parametros.put("direccion",direccion)
                                    parametros.put("entre",entre)
                                    parametros.put("colonia",colonia)
                                    parametros.put("ciudad",ciudad)
                                    parametros.put("pagara",pagara)
                                    parametros.put("monto",monto)
                                    parametros.put("telefono",telefono)
                                    parametros.put("dias_pago",dias_pago)
                                    parametros.put("primer_pago",primer_pago)
                                    parametros.put("cantidad",cantidad)
                                    parametros.put("articulo",articulo)
                                    parametros.put("precio",precio)
                                    parametros.put("anticipo",anticipo)
                                    parametros.put("saldo",saldo)
                                    parametros.put("referencias",referenci)
                                    return parametros
                                }
                            }

                            queue.add(resultadoPost)



                            // fin subir


                           // significa que no existe
                        }
                        if(validar >= 1){

                            Toast.makeText(this,"Ya existe este usuario",Toast.LENGTH_SHORT).show()
                        }

                    }, { error ->

                        Toast.makeText(this,"error $error",Toast.LENGTH_LONG).show()
                    }
                )
                queue.add(jsonObjectRequest)

                // fin sincronizar

            }while (fila.moveToNext())

            Toast.makeText(this,"Listo, datos sincronizados!",Toast.LENGTH_LONG).show()

        }else{
            Toast.makeText(this,"No hay nada que mostrar",Toast.LENGTH_LONG).show()
        }

    }


    fun Descargar(){

        val cuenta = tv_ruta?.text.toString() + txt_cuenta?.text.toString()
        val queue = Volley.newRequestQueue(this)
        val url= "http://192.168.1.72/promociones/includes/prueba.php?cuentaClientes=$cuenta"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
                var jsonarray = response.getJSONArray("data")

                for (i in 0 until jsonarray.length()) {
                    val jsonobject = jsonarray.getJSONObject(i)
                    var cuenta = jsonobject.getString("cuenta")
                    var nombre = jsonobject.getString("nombre")
                    Toast.makeText(this, "cuenta: $cuenta y nombre: $nombre", Toast.LENGTH_LONG).show()
                }

            }, { error ->

                Toast.makeText(this,"error $error",Toast.LENGTH_LONG).show()
            }
        )
        queue.add(jsonObjectRequest)
        //nota hay que simprlificar el codigo

    }


    fun SubirAbonos(){
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase

        val fila = BaseDatos.rawQuery("select folio, no_cuenta, fecha, abono, saldo from abonos where inspecion = 'sin sincronizar'",null)
        if(fila.moveToFirst() == true){
            val columnas = fila.count
            Toast.makeText(this," registros $columnas",Toast.LENGTH_SHORT).show()
            do {
                val folio = fila.getString(0)
                val cuenta = fila.getString(1)
                val fecha = fila.getString(2)
                val abono = fila.getString(3)
                val saldo = fila.getString(4)


                // Toast.makeText(this," $validar $folio $cuenta $fecha $abono $saldo",Toast.LENGTH_SHORT).show()

                //metodo para subir la informacin a mysql


                val url= "http://192.168.1.72/promociones/includes/insertarAbonos.php"
                val queue= Volley.newRequestQueue(this)
                var resultadoPost = object : StringRequest(Request.Method.POST,url,
                    Response.Listener <String> { response ->
                        var respuesta = response.toInt()
                        if(respuesta == 0){

                            // actualizo el registro a 'sincronizado'

                            var registro = ContentValues()
                            registro.put("inspecion","sincronizado")

                            var resul = BaseDatos.update("abonos",registro, "folio = $folio",null)
                            if (resul >=1){
                                Toast.makeText(this,"$resul abonos insertados",Toast.LENGTH_SHORT).show()
                            }

                        }
                        if (respuesta==1){
                            Toast.makeText(this,"Ya existe el abono",Toast.LENGTH_SHORT).show()

                        }
                        if (respuesta == 2){
                            Toast.makeText(this,"Error al insertar",Toast.LENGTH_SHORT).show()

                        }

                        //  Toast.makeText(this,"Abonos insertados",Toast.LENGTH_SHORT).show()
                    }, Response.ErrorListener { error ->
                        Toast.makeText(this,"Error, no esta disponible el servidor",Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getParams(): MutableMap<String, String>? {
                        val parametros = HashMap<String,String>()

                        parametros.put("no_cuenta",cuenta)
                        parametros.put("fecha",fecha)
                        parametros.put("abono",abono)
                        parametros.put("saldo",saldo)

                        return parametros
                    }
                }

                queue.add(resultadoPost)





                // fin subir

                // significa que no existe


                //




            }
                while (fila.moveToNext())

            Toast.makeText(this,"Abonos insertados",Toast.LENGTH_SHORT).show()

        }

    }


    fun SearchUpload(){ // buscar e insertar los datos desde el servidor
        val cuenta = tv_ruta?.text.toString() + txt_cuenta?.text.toString()
        val queue = Volley.newRequestQueue(this)
        val url= "http://185.27.134.55/aplicacion/buscar.php?SearchUploadValidar=$cuenta"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
                var validar = response.getString("SearchUploadValidar").toInt()// ide del api rest php

                if(validar == 0){

                    Toast.makeText(this,"No existe en el servidor del internet ni local",Toast.LENGTH_LONG).show()

                   // Subir() // significa que no existe
                }
                if(validar >= 1){



                    Toast.makeText(this,"Existe este usuario en el servidor pero no en local",Toast.LENGTH_LONG).show()
                    SearchUploadSubir()
                }

            }, { error ->

                Toast.makeText(this,"error $error",Toast.LENGTH_LONG).show()
            }
        )
        queue.add(jsonObjectRequest)

        //nota hay que simprlificar el codigo
    }


    fun SearchUploadSubir(){

        val cuenta = tv_ruta?.text.toString() + txt_cuenta?.text.toString()
        val queue = Volley.newRequestQueue(this)
        val url= "http://185.27.134.55/aplicacion/buscar.php?SearchUploadSubir=$cuenta"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,url,null,
            { response ->
               // var validar = response.getString("validar").toInt()

                var fecha = txt_fecha?.text.toString() // response.getString("validar").toInt()

                //  var cuenta = txt_cuenta?.text.toString()

                //cuenta = tv_ruta.text.toString()+cuenta


                var posicion = AsignarCiuBuscar(response.getString("ciudad")) // metodo para buscar la posicion del spinner

                txt_fecha?.setText(response.getString("fecha"))

                txt_nombre?.setText(response.getString("nombre"))
                txt_apeellidoP?.setText(response.getString("apellido"))
                txt_apellidoM?.setText(response.getString("apellidoM"))
                txt_direccion?.setText(response.getString("direccion"))
                txt_entre?.setText(response.getString("entre"))
                txt_colonia?.setText(response.getString("colonia"))

                sp_ciudad?.setSelection(posicion.toInt())
                //txt_ciudad?.setText(fila.getString(8))

                txt_pagara?.setText(response.getString("pagara"))
                txt_monto?.setText(response.getString("monto"))
                txt_telefono?.setText(response.getString("telefono"))
                txt_dias_pago?.setText(response.getString("dias_pago"))
                txt_primer_pago?.setText(response.getString("primer_pago"))
                txt_cantidad?.setText(response.getString("cantidad"))
                txt_articulo?.setText(response.getString("articulo"))
                txt_precio?.setText(response.getString("precio"))
                txt_anticipo?.setText(response.getString("anticipo"))
                txt_saldo?.setText(response.getString("saldo"))
                txt_ref?.setText(response.getString("referencias"))


                //insertar los datos de manera local

                var con = SQlite(this,"promociones",null,1)
                var baseDatos = con.writableDatabase

                var registro= ContentValues()
                registro.put("fecha",response.getString("fecha"))
                registro.put("cuenta",cuenta)
                registro.put("nombre",response.getString("nombre"))
                registro.put("apeellidoP",response.getString("apellido"))
                registro.put("apellidoM",response.getString("apellidoM"))
                registro.put("direccion",response.getString("direccion"))
                registro.put("entre",response.getString("entre"))
                registro.put("colonia",response.getString("colonia"))
                registro.put("ciudad",response.getString("ciudad"))
                registro.put("pagara",response.getString("pagara"))
                registro.put("monto",response.getString("monto"))
                registro.put("telefono",response.getString("telefono"))
                registro.put("dias_pago",response.getString("dias_pago"))
                registro.put("primer_pago",response.getString("primer_pago"))
                registro.put("cantidad",response.getString("cantidad"))
                registro.put("articulo",response.getString("articulo"))
                registro.put("precio",response.getString("precio"))
                registro.put("anticipo",response.getString("anticipo"))
                registro.put("saldo",response.getString("saldo"))
                registro.put("referencias",response.getString("referencias"))

                baseDatos.insert("clientes",null,registro)





                //






            }, { error ->

                Toast.makeText(this,"error $error",Toast.LENGTH_LONG).show()
            }
        )
        queue.add(jsonObjectRequest)





    }










}