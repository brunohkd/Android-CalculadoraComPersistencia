package com.martin.calculadorasimples;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.calculadorasimples.adaptador.AdapterListaResultados;
import com.martin.calculadorasimples.entidade.Calculo;
import com.martin.calculadorasimples.persistencia.DbConexao;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton adicaoButton, subtracaoButton, multiplicacaoButton, divisaoButton;
    private Button limpaButton;
    private EditText valorUmEditText, valorDoisEditText;
    private TextView respostaTextView;
    private AdapterListaResultados adapterListaResultados;
    private boolean inserir = true;
    private long idAlteracao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        adicaoButton = (ImageButton) findViewById(R.id.adicaoImageButton);
        subtracaoButton = (ImageButton) findViewById(R.id.subtracaoImageButton);
        multiplicacaoButton = (ImageButton) findViewById(R.id.multiplicacaoImageButton);
        divisaoButton = (ImageButton) findViewById(R.id.divisaoIageButton);
        limpaButton = (Button) findViewById(R.id.limpaButton);

        adicaoButton.setOnClickListener(this);
        subtracaoButton.setOnClickListener(this);
        multiplicacaoButton.setOnClickListener(this);
        divisaoButton.setOnClickListener(this);
        limpaButton.setOnClickListener(this);

        valorUmEditText = (EditText) findViewById(R.id.valorUmEditText);
        valorDoisEditText = (EditText) findViewById(R.id.valorDoisEditText);
        respostaTextView = (TextView) findViewById(R.id.respostaTextView);


        ListView listView = (ListView) findViewById(R.id.listView);
        adapterListaResultados = new AdapterListaResultados(this);
        listView.setAdapter(adapterListaResultados);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int posicao, long id) {
                Calculo calculo = (Calculo) adapterListaResultados.getItem(posicao);
                valorUmEditText.setText(calculo.getValorUmString());
                valorDoisEditText.setText(calculo.getValorDoisString());
                respostaTextView.setText(calculo.getRespostaString());
                inserir = false;
                idAlteracao = id;
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                                           int posicao, long id) {
                final Context contexto = adapterView.getContext();
                final long idRegistro = id;


                AlertDialog.Builder alerta = new AlertDialog.Builder(contexto);
                alerta.setTitle("Faça sua Escolha");
                alerta.setMessage("Confirma exclusão?");

                alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        (new DbConexao(contexto)).excluir(idRegistro);
                        adapterListaResultados.notifyDataSetChanged();
                    }
                });

                alerta.setNegativeButton("Não", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(contexto,"Cancelado!!!", Toast.LENGTH_SHORT).show();
                    }
                });

                alerta.show();


                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {
        Log.d("MEUAPP", String.valueOf(view.getId()));
        String operador = null;

        double valorUm = 0;
        double valorDois = 0;
        double resultado = 0;

        try {
            valorUm = Float.parseFloat(valorUmEditText.getText().toString());
            valorDois = Float.parseFloat(valorDoisEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this,"Valor inválido",Toast.LENGTH_SHORT).show();
        }

        switch (view.getId()){
            case R.id.adicaoImageButton:
                resultado = valorUm + valorDois;
                operador = "+";
                break;
            case R.id.subtracaoImageButton:
                resultado = valorUm - valorDois;
                operador = "-";
                break;
            case R.id.multiplicacaoImageButton:
                resultado = valorUm * valorDois;
                operador = "*";
                break;
            case R.id.divisaoIageButton:
                resultado = valorUm / valorDois;
                operador = "/";
                break;
            case R.id.limpaButton:
                respostaTextView.setText("");
                valorUmEditText.setText("");
                valorDoisEditText.setText("");
                inserir = true;
                break;
        }
        respostaTextView.setText(String.valueOf(resultado));

        Calculo calculo = new Calculo(valorUm, valorDois, operador, resultado);
        DbConexao dbConexao = new DbConexao(this);
        if(inserir)
            dbConexao.inserir(calculo);
        else {
            calculo.setId(idAlteracao);
            dbConexao.alterar(calculo);
            inserir = true;
        }

        adapterListaResultados.notifyDataSetChanged();

    }
}








