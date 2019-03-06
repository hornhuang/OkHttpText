package com.example.okhttptext.getIfo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostServer extends AppCompatActivity implements View.OnClickListener {
    /*
    post 请求
     */
    private static final int POST = 2;
    private static final String TAG = GetServer.class.getSimpleName();
    private TextView mText;
    private Button mButton;
    private Button mPostButton;
    private Button mUpLoadBtn;
    private ProgressBar mProgressBar;

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    //从相册获得图片
    Bitmap bitmap;
    //判断返回到的Activity
    private static final int IMAGE_REQUEST_CODE = 0;
    //图片路径
    private String path, uploadPath ;
    private ImageView imageView01;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if((Integer)msg.obj==0){
                imageView01.setImageBitmap(bitmap);
                multiFileUpload();
            }
            super.handleMessage(msg);
        }
    };


    private Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case POST:
                    //获取数据
                    mText.setText((String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_server);
        mButton = (Button) findViewById(R.id.btn_get_post);
        mText = (TextView) findViewById(R.id.tv_result);
        mPostButton = (Button) findViewById(R.id.btn_post_ok_http_utils);
        mUpLoadBtn = (Button) findViewById(R.id.btn_upload_file);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        imageView01 = (ImageView) findViewById(R.id.imageview);

        mButton.setOnClickListener(this);
        mPostButton.setOnClickListener(this);
        mUpLoadBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_post://用原生 okhttp 请求网络数据
                mText.setText("");
                getDataFromPost();
                break;
            case R.id.btn_post_ok_http_utils://用 okhttp_utils 请求网络数据
                mText.setText("");
                postDataByOkhttpUtils();
                break;
            case R.id.btn_upload_file://用 okhttp_utils 请求文件上传
                mText.setText("");
                uploadClick();
                break;
        }
    }

    /*
    使用 post 请求访问数据
     */
    private void getDataFromPost(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("id",2);
                        json.put("name","jsonText");
                        json.put("age",21);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String string = post("http://47.107.132.227/api/mysql/getifo",json.toString());
                    Log.e("TAG",string);
                    Message msg = Message.obtain();
                    msg.what = POST;
                    msg.obj = string;
                    handler1.sendMessage(msg);
                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(PostServer.this,"发送失败！",Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    /**
     * okHttp post请求
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 使用 ok_http_Utils 获得请求数据
     */
    public void postDataByOkhttpUtils()
    {
        String url = "http://www.zhiyun-tech.com/App/Rider-M/changelog-zh.txt";
        url="http://47.107.132.227/api/mysql/postifo?id=5&name=%22qaz%22&age=%22666%22";
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new PostServer.MyStringCallback());
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
        }

        @Override
        public void onResponse(String response, int id)
        {
            Log.e(TAG, "onResponse：complete");
            mText.setText("onResponse:" + response);

            switch (id)
            {
                case 100:
                    Toast.makeText(PostServer.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(PostServer.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id)
        {
            Log.e(TAG, "inProgress:" + progress);
            mProgressBar.setProgress((int) (100 * progress));
        }
    }

    /**
     * 使用 okHttp_Utils 上传 单个/多个 文件
     */
    public void multiFileUpload()
    {
        String mBaseUrl = "http://47.107.132.227/upload";
        File file = new File(uploadPath);
//        File file2 = new File(uploadPath, "test02.png");
        if (!file.exists())//判断是否存在
        {
            Toast.makeText(PostServer.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();

        String url = mBaseUrl;
        OkHttpUtils.post()//
                .addFile("logo" +
                        ".", "text01.png", file)//
//                .addFile("mFile", "text02.png", file2)//
                .url(url)
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    // 文件上传的点击事件
    private void uploadClick(){
        if(ContextCompat.checkSelfPermission(PostServer.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PostServer.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }


    /**
     * 访问相册 获得路径
     */
    private  void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过Uri和selection来获取真实图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView01.setImageBitmap(bitmap);
        }else {
            Toast.makeText(PostServer.this,"fail to get image",Toast.LENGTH_SHORT).show();
        }
    }


    @TargetApi(19)
    private void handleImageOmKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //如果document类型是U日，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是普通类型 用普通方法处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果file类型位uri直街获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //在相册里面选择好相片之后调回到现在的这个activity中
        switch (requestCode) {
            case IMAGE_REQUEST_CODE://这里的requestCode是我自己设置的，就是确定返回到那个Activity的标志
                if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        path = cursor.getString(columnIndex);  //获取照片路径
                        uploadPath = path;
                        cursor.close();

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 1;
                        bitmap = BitmapFactory.decodeFile(path,options);
                        imageView01.setImageBitmap(bitmap);
                        change();
                        Toast.makeText(PostServer.this,path,Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /*定义一个Handler，定义延时执行的行为*/
    public  void change(){
        new Thread(){
            @Override
            public void run() {
                while ( bitmap == null ){
                    bitmap = BitmapFactory.decodeFile(path);
                    Log.v("qwe","123");
                }
                Message message = handler.obtainMessage();
                message.obj = 0;
                handler.sendMessage(message);
            }
        }.start();
    }


}
