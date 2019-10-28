package com.example.okhttptext.getIfo;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.okhttptext.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetServer extends AppCompatActivity implements View.OnClickListener {

    /*
    这是一个Get请求
     */
    private static final int GET = 1;
    private static final String TAG = GetServer.class.getSimpleName();
    private TextView mText;
    private Button mButton;
    private Button mOkHttpUtils;
    private Button mDownLoadBurrom;
    private Button mRequestImage;
    private ImageView imageView;
    private ProgressBar mProgressBar;

    //okHttp
    private OkHttpClient client = new OkHttpClient();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET:
                    //获取数据
                    mText.setText((String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_server);
        mButton = (Button) findViewById(R.id.btn_get_post);
        mText = (TextView) findViewById(R.id.tv_result);
        mOkHttpUtils = (Button) findViewById(R.id.btn_get_ok_http_utils);
        mDownLoadBurrom = (Button)findViewById(R.id.btn_download_file);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        imageView = (ImageView) findViewById(R.id.image_icon);
        mRequestImage = (Button)findViewById(R.id.btn_image);

        mButton.setOnClickListener(this);
        mOkHttpUtils.setOnClickListener(this);
        mDownLoadBurrom.setOnClickListener(this);
        mRequestImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_post://用原生 okhttp 请求网络数据
                getDataFromGet();
                break;
            case R.id.btn_get_ok_http_utils://用 http_utils 请求网络数据
                getDataByOkhttpUtils();
                break;
            case R.id.btn_download_file://用 http_utils 请求网络数据
                downloadFile();
                break;
            case R.id.btn_image://用 http_utils 请求网络数据
                getImage();
                break;
        }
    }

    private void getDataFromGet(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String string = getUrl("http://192.168.31.176:8080/MyJspProject/lessen/exp_5/index.jsp");
                    Log.e(TAG,"111------------------1111------"+string);
                    Message msg = Message.obtain();
                    msg.what = GET;
                    msg.obj = string;
                    handler.sendMessage(msg);
                }catch (IOException e){
                    Log.e(TAG,"222------------------222------"+e.toString());
                    e.printStackTrace();
                    Toast.makeText(GetServer.this,"无法获得数据！请检查网络",Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    /**
     * get请求
     * @param url 网络连接
     * @return
     * @throws IOException
     */
    private String getUrl(String url) throws IOException {//OkHttp方法不能再主线程中执行/*所以要间隔子线程，并在其中执行*/
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.e(TAG,"333------------------333------"+request);
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 使用 ok_http_Utils 获得请求数据
     */
    public void getDataByOkhttpUtils()
    {
        String url = "http://www.391k.com/api/xapi.ashx/info.json?key=bd_hyrzjjfb4modhj&size=10&page=1";
        url="http://47.107.132.227/";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback//回调
    {
        @Override
        public void onBefore(Request request, int id)
        {
            setTitle("loading...");
        }

        @Override
        public void onAfter(int id)
        {
            setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id)
        {
            e.printStackTrace();
            mText.setText("onError:" + e.getMessage());
            Log.d(TAG, e.toString());
        }

        @Override
        public void onResponse(String response, int id)
        {
            Log.e(TAG, "onResponse：complete");
            mText.setText("onResponse:" + response);

            switch (id)
            {
                case 100:
                    Toast.makeText(GetServer.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(GetServer.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id)
        {
            Log.e(TAG, "inProgress:" + progress);
//            mProgressBar.setProgress((int) (100 * progress));
        }
    }

    /**
     * 使用 okHttp_Utils 下载大文件
     */
    public void downloadFile()
    {
        String url = "https://github.com/hongyangAndroid/okhttp-utils/blob/master/okhttputils-2_4_1.jar?raw=true";
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson-2.2.1.jar")//
                {

                    @Override
                    public void onBefore(Request request, int id)
                    {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id)
                    {
                        mProgressBar.setProgress((int) (100 * progress));
                        Log.e(TAG, "inProgress :" + (int) (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        Log.e(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file, int id)
                    {
                        Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                    }
                });
    }

    /**
     * 使用 okHttp 获取图片
     * @param
     */
    public void getImage()
    {
        mText.setText("");
        String url = "http://47.107.132.227/form";
        OkHttpUtils
                .get()//
                .url(url)//
                .tag(this)//
                .build()//
                .connTimeOut(20000)//连接超时
                .readTimeOut(20000)//读取超时
                .writeTimeOut(20000)//写超时
                .execute(new BitmapCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        mText.setText("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id)
                    {
                        Log.e("TAG", "onResponse：complete");
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

}
