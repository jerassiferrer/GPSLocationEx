package com.mobile.jera.gpslocationex;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final long TIEMPO_MIN = 10 * 1000; // 10 segundos
    private static final long DISTANCIA_MIN = 5; // 5 metros
    private static final String[] A = {"n/d", "preciso", "impreciso"};
    private static final String[] P = {"n/d", "bajo", "medio", "alto"};
    private static final String[] E = {"fuera de servicio",
            "temporalmente no disponible ", "disponible"};
    private LocationManager manejador;
    private String proveedor;
    private TextView salida;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        salida = (TextView) findViewById(R.id.salida);

//Locationmanager muestra los systemas disponibles
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
        log("Proveedores de localización: \n ");
        muestraProveedores();
//Se definen los Criterios para el LocationManager nos devuelva los servicios que coincidan con los criterios definidos
        Criteria criterio = new Criteria();
//costo de bateria
        criterio.setCostAllowed(false);
//Con altitud
        criterio.setAltitudeRequired(true);
//Tipo de precisión  exacta
        criterio.setAccuracy(Criteria.ACCURACY_FINE);
//metodo getbestprovider del location manager devuelve el mejor proveedor correspondientes a los criterios
        proveedor = manejador.getBestProvider(criterio, true);

        log("Mejor proveedor: " + proveedor + "\n");
        log("Comenzamos con la última localización conocida:");
//getlastknownlocation nos devuelve la ultima localizacion que identifico el proveedor
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location localizacion = manejador.getLastKnownLocation(proveedor);
        muestraLocaliz(localizacion);

    }

    // Método RESUME
    @Override
    protected void onResume() {
        super.onResume();
//Vefificar permisos en Android Marshmallow
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

// conseguir que se notifiquen cambios de posición hay que llamar al método requestlocationupdates
        manejador.requestLocationUpdates(proveedor, TIEMPO_MIN, DISTANCIA_MIN, (LocationListener) this);
    }


//  METODO ON PAUSE
    @Override
    protected void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
// el metodo removeupdates indicar que se dejen de hacer las notificaciones
// para que se reporten notificaciones solo cuando la aplicación esté activa
        manejador.removeUpdates((LocationListener) this);
    }

    // Métodos de la interfaz LocationListener
    public void onLocationChanged(Location location) {
        log("Nueva localización: ");
        muestraLocaliz(location);
    }

    public void onProviderDisabled(String proveedor) {
        log("Proveedor deshabilitado: " + proveedor + "\n");
    }

    public void onProviderEnabled(String proveedor) {
        log("Proveedor habilitado: " + proveedor + "\n");
    }

    public void onStatusChanged(String proveedor, int estado, Bundle extras) {
        log("Cambia estado proveedor: " + proveedor + ", estado="
                + E[Math.max(0, estado)] + ", extras=" + extras + "\n");
    }

    // Métodos para mostrar información
    private void log(String cadena) {
        salida.append(cadena + "\n");
    }

    private void muestraLocaliz(Location localizacion) {
        if (localizacion == null)
            log("Localización desconocida\n");
        else
            log(localizacion.toString() + "\n");
    }

    private void muestraProveedores() {
        log("Proveedores de localización: \n ");
        List<String> proveedores = manejador.getAllProviders();
        for (String proveedor : proveedores) {
            muestraProveedor(proveedor);
        }
    }

    private void muestraProveedor(String proveedor) {
        LocationProvider info = manejador.getProvider(proveedor);
        log("LocationProvider[ " + "getName=" + info.getName()
                + ", isProviderEnabled="
                + manejador.isProviderEnabled(proveedor) + ", getAccuracy="
                + A[Math.max(0, info.getAccuracy())] + ", getPowerRequirement="
                + P[Math.max(0, info.getPowerRequirement())]
                + ", hasMonetaryCost=" + info.hasMonetaryCost()
                + ", requiresCell=" + info.requiresCell()
                + ", requiresNetwork=" + info.requiresNetwork()
                + ", requiresSatellite=" + info.requiresSatellite()
                + ", supportsAltitude=" + info.supportsAltitude()
                + ", supportsBearing=" + info.supportsBearing()
                + ", supportsSpeed=" + info.supportsSpeed() + " ]\n");
    }





}
