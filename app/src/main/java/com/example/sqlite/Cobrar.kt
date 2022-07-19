package com.example.sqlite

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
import java.util.*

class Cobrar : AppCompatActivity() {

    var tl_cobrar:TableLayout?= null
    var dp_fecha_cobrar:DatePicker?=null
    var txt_nom_cobrar:EditText?=null
    var txt_fec_cobrar:EditText?=null
    var sp_cobrar:Spinner?=null

    var  ContadorSinc = 0
    var DiaDeSemana:String? = null
    var ciudad_posicion:String? = null  // ciudad en la que se esta cobrando

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobrar)

        tl_cobrar = findViewById(R.id.tl_cobrar)
        dp_fecha_cobrar= findViewById(R.id.dp_fecha_cobrar)
        txt_fec_cobrar= findViewById(R.id.txt_fec_cobrar)
        txt_nom_cobrar= findViewById(R.id.txt_nom_cobrar)
        sp_cobrar= findViewById(R.id.sp_cobrar)

        ciudad_posicion = intent.getStringExtra("ciudad").toString() // reci



        // asignar el valor al editex txt_fecha
        txt_fec_cobrar?.setText(GetFecha())

        // funcion para mostrar el calendario
        val onDateChangedListener =
            dp_fecha_cobrar?.setOnDateChangedListener { dp_fecha_cobrar, anio, mes, dia ->
                txt_fec_cobrar?.setText(GetFecha())
                dp_fecha_cobrar?.visibility = View.GONE


            }

        // codigo spiner_cobrar
        var listCampos = arrayOf("Oluta","Soconusco","Acayucan","Hidalgo","Dehesa","Cuadra","Sayula")
        var adaptador : ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_spinner_item,listCampos)
        sp_cobrar?.adapter=adaptador
        // end



        ObtenerDia()
        var Con = ValidarClientes()
        if (Con == 1){
            //Toast.makeText(this,"ya hya registros", Toast.LENGTH_SHORT).show()

        }else{
            SincronizarClientes()
        }

       //
        if(ciudad_posicion=="null"){}else{sp_cobrar?.setSelection(ciudad_posicion!!.toInt())}
        llenarTablaCobrar() // funcion para llenar toda la tabla principal



        //Toast.makeText(this,sp_cobrar?.selectedItem.toString(), Toast.LENGTH_SHORT).show()
    }

    /////////////////////////////////////menu////////////////////////
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_clientes_ver,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_actualizar -> llenarTabla2()
            R.id.home_clie -> VerCli()
            R.id.Ver_cobrado-> Vercobrado()


        }
        return super.onOptionsItemSelected(item)
    }

    ////////////////////////////////////////// menu ////////




    // funcion para obtener la fecha
    fun GetFecha():String{
        var dia= dp_fecha_cobrar?.dayOfMonth.toString().padStart(2,'0')
        var mes=(dp_fecha_cobrar!!.month+1).toString().padStart(2,'0')
        var anio= dp_fecha_cobrar?.year.toString()
        return anio+"-"+mes+"-"+dia
    }

    // funcion para mostrar el calendario

    fun MostrarCalendario(view: View){
        dp_fecha_cobrar?.visibility=View.VISIBLE
    }



    fun llenarTablaCobrar(){ // funcion para llenar la tbla de clientes por ver

        tl_cobrar?.removeAllViews()
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        var query = ""
        if(ciudad_posicion=="null"){
            query = "select c.cuenta,c.nombre,c.apeellidoP,c.articulo,c.ciudad from clientes c, fechar f where c.cuenta = f.cuenta and f.fecha = date('now','localtime') and estado in ('reprogramado','por_ver','por_ver_quincenal')"
        }else{
            var ciu = sp_cobrar?.selectedItem.toString() // variable para mostrar los clientes de la ciudades especificas
            query = "select c.cuenta,c.nombre,c.apeellidoP,c.articulo,c.ciudad from clientes c, fechar f where c.cuenta = f.cuenta and c.ciudad ='$ciu' and f.fecha = date('now','localtime') and estado in ('reprogramado','por_ver','por_ver_quincenal')"
        }

        val fila = BaseDatos.rawQuery(query,null)
        if(fila.moveToFirst()==true){
            fila.moveToFirst()

            do {
                val registro = LayoutInflater.from(this).inflate(R.layout.item_table_cobrar,null,false)
                val tv_cuenta =registro.findViewById<View>(R.id.tv_cuenta_cobrar) as TextView
                val tv_nombre =registro.findViewById<View>(R.id.tv_nombre_cobrar) as TextView
                val tv_apellido =registro.findViewById<View>(R.id.tv_apel_cobrar) as TextView
                val tv_articulo =registro.findViewById<View>(R.id.tv_art_cobrar) as TextView
                val tv_ciudad =registro.findViewById<View>(R.id.tv_ciu_cobrar) as TextView


                tv_cuenta.setText(fila.getString(0))
                tv_nombre.setText(fila.getString(1))
                tv_apellido.setText(fila.getString(2))
                tv_articulo.setText(fila.getString(3))
                tv_ciudad.setText(fila.getString(4))


               // contadorPagosVacios = 0
                tl_cobrar?.addView(registro)

            }while (fila.moveToNext())
        } else{

            Toast.makeText(this,"No hay abonos de este cliente", Toast.LENGTH_LONG).show()

        }


    }


    fun llenarTabla2(){
        tl_cobrar?.removeAllViews()
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase

        var query = "";
        val listaBuscar = sp_cobrar?.selectedItem.toString()
        var nombre = txt_nom_cobrar?.text.toString()
        var fecha = txt_fec_cobrar?.text.toString()
        if (nombre.isEmpty() == true){
            query = "select c.cuenta,c.nombre,c.apeellidoP,c.articulo,c.ciudad from clientes c, fechar f where c.cuenta = f.cuenta and f.fecha = '$fecha' and ciudad like '%$listaBuscar%' and f.estado in ('reprogramado','por_ver','por_ver_quincenal') "
        }else{
            query = "select c.cuenta,c.nombre,c.apeellidoP,c.articulo,c.ciudad from clientes c, fechar f where c.cuenta = f.cuenta and f.fecha = '$fecha' and nombre like '%$nombre%' and f.estado in ('reprogramado','por_ver','por_ver_quincenal') "
        }



        val fila = BaseDatos.rawQuery(query,null)
        if(fila.moveToFirst()==true){
            fila.moveToFirst()

            do {
                val registro = LayoutInflater.from(this).inflate(R.layout.item_table_cobrar,null,false)
                val tv_cuenta =registro.findViewById<View>(R.id.tv_cuenta_cobrar) as TextView
                val tv_nombre =registro.findViewById<View>(R.id.tv_nombre_cobrar) as TextView
                val tv_apellido =registro.findViewById<View>(R.id.tv_apel_cobrar) as TextView
                val tv_articulo =registro.findViewById<View>(R.id.tv_art_cobrar) as TextView
                val tv_ciudad =registro.findViewById<View>(R.id.tv_ciu_cobrar) as TextView


                tv_cuenta.setText(fila.getString(0))
                tv_nombre.setText(fila.getString(1))
                tv_apellido.setText(fila.getString(2))
                tv_articulo.setText(fila.getString(3))
                tv_ciudad.setText(fila.getString(4))


                // contadorPagosVacios = 0
                tl_cobrar?.addView(registro)

            }while (fila.moveToNext())
        } else{
            //contadorPagosVacios = 1
            Toast.makeText(this,"No hay registros disponibles", Toast.LENGTH_LONG).show()

        }
    }


    fun clickobtenerCodigoCobrar(view: View){
        ResetColor()
        view.setBackgroundColor(Color.GRAY)
        val registro = view as TableRow

        val ControlCuenta = registro.getChildAt(0) as TextView
        val ControlNombre = registro.getChildAt(1) as TextView
        val ControlApe = registro.getChildAt(2) as TextView


        val Cuenta = ControlCuenta.text.toString()
        val Nombre = ControlNombre.text.toString() +" " + ControlApe.text.toString()


        var intent = Intent(this,MainActivity2::class.java)
        intent.putExtra("cuenta", Cuenta)
        intent.putExtra("nombre",Nombre)
        startActivity(intent)



    }

    fun ResetColor(){
        for (i in 0 .. tl_cobrar!!.childCount){
            val registros = tl_cobrar?.getChildAt(i)
            registros?.setBackgroundColor(Color.WHITE)
        }

    }

    fun llenarTodo(view: View){
        tl_cobrar?.removeAllViews()
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        val fila = BaseDatos.rawQuery("select c.cuenta,c.nombre,c.apeellidoP,c.articulo,c.ciudad from clientes c, fechar f where c.cuenta = f.cuenta and f.fecha = date('now','localtime') and estado in ('reprogramado','por_ver','por_ver_quincenal')",null)
        if(fila.moveToFirst()==true){
            fila.moveToFirst()

            do {
                val registro = LayoutInflater.from(this).inflate(R.layout.item_table_cobrar,null,false)
                val tv_cuenta =registro.findViewById<View>(R.id.tv_cuenta_cobrar) as TextView
                val tv_nombre =registro.findViewById<View>(R.id.tv_nombre_cobrar) as TextView
                val tv_apellido =registro.findViewById<View>(R.id.tv_apel_cobrar) as TextView
                val tv_articulo =registro.findViewById<View>(R.id.tv_art_cobrar) as TextView
                val tv_ciudad =registro.findViewById<View>(R.id.tv_ciu_cobrar) as TextView


                tv_cuenta.setText(fila.getString(0))
                tv_nombre.setText(fila.getString(1))
                tv_apellido.setText(fila.getString(2))
                tv_articulo.setText(fila.getString(3))
                tv_ciudad.setText(fila.getString(4))


                // contadorPagosVacios = 0
                tl_cobrar?.addView(registro)

            }while (fila.moveToNext())
        } else{
            //contadorPagosVacios = 1
            Toast.makeText(this,"No hay abonos de este cliente", Toast.LENGTH_LONG).show()

        }
    }

    fun SincronizarClientes(){
        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        val fila = BaseDatos.rawQuery("insert into fechar (cuenta,fecha,estado) select cuenta, date('now','localtime'),'por_ver' from clientes where dias_pago like '%$DiaDeSemana%'",null)
        if (fila.moveToFirst()==true){
            Toast.makeText(this,"sincronizado",Toast.LENGTH_SHORT).show()
        }
    }


    fun ValidarClientes(): Int { // funcion para validar si existen registros en la tabla fechar para el dia que corresponde


        val con=SQlite(this,"promociones",null,1)
        val BaseDatos = con.writableDatabase
        var resultado = 0;
        val fila = BaseDatos.rawQuery("select count(*) from fechar where fecha = date('now','localtime') and estado in ('por_ver','para_la_otra','abonado','maañana')",null)
        if(fila.moveToFirst()== true){

            resultado = fila.getString(0).toInt()
            if (resultado >= 1){ // significa que ya hay registros
                resultado = 1;

            }else{
                resultado = 0; // no existen registros

            }
        }

        return resultado

    }





    fun ObtenerDia(){
        val c = Calendar.getInstance()
        var dia = c.get(Calendar.DAY_OF_WEEK)
        if (dia == Calendar.SUNDAY){
            DiaDeSemana = "Domingo"
        }else if(dia == Calendar.MONDAY){
            DiaDeSemana = "Lunes"
        }else if(dia == Calendar.TUESDAY){
            DiaDeSemana = "Martes"
        }else if(dia == Calendar.WEDNESDAY){
            DiaDeSemana = "Miércoles"
        }else if(dia == 5){
            DiaDeSemana = "Jueves"
        }else if(dia == Calendar.FRIDAY){
            DiaDeSemana = "Viernes"
        }else if(dia == Calendar.SATURDAY){
            DiaDeSemana = "Sábado"
        }


    }


    fun RegresarClien(view: View){
        var intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    fun VerCli(){
        var intent = Intent(this,Clientes::class.java)
        startActivity(intent)
    }

    fun Vercobrado(){
        var intent = Intent(this,tarjetas_abonadas::class.java)
        startActivity(intent)
    }




}