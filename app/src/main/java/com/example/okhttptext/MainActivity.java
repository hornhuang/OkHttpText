package com.example.okhttptext;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.okhttptext.getIfo.GetServer;
import com.example.okhttptext.getIfo.PostServer;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mText;
    private Button mGetButton;
    private Button mPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGetButton = (Button) findViewById(R.id.btn_get_post);
        mPostButton = (Button) findViewById(R.id.btn_post_post);
        mText = (TextView) findViewById(R.id.tv_result);

        mGetButton.setOnClickListener(this);
        mPostButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_post://用原生 okhttp 请求网络数据
                startActivity(new Intent(MainActivity.this, GetServer.class));
                break;
            case R.id.btn_post_post://用原生 okhttp 请求网络数据
                startActivity(new Intent(MainActivity.this, PostServer.class));
                break;
        }
    }

}