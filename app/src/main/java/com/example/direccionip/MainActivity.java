package com.example.direccionip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    EditText ipEditText;
    EditText netmaskEditText;
    TextView netidTextView;
    TextView broadcastTextView;
    TextView hostnumberTextView;
    TextView networkTextView;
    TextView hostTextView;

    int ip[] = new int[4];
    int netmask[] = new int[4];
    int netid[] = new int[4];
    int broadcast[] = new int[4];
    int hostNumber;
    int network[] = new int[4];
    int host[] = new int[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipEditText = findViewById(R.id.ipEditText);
        netmaskEditText = findViewById(R.id.netmaskEditText);
        netidTextView = findViewById(R.id.netidTextView);
        broadcastTextView = findViewById(R.id.broadcastTextView);
        hostnumberTextView = findViewById(R.id.hostNumberTextView);
        networkTextView = findViewById(R.id.networkTextView);
        hostTextView = findViewById(R.id.hostTextView);

        ipEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (validateInputs()){
                    calculate();
                }
            }
        });

        netmaskEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (validateInputs()){
                    calculate();
                }
            }
        });
    }

    private void calculate(){
        //Crea la mascara de red y la mascara de red invertida
        int netmaskInput = Integer.parseInt(netmaskEditText.getText().toString());
        int auxNetmask= netmaskInput;
        int hostmask[] = new int[4];
        for (int i=0; i<4; i++){
            if (auxNetmask - 8 >= 0){
                netmask[i] = 255;
                hostmask[i] = 0;
            }else {
                if (auxNetmask < 0){
                    auxNetmask = 0;
                }
                netmask[i] = (int)(Math.pow(2,8)-Math.pow(2,8-auxNetmask));
                hostmask[i] = (int)(Math.pow(2,8-auxNetmask)-1);
            }
            auxNetmask -= 8;
        }

        //Calculo de netID
        for (int i=0; i<4; i++) {
            netid[i] = ip[i] & netmask[i];
        }
        netidTextView.setText(netid[0]+"."+netid[1]+"."+netid[2]+"."+netid[3]);

        //Calculo de broadcast
        for (int i=0; i<4; i++) {
            broadcast[i] = ip[i] | hostmask[i];
            Log.d("TEST",hostmask[i]+"");
        }
        broadcastTextView.setText(broadcast[0]+"."+broadcast[1]+"."+broadcast[2]+"."+broadcast[3]);

        //Calculo de numero de hosts
        hostNumber = (int)Math.pow(2,32-netmaskInput)-2;
        hostnumberTextView.setText(hostNumber+"");

        //Calculo parte de red
        for (int i=0; i<4; i++){
            network[i] = ip[i] & netmask[i];
        }
        networkTextView.setText(network[0]+"."+network[1]+"."+network[2]+"."+network[3]);

        //Calculo parte de host
        for (int i=0; i<4; i++){
            host[i] = ip[i] & hostmask[i];
        }
        hostTextView.setText(host[0]+"."+host[1]+"."+host[2]+"."+host[3]);
    }

    private boolean validateInputs(){
        String ipText = ipEditText.getText().toString();
        String netmaskText = netmaskEditText.getText().toString();

        //Verifica si el campo de ip esta vacio
        if (ipText.equals("")){
            ipEditText.setError(getString(R.string.blank_error));
            return false;
        }else{
            ipEditText.setError(null);
        }

        //Verifica si la ip tiene el formato correcto
        String ipRegex = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
        if (!ipText.matches(ipRegex)){
            ipEditText.setError(getString(R.string.format_error));
            return false;
        }else{
            ipEditText.setError(null);
        }

        //Convierte la ip de string a int
        String ipInput[] = ipEditText.getText().toString().split("\\.");
        for (int i=0; i<4; i++){
            ip[i] = Integer.parseInt(ipInput[i]);
        }

        //Verifica si algun octeto de la ip esta fuera de rango
        for (int i=0; i<4; i++){
           if (ip[i] < 0 || ip[i] > 255){
               ipEditText.setError(getString(R.string.ip_range_error));
               return false;
           }else{
               ipEditText.setError(null);
           }
        }

        //Verifica si el campo de mascara esta vacio
        if (netmaskText.equals("")){
            netmaskEditText.setError(getString(R.string.blank_error));
            return false;
        }else{
            networkTextView.setError(null);
        }

        //Verifica si la mascara esta fuera de rango
        int netmask = Integer.parseInt(netmaskEditText.getText().toString());
        if (netmask < 1 || netmask > 31){
            netmaskEditText.setError(getString(R.string.netmask_range_error));
            return false;
        }else{
            netmaskEditText.setError(null);
        }

        return true;
    }
}
