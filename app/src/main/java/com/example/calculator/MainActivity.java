package com.example.calculator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int [] numericButtons={R.id.btnZero,R.id.btnOne,R.id.btnTwo,R.id.btnThree,R.id.btnFour,R.id.btnFive,R.id.btnSix,R.id.btnSeven,R.id.btnEight,R.id.btnNine};
    private int [] operatorButtons={R.id.btnAdd,R.id.btnSubtract,R.id.btnMultiply,R.id.btnDivide};
    private TextView txtScreen;
    private boolean lastNumeric;
    private boolean stateError;
    private boolean lastDot;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSpeak=findViewById(R.id.btnSpeak);
        txtScreen=findViewById(R.id.txtScreen);
        setNumericOnClickListener();
        setOpeartorOnCLickListener();
    }

    private void setOpeartorOnCLickListener() {
        View.OnClickListener listener= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button=(Button) v;
                if(!stateError  &&  lastNumeric){
                    txtScreen.append(button.getText());
                    lastNumeric=false;
                    lastDot=false;
                }
            }
        };
        for(int id:operatorButtons){
            findViewById(id).setOnClickListener(listener);
        }

        findViewById(R.id.btnDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastNumeric && !stateError  &&  !lastDot) {
                    txtScreen.append(".");
                    lastNumeric=false;
                    lastDot=true;
                }
            }
        });

        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtScreen.setText("");
                lastNumeric=false;
                lastDot=false;
                stateError=false;
            }
        });

        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEqual();
            }
        });

        findViewById(R.id.btnSpeak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stateError){
                    txtScreen.setText("Try Again");
                    stateError=false;
                }else {
                    promptSpeechInput();
                }
                lastNumeric=true;
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);
        }catch (ActivityNotFoundException A){
            Toast.makeText(this,getString(R.string.speech_not_supported),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT: {
                if ((resultCode == RESULT_OK) && (data != null)) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String change = result.get(0);
                    change = change.replace("x", "*");
                    change = change.replace("X", "*");
                    change = change.replace("add", "+");
                    change = change.replace("sub", "-");
                    change = change.replace("to", "2");
                    change = change.replace("plus", "+");
                    change = change.replace("minus", "-");
                    change = change.replace("times", "*");
                    change = change.replace("into", "*");
                    change = change.replace("in2", "*");
                    change = change.replace("multiply by", "*");
                    change = change.replace("divide by", "/");
                    change = change.replace("equal", "=");
                    change = change.replace("equals", "=");
                    if (change.contains("=")) {
                        change = change.replace("=", "");
                        txtScreen.setText(change);
                        onEqual();
                    } else {
                        txtScreen.setText(change);
                    }
                }
                break;
            }
        }
    }

    private void onEqual() {
        if(lastNumeric  &&  !stateError){
            String txt=txtScreen.getText().toString();
            try{
                Expression expression = null;
                try{
                    expression = new ExpressionBuilder(txt).build();
                    double result=expression.evaluate();
                    txtScreen.setText(Double.toString(result));
                }
                catch (Exception e){
                    txtScreen.setText("Error");
                }
            }
            catch (ArithmeticException e){
                stateError=true;
                lastNumeric=false;
                txtScreen.setText("Error");
            }
        }
    }

    private void setNumericOnClickListener() {
        View.OnClickListener listener= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button=(Button) v;
                if(stateError){
                    txtScreen.setText(button.getText());
                    stateError=false;
                }else{
                    txtScreen.append(button.getText());
                }
                lastNumeric=true;
            }
        };
        for(int id:numericButtons){
            findViewById(id).setOnClickListener(listener);
        }
    }
}