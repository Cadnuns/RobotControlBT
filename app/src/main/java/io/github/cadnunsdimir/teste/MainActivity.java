package io.github.cadnunsdimir.teste;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fazerConexoesDoLayout_e_Listeners();

        verificarCondicaoDoBluetooth();
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    Button conectar;
    Button desconectar;
    Button receberDados;
    EditText textoRecebido;

    // Represents a remote Bluetooth device.
    private BluetoothDevice dispositivoBluetoohRemoto;

    // Represents the local device Bluetooth adapter.
    private BluetoothAdapter meuBluetoothAdapter = null;

    // A connected or connecting Bluetooth socket.
    private BluetoothSocket bluetoothSocket = null;

    private static final String endereco_MAC_do_Bluetooth_Remoto = "20:14:05:15:32:00";

    public static final int CODIGO_PARA_ATIVACAO_DO_BLUETOOTH = 1;

    // Anyone can create a UUID and use it to identify something with
    // reasonable confidence that the same identifier will never be
    // unintentionally created by anyone to identify something else
    private static final UUID MEU_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // A readable source of bytes.
    private InputStream inputStream = null;



    public void fazerConexoesDoLayout_e_Listeners(){

        conectar =     (Button)findViewById(R.id.conectar);
        desconectar =  (Button)findViewById(R.id.desconectar);
        receberDados =  (Button)findViewById(R.id.enviarDados);

        textoRecebido = (EditText)findViewById(R.id.textoEnviado);

        conectar.   setOnClickListener(new Conectar());
        desconectar.setOnClickListener(new Desconectar());
        receberDados.setOnClickListener(new ReceberDados());

    }

    public void verificarCondicaoDoBluetooth() {

        // Get a handle to the default local Bluetooth adapter.
        meuBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Verifica se o celular tem Bluetooth
        if(meuBluetoothAdapter == null){

            Toast.makeText(getApplicationContext(), "Dispositivo n�o possui adaptador Bluetooth", Toast.LENGTH_LONG).show();

            // Finaliza a aplica��o.
            finish();

        } else {

            // Verifica se o bluetooth est� desligado. Se sim, pede permiss�o para ligar.
            if(!meuBluetoothAdapter.isEnabled()){

                // Activity Action: Show a system activity that allows the user to turn on Bluetooth.

                // This system activity will return once Bluetooth has completed turning ON, or the
                // user has decided not to turn Bluetooth on.

                // Notification of the result of this activity is posted using the
                // #onActivityResult callback. The resultCode will be RESULT_OK if
                // Bluetooth has been turned ON or RESULT_CANCELED if the user has
                // rejected the request or an error has occurred.
                Intent novoIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(novoIntent,  CODIGO_PARA_ATIVACAO_DO_BLUETOOTH);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case  CODIGO_PARA_ATIVACAO_DO_BLUETOOTH:

                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(), "Bluetooth foi ativado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth n�o foi ativado", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    public class Conectar implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // Validate a Bluetooth address, such as "00:43:A8:23:10:F0".
            // (Alphabetic characters must be uppercase to be valid)
            if(BluetoothAdapter.checkBluetoothAddress(endereco_MAC_do_Bluetooth_Remoto)){

                // Get a BluetoothDevice object for the given Bluetooth hardware.
                // Valid Bluetooth hardware addresses must be upper case, in a format
                // such as "00:11:22:33:AA:BB"
                dispositivoBluetoohRemoto = meuBluetoothAdapter.getRemoteDevice(endereco_MAC_do_Bluetooth_Remoto);

            } else{
                Toast.makeText(getApplicationContext(), "Endere�o MAC do dispositivo Bluetooth remoto n�o � v�lido", Toast.LENGTH_SHORT).show();
            }

            try{

                // Create an RFCOMM BluetoothSocket socket ready to start an insecure
                // outgoing connection to this remote device using SDP lookup of UUID.
                // The RFCOMM protocol emulates the serial cable line settings and
                // status of an RS-232 serial port and is used for providing serial data transfer
                bluetoothSocket = dispositivoBluetoohRemoto.createInsecureRfcommSocketToServiceRecord(MEU_UUID);

                // Attempt to connect to a remote device.
                bluetoothSocket.connect();

                Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_SHORT).show();

            } catch(IOException e){

                Log.e("ERRO AO CONECTAR", "O erro foi" + e.getMessage());
                Toast.makeText(getApplicationContext(), "Conex�o n�o foi estabelecida", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public class Desconectar implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if(bluetoothSocket != null) {
                try{
                    // Immediately close this socket, and release all associated resources.
                    bluetoothSocket.close();

                    bluetoothSocket = null;
                    Toast.makeText(getApplicationContext(), "Conex�o encerrada", Toast.LENGTH_SHORT).show();

                } catch(IOException e){

                    Log.e("ERRO AO DESCONECTAR", "O erro foi" + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Erro - A conex�o permanece estabelecida", Toast.LENGTH_SHORT).show();
                }

            } else{
                Toast.makeText(getApplicationContext(), "N�o h� nenhuma conex�o estabelecida a ser desconectada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ReceberDados implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // Verifica se h� conex�o estabelecida com o Bluetooth.
            if(bluetoothSocket != null){

                textoRecebido.setText("");

                try{

                    // Get the input stream associated with this socket.
                    inputStream = bluetoothSocket.getInputStream();

                    byte[] msgBuffer = new byte[1];

                    // Reads bytes from this stream and stores them in the byte array
                    inputStream.read(msgBuffer);

                    textoRecebido.setText(new String(msgBuffer));

                    Toast.makeText(getApplicationContext(), "Mensagem foi recebida", Toast.LENGTH_LONG).show();

                } catch(IOException e){
                    Log.e("ERRO MENSAGEM", "O erro foi" + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Mensagem n�o recebida", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth n�o est� conectado", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
