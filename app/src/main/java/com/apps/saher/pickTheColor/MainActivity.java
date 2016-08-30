package com.apps.saher.pickTheColor;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener,View.OnTouchListener {

    static final int REQUEST_IMAGE_CAPTURE = 1777;
    static final int REQUEST_PICKUP_PHOTO = 1888;
    Button take_photo,choose_photo,zoom_button;
    LinearLayout linearrLayout;
    ImageView iv_showimage;
    TextView tv_RGB, tv_HEX,text_pick_photo;
    PopupWindow popupWindow;
    LinearLayout popup_layout, popup_layout1, popup_layout2;
    RelativeLayout relativeLayout;
    View popupView,circle_view;
    float eventX,eventY;
    int x,y,n,parsedResult,int_color,widthof_screen,heightof_screen;
    private boolean _doubleBackToExitPressedOnce = false;
    String colorHEX,colorRGB;
    File image,imagefolder;
    Bitmap bitmap,bitmapgallery;
    DataBaseManager dataBaseManager;
    android.app.ActionBar actionBar;
    public Typeface typeface;
    Cursor cursor;
    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    float MIN_ZOOM = 0.9f,MAX_ZOOM = 10f;

    MotionEvent motionevent;
    GestureDetector gestureDetector;
    String timeStamp;
    float scaleX , scaleY,transX,transY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBaseManager = new DataBaseManager(this);
        image = null;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        SpannableString title = new SpannableString("Library");
        title.setSpan(new TypefaceSpan("fonts/avenirmedium.otf"), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar = getActionBar();
        actionBar.setTitle(title);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
        actionBar.hide();

        AssetManager assetManager = getApplicationContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/avenirmedium.otf");

        linearrLayout = (LinearLayout) findViewById(R.id.linearrlayout);
        take_photo = (Button) findViewById(R.id.btn_takephoto);
        take_photo.setTypeface(typeface);
        choose_photo = (Button) findViewById(R.id.btn_pick);
        choose_photo.setTypeface(typeface);
        iv_showimage = (ImageView) findViewById(R.id.iv_showimage);
//        zoom_button = (Button) findViewById(R.id.zoombutton);

        text_pick_photo = (TextView) findViewById(R.id.tv_text_pick_photo);
        text_pick_photo.setTypeface(typeface);

        take_photo.setOnClickListener(this);
        choose_photo.setOnClickListener(this);
        iv_showimage.setOnTouchListener(this);
        iv_showimage.setClickable(true);
//        iv_showimage.setOnClickListener(this);

        colorHEX = null;
        colorRGB = null;
        bitmap = null;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthof_screen = size.x;
        heightof_screen = size.y;

        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

       gestureDetector = new GestureDetector(MainActivity.this,new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(popupWindow.isShowing())
                    popupWindow.dismiss();
                touching(e);
                return true;
            }
        });

        // To Share Photo Across The App
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/"))
            {
                handleSendImage(intent);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup, null);
        popupWindow = new PopupWindow(relativeLayout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setContentView(popupView);
    }

    public void createPopUp(int Color_Int) {
        relativeLayout = (RelativeLayout) popupView.findViewById(R.id.relativelayout);
        popup_layout = (LinearLayout) popupView.findViewById(R.id.popup_layout);
        popup_layout1 = (LinearLayout) popupView.findViewById(R.id.popup_layout1);
        popup_layout2 = (LinearLayout) popupView.findViewById(R.id.popup_layout2);
        circle_view = popupView.findViewById(R.id.cirle_popup);
        tv_HEX = (TextView) popupView.findViewById(R.id.tv_HEX);
        tv_RGB = (TextView) popupView.findViewById(R.id.tv_RGB);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

//        int parsedResult = (int) Long.parseLong(String.valueOf(Color_Int), 16);
        parsedResult = Color_Int;
        Log.e("The value of color", "is" + parsedResult);

        GradientDrawable gd = (GradientDrawable) circle_view.getBackground().getCurrent();
        gd.setColor(parsedResult);

        int_color = parsedResult;

        colorHEX = "#" + Integer.toHexString(Color_Int).toUpperCase().substring(2);
        //Color_In_Hex
        tv_HEX.setText("#" + Integer.toHexString(Color_Int).toUpperCase().substring(2));

        //Color_In_RGB
        int red = (Color_Int >> 16) & 0xFF;
        int green = (Color_Int >> 8) & 0xFF;
        int blue = (Color_Int) & 0xFF;

        colorRGB = "R:" + red + " " +"G:"+ green + " " +"B:"+ blue + "".toUpperCase();
        tv_RGB.setText("R :" + red + " " +"G :"+ green + " " +"B :"+ blue + "".toUpperCase());
//        SpannableStringBuilder sb = new SpannableStringBuilder("R :" + red + " " +"G :"+ green + " " +"B :"+ blue + "".toUpperCase());
//
//        sb.setSpan(R.color.Red,0,1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        tv_RGB.setText(sb);

        popupWindow.setBackgroundDrawable(new ColorDrawable());
        if((int)eventX>=400){popupWindow.showAtLocation(iv_showimage, Gravity.NO_GRAVITY, (int)eventX-(popupWindow.getWidth()/2), (int)eventY+(popupWindow.getHeight()/4));}
        else if ((int)eventX<400){popupWindow.showAtLocation(iv_showimage, Gravity.NO_GRAVITY, (int)eventX/2, (int)eventY+(popupWindow.getHeight()/4));}
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            linearrLayout.setVisibility(View.GONE);
            iv_showimage.setVisibility(View.VISIBLE);
//            zoom_button.setVisibility(View.VISIBLE);
            actionBar.show();
            File imageFile = new File(getRealPathFromURI(imageUri));
            try {
                bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, widthof_screen, heightof_screen, true);
                iv_showimage.setImageBitmap(bitmap2);
            }catch (Exception ed){}
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_takephoto:
                //camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //create new folder..
                imagefolder= new File(Environment.getExternalStorageDirectory()+"/"+getResources().getString(R.string.app_name)+"/");
                boolean success = false;
                if(!imagefolder.exists()){
                    success = imagefolder.mkdir();
                } if (!success){
                //pass the photo to the folder..
                image = new File(imagefolder,"image_"+timeStamp+".jpg");

                Log.e("the  ","n ="+n);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);}
            else {Toast.makeText(getApplicationContext(),"the folder not created ..!",Toast.LENGTH_SHORT).show();}
                break;

            case R.id.btn_pick:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_PICKUP_PHOTO);
                break;
//            case R.id.iv_showimage:
//                touching(motionevent);
//                break;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = null;
        cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    linearrLayout.setVisibility(View.GONE);
                    iv_showimage.setVisibility(View.VISIBLE);
//                    zoom_button.setVisibility(View.VISIBLE);
                    actionBar.show();
                    try {
                        bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        Bitmap bitmap1 = Bitmap.createScaledBitmap(rotatedBitmap, widthof_screen, heightof_screen, true);
                        iv_showimage.setImageBitmap(bitmap1);
//                        n++;
                    }catch (Exception ex){}
                    break;

                case REQUEST_PICKUP_PHOTO:
                    linearrLayout.setVisibility(View.GONE);
                    iv_showimage.setVisibility(View.VISIBLE);
//                    zoom_button.setVisibility(View.VISIBLE);
                    actionBar.show();
                    Uri selectedImage = data.getData();
                    File imageFile = new File(getRealPathFromURI(selectedImage));
                    try {
                        bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//                        Bitmap bitmap2 = Bitmap.createBitmap(bitmap,widthof_screen-bitmap.getWidth(),heightof_screen-bitmap.getHeight(), bitmap.getWidth(), bitmap.getHeight());
                        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                        iv_showimage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        iv_showimage.setImageBitmap(bitmap2);
                    }catch (Exception ex){}
                    break;
            }
        }
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);

    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void touching(MotionEvent event){
        eventX = event.getX();
        eventY = event.getY();

        float[] eventXY = new float[]{eventX, eventY};

        Matrix invertMatrix = new Matrix();
        iv_showimage.getImageMatrix().invert(invertMatrix);

        invertMatrix.mapPoints(eventXY);
        x = Integer.valueOf((int) eventXY[0]);
        y = Integer.valueOf((int) eventXY[1]);

        Drawable imgDrawable = iv_showimage.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();
        // Limit x, y range within bitmap
        if (x < 0) {
            x = 0;
        } else if (x > bitmap.getWidth() - 1) {
            x = bitmap.getWidth() - 1;
        }
        if (y < 0) {
            y = 0;
        } else if (y > bitmap.getHeight() - 1) {
            y = bitmap.getHeight() - 1;
        }
        int touchedHEX = bitmap.getPixel(x, y);
        if(popupWindow.isShowing())
            popupWindow.dismiss();
        createPopUp(touchedHEX);
    }

    @Override
    public void onBackPressed() {
        if(popupWindow.isShowing())
            popupWindow.dismiss();
        else if (_doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this._doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.the_Toast_mes), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                _doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        popupWindow.dismiss();
//        switch (requestcode_1){
//            case REQUEST_PICKUP_PHOTO:
//                iv_showimage.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                break;
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_actionbar, menu);

        MenuItem share = menu.findItem(R.id.menu_item_share);
        MenuItem button =menu.findItem(R.id.action_cart);
        MenuItem favorite =menu.findItem(R.id.favorite);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                if(popupWindow.isShowing())
                    popupWindow.dismiss();
                iv_showimage.setVisibility(View.GONE);
//                zoom_button.setVisibility(View.GONE);
                linearrLayout.setVisibility(View.VISIBLE);
                actionBar.hide();
                break;
            case R.id.favorite:
                if(popupWindow.isShowing()) {
                    boolean check = dataBaseManager.Add_Color(colorHEX, colorRGB);
                    if (check)
                        Toast.makeText(this, getResources().getString(R.string.color_saved), Toast.LENGTH_SHORT).show();
                    else Toast.makeText(this, getResources().getString(R.string.color_not_saved), Toast.LENGTH_SHORT).show();
                }
                else {Toast.makeText(this,getResources().getString(R.string.touch_to_save),Toast.LENGTH_LONG).show();}
                break;
            case R.id.action_cart:
                Intent intent = new Intent(this,FavoriteActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_item_share:
                if(popupWindow.isShowing())
                    sharechooser();
                else{Toast.makeText(this,getResources().getString(R.string.touch_to_share),Toast.LENGTH_LONG).show();}
                break;
            default:
//                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void sharechooser(){
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.setType("text/plain");

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        Intent openInChooser = Intent.createChooser(emailIntent, "Share via");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("mms") || packageName.contains("whatsapp") || packageName.contains("com.facebook.orca") || packageName.contains("com.google.android.apps.docs")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, colorHEX + "\n" + colorRGB);
//                if(packageName.contains("whatsapp")) {
//                    intent.putExtra(Intent.EXTRA_TEXT, colorHEX +"\n"+ colorRGB);
//                }
//                else if(packageName.contains("mms")) {
//                    intent.putExtra(Intent.EXTRA_TEXT, colorHEX +"\n"+ colorRGB);
//                }
//                else if(packageName.contains("com.facebook.orca")) {
//                    intent.putExtra(Intent.EXTRA_TEXT, colorHEX +"\n"+ colorRGB);
//                }
//                else if(packageName.contains("com.google.android.apps.docs")) {
//                    intent.putExtra(Intent.EXTRA_TEXT, colorHEX +"\n"+ colorRGB);
//                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }
        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    private void limitZoom(Matrix m) {

        float[] values = new float[9];
        m.getValues(values);
        scaleX = values[Matrix.MSCALE_X];
        scaleY = values[Matrix.MSCALE_Y];
        if(scaleX > MAX_ZOOM) {
            scaleX = MAX_ZOOM;
            values[Matrix.MTRANS_X]=transX;
//            values[Matrix.MTRANS_Y]=0;
        } else if(scaleX < MIN_ZOOM) {
            scaleX = MIN_ZOOM;
            values[Matrix.MTRANS_X]=transX;
//            values[Matrix.MTRANS_Y]=0;
        }

        if(scaleY > MAX_ZOOM) {
            scaleY = MAX_ZOOM;
//            values[Matrix.MTRANS_X]=0;
            values[Matrix.MTRANS_Y]=transY;
        } else if(scaleY < MIN_ZOOM) {
            scaleY = MIN_ZOOM;
//            values[Matrix.MTRANS_X]=0;
            values[Matrix.MTRANS_Y]=transY;
        }

        values[Matrix.MSCALE_X] = scaleX;
        values[Matrix.MSCALE_Y] = scaleY;
        m.setValues(values);
    }

    private void limitDrag(Matrix m) {
        float[] values = new float[9];
        m.getValues(values);
        transX = values[Matrix.MTRANS_X];
        transY = values[Matrix.MTRANS_Y];
//        float scaleX = values[Matrix.MSCALE_X];
//        float scaleY = values[Matrix.MSCALE_Y];

        Rect bounds = iv_showimage.getDrawable().getBounds();
        int viewWidth = getResources().getDisplayMetrics().widthPixels;
        int viewHeight = getResources().getDisplayMetrics().heightPixels;

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        float minX = (-width + 100) * scaleX;
        float minY = (-height + 200) * scaleY;

        if(transX > (viewWidth /2)) {
            transX = viewWidth /2;
        } else if(transX < minX ) {
            transX = minX;
        }

        if(transY > (viewHeight /2)) {
            transY = viewHeight /2;
        } else if(transY < minY) {
            transY = minY;
        }

        values[Matrix.MTRANS_X] = transX;
        values[Matrix.MTRANS_Y] = transY;
        m.setValues(values);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        motionevent = event;
        iv_showimage.setScaleType(ImageView.ScaleType.MATRIX);
        if(v.getId()==R.id.iv_showimage) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if(popupWindow.isShowing())
                        popupWindow.dismiss();
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    break;
                case MotionEvent.ACTION_UP:
//                    touching(event);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        // ...
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = newDist / oldDist;
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }}
                    break;
            }
        }
        limitZoom(matrix);
        limitDrag(matrix);
//        Bitmap bitmap4 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        iv_showimage.setImageMatrix(matrix);

        return false;
    }

}






