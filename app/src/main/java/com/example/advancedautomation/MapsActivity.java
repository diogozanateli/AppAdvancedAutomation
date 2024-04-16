package com.example.advancedautomation;

import android.content.SharedPreferences;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.mathlibrary.Utils;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference(); //Referência de conexão com o Firebase
    private final Object lock = new Object(); //Objeto lock
    private GoogleMap mMap;
    private Marker marker;
    private Regiao lastRegion;
    private RestrictedRegiao restrictedRegiao;
    private boolean lastRegionIsSubRegion = false; // Flag para alternar entre SubRegion e RestrictedRegion
    private boolean lastRegionisRegion = false;
    private TextView latitudeLabel, longitudeLabel;
    private Button addButton, addToDatabaseButton;
    private BlockingQueue<String> regionQueue = new LinkedBlockingQueue<>();
    private JsonGenerate jsonGenerate = new JsonGenerate();
    private Semaphore semaphore = new Semaphore(1);
    private double selectedLatitude;
    private double selectedLongitude;
    private double lastAddLatitudeQueue = 0.0;
    private double lastAddLongitudeQueue = 0.0;
    private double lastAddLatitudeBanco = 0.0;
    private double lastAddLongitudeBanco = 0.0;
    private double raioQueue;
    private double raioBanco;
    public void MapsActivity(){};
    private int countRegiao = 0;

    //Criar a tela visual do aplicativo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); //Carrega o arquivo de layout

        //Atribuição dos componentes do layout
        latitudeLabel = findViewById(R.id.latitude_label);
        longitudeLabel = findViewById(R.id.longitude_label);
        addButton = findViewById(R.id.add_button);
        addToDatabaseButton = findViewById(R.id.add_to_database_button);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Armazenamento da variável countRegião
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int savedCount = sharedPreferences.getInt("countRegiao", 0);
        countRegiao = savedCount;

    }

    //Carregamento visual da API do Google Maps
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setAllGesturesEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // point contém a latitude e a longitude do ponto clicado
                selectedLatitude = point.latitude;
                selectedLongitude = point.longitude;

                // Atualiza o marcador existente ou adiciona um novo se não houver nenhum
                if (marker != null) {
                    marker.setPosition(point);
                } else {
                    marker = mMap.addMarker(new MarkerOptions().position(point).title("Ponto Selecionado"));
                }

                // Atualiza as labels com os valores de latitude e longitude
                latitudeLabel.setText("Latitude: " + String.valueOf(selectedLatitude));
                longitudeLabel.setText("Longitude: " + String.valueOf(selectedLongitude));

                Log.d("Location", "Latitude: " + selectedLatitude + ", Longitude: " + selectedLongitude);
            }
        });

        //Comando de clique do botão "Adicionar Região"
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRegion();
            }
        });

        //Comando de clique do botão "Adicionar ao Firebase"
        addToDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRegionFirebase();
            }
        });

    }

    private void addRegion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    raioQueue = Regiao.calculateDistance(lastAddLatitudeQueue, lastAddLongitudeQueue, selectedLatitude, selectedLongitude);
                    Log.d("RAIO CALCULADO", String.valueOf(raioQueue));

                    synchronized (lock) {
                        if (!lastRegionisRegion && selectedLatitude != lastAddLatitudeQueue && selectedLongitude != lastAddLongitudeQueue) {
                            // Inserção de Region
                            countRegiao++;
                            Regiao newRegion = new Regiao("regiao" + countRegiao, selectedLatitude, selectedLongitude, getUserCode());
                            JSONObject regiaoJson = jsonGenerate.generateJson(newRegion);
                            String encryptedRegionJson = Criptography.encrypt(regiaoJson.toString());

                            regionQueue.add(encryptedRegionJson);

                            lastAddLatitudeQueue = newRegion.getLatitude();
                            lastAddLongitudeQueue = newRegion.getLongitude();
                            lastRegionIsSubRegion = false;
                            lastRegionisRegion = true;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MapsActivity.this, "Região adicionada à Fila!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (!lastRegionIsSubRegion && raioQueue >= 5.0 && raioQueue <= 30.0) {
                            // Inserção de SubRegion
                            SubRegiao subRegiao = new SubRegiao("subregiao"+ countRegiao, selectedLatitude, selectedLongitude, getUserCode(), lastRegion);
                            JSONObject subRegionJson = jsonGenerate.generateJson(subRegiao);
                            String encryptedSubRegionJson = Criptography.encrypt(subRegionJson.toString());

                            regionQueue.add(encryptedSubRegionJson);

                            lastRegionIsSubRegion = true;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MapsActivity.this, "Sub-Região adicionada!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (lastRegionIsSubRegion && raioQueue <= 5.0) {
                            // Inserção de RestrictedRegion
                            RestrictedRegiao restrictedRegiao = new RestrictedRegiao("restrictedregiao"+ countRegiao, selectedLatitude, selectedLongitude, getUserCode(), lastRegion, true);
                            JSONObject restrictedRegionJson = jsonGenerate.generateJson(restrictedRegiao);

                            String encryptedRestrictedRegionJson = Criptography.encrypt(restrictedRegionJson.toString());

                            regionQueue.add(encryptedRestrictedRegionJson);
                            lastRegionIsSubRegion = false;
                            lastRegionisRegion = false;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MapsActivity.this, "RestrictedRegion adicionada!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MapsActivity.this, "A região não pode ser adicionada devido às regras de distância.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void addRegionFirebase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Semáforo para garantir que apenas uma operação seja realizada por vez
                    semaphore.acquire();
                    //Cálculo do raio a partir da biblioteca criada com base no último ponto adicionado ao Firebase
                    raioBanco =  Regiao.calculateDistance(lastAddLatitudeBanco, lastAddLongitudeBanco, selectedLatitude, selectedLongitude);
                    Log.d("REGIAO A SER INSERIDA NO FB", regionQueue.toString());
                    synchronized (lock) { // Garante que apenas uma Thread acessa a fila por vez
                        if (!regionQueue.isEmpty()){
                            Log.d("RAIOBANCO", "Raio: " + raioBanco);
                            if (raioBanco > 30.0) {
                                //Armazena a última região da Fila
                                // Criptografe o objeto Region em uma string JSON
                                String regionJson = regionQueue.peek();
                                String decrypt = Criptography.decrypt(regionJson);
                                Log.d("DECRYPT", decrypt);
                                Regiao regiaoFila = jsonGenerate.readJson(decrypt);
                                Log.d("REGIÃO CONVERTIDA", regiaoFila.toString());

                                if(regiaoFila instanceof Regiao) {
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                    mDatabase.child("regions").child(regiaoFila.getName()).setValue(regionJson);
                                }
                                else if(regiaoFila instanceof SubRegiao){
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                    mDatabase.child("regions").child(regiaoFila.getName()).setValue(regionJson);
                                }
                                else if(regiaoFila instanceof RestrictedRegiao){
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                    mDatabase.child("regions").child(regiaoFila.getName()).setValue(regionJson);
                                }
                                DatabaseReference.CompletionListener databasenull = new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Log.d("DATABASENULL", "Os dados não foram salvos! " + databaseError.getMessage());
                                        } else {
                                            //Chama a função para atualizar a última região adicionada ao Firebase
                                            updateLastAddedRegion();
                                            Log.d("LATLNGATT", "LAT E LONG ATULALIZADOS!");
                                        }
                                    }
                                };
                                // Exibe a mensagem na UI se a região foi adicionada ao Firebase
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MapsActivity.this, "Região adicionada ao Firebase!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // Se houver regiões dentro de 30 metros, exiba uma mensagem de erro na UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MapsActivity.this, "Esta região está em um raio de 30 metros da última e não pode ser adicionada.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            // Se a localização não foi selecionada, exibe uma mensagem de erro na UI
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MapsActivity.this, "Não há rotas a serem processadas.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    semaphore.release(); //Libera o semáforo para a próxima transação
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                removeRegion();
            }
        }).start();
    }
    private void removeRegion(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) { // Garante que apenas uma Thread acessa a Fila por vez
                    //Apaga a região da fila
                    while(!regionQueue.isEmpty()){
                        regionQueue.poll();
                    }
                    Log.d("REGIAO REMOVIDA", regionQueue.toString());
                }
            }
        }).start();
    }

    private void updateLastAddedRegion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("regions").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String regionJson = dataSnapshot.getValue(String.class);
                        String decryptedRegionJson = Criptography.decrypt(regionJson);

                        Regiao ultimaRegion = jsonGenerate.readJson(decryptedRegionJson);

                        Log.d("ULTIMAREGIAOFIREBASE", ultimaRegion.toString());
                        if (ultimaRegion.getLatitude() != 0.0 && ultimaRegion.getLongitude() != 0.0) {
                            // Atualiza lastAddLatitude e lastAddLongitude com as coordenadas da última região
                            lastAddLatitudeBanco = ultimaRegion.getLatitude();
                            lastAddLongitudeBanco = ultimaRegion.getLongitude();
                            Log.d("LATLONGFB", String.valueOf(lastAddLatitudeBanco) + String.valueOf(lastAddLongitudeBanco));
                        }
                        if (ultimaRegion instanceof SubRegiao) {
                            // Trate SubRegion
                        } else if (ultimaRegion instanceof RestrictedRegiao) {
                            // Trate RestrictedRegion
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("TAG", "Falha ao ler valor.", databaseError.toException());
                    }
                });
            }
        }).start();
    }

    private int getUserCode() {
        return 201810656; // Código de usuário = Número de matricula
    }
}