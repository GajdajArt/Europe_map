package com.labralab.europemap;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.labralab.europemap.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Активируем кастомное scrollView
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.horizontal.scrollView = binding.vertical;

        //Скрываем статус бар
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        //Масив названий стран
        final String[] countryTitles = {getString(R.string.finland),
                getString(R.string.estonia), getString(R.string.latvia), getString(R.string.lithuania),
                getString(R.string.belarus), getString(R.string.ukraine), getString(R.string.moldova),
                getString(R.string.sweden), getString(R.string.poland), getString(R.string.slovakia),
                getString(R.string.hungary), getString(R.string.romania), getString(R.string.bulgaria),
                getString(R.string.macedonia), getString(R.string.greece), getString(R.string.norway),
                getString(R.string.denmark), getString(R.string.germany), getString(R.string.czech_rep),
                getString(R.string.austria), getString(R.string.slovenia), getString(R.string.croatia),
                getString(R.string.bos_her), getString(R.string.serbia), getString(R.string.montenegro),
                getString(R.string.kosovo), getString(R.string.albania), getString(R.string.switzerland),
                getString(R.string.italy), getString(R.string.uk), getString(R.string.ireland),
                getString(R.string.netherlands), getString(R.string.belgium), getString(R.string.lux),
                getString(R.string.france), getString(R.string.spain), getString(R.string.portugal),
        };
        //Масив цветов в порядке соответствия названиям стран
        final int[] countryColors = {getResources().getColor(R.color.finland),
                getResources().getColor(R.color.estonia),
                getResources().getColor(R.color.latvia), getResources().getColor(R.color.lithuania),
                getResources().getColor(R.color.belarus), getResources().getColor(R.color.ukraine),
                getResources().getColor(R.color.moldova), getResources().getColor(R.color.sweden),
                getResources().getColor(R.color.poland), getResources().getColor(R.color.slovakia),
                getResources().getColor(R.color.hungary), getResources().getColor(R.color.romania),
                getResources().getColor(R.color.bulgaria), getResources().getColor(R.color.macedonia),
                getResources().getColor(R.color.greece), getResources().getColor(R.color.norway),
                getResources().getColor(R.color.denmark), getResources().getColor(R.color.germany),
                getResources().getColor(R.color.czech_rep), getResources().getColor(R.color.austria),
                getResources().getColor(R.color.slovenia), getResources().getColor(R.color.croatia),
                getResources().getColor(R.color.bos_her),
                getResources().getColor(R.color.serbia), getResources().getColor(R.color.montenegro),
                getResources().getColor(R.color.kosovo), getResources().getColor(R.color.albania),
                getResources().getColor(R.color.switzerland), getResources().getColor(R.color.italy),
                getResources().getColor(R.color.uk), getResources().getColor(R.color.ireland),
                getResources().getColor(R.color.netherlands), getResources().getColor(R.color.belgium),
                getResources().getColor(R.color.lux), getResources().getColor(R.color.france),
                getResources().getColor(R.color.spain), getResources().getColor(R.color.portugal),};

        //Находим mapView и обрабатываем касание
        final ImageView mapView = (ImageView) findViewById(R.id.mapView);
        mapView.setOnTouchListener(new ImageView.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //В случае отпускания
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int x = Math.round(event.getX());
                    int y = Math.round(event.getY());

                    //Получаем Bitmap с учетом того изображения которе вне области видимости
                    LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                    inflater.inflate(R.layout.activity_main, null).setDrawingCacheEnabled(true);
                    Bitmap bitmap = getBitmapFromView(MainActivity.this.getWindow().findViewById(R.id.mapView));
                    inflater.inflate(R.layout.activity_main, null).setDrawingCacheEnabled(false);


                    //Получаем пиксель подприкосновением
                    int pixel = bitmap.getPixel(x, y);

                    //Убиваем Bitmap (на всякий случай)
                    bitmap = null;

                    //позиция цвета и нзвания страны в массивах
                    int countryID = findCountry(pixel, countryColors);

                    //Если цвет пикселя соответствует какой либо стране
                    if (countryID > -1) {

                        //Запускаем диалог с названием выбранной страны
                        dialogCreate(countryTitles[countryID]);
                    }
                }
                return true;
            }
        });
    }

    //Метод для получения Bitmap с учетом scrollView
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    //Метод для нахождения ID
    private int findCountry(int pixel, int[] mColors) {

        //Возвращаемое значение
        int backingI = -1;
        //Получаем значения RGB пикселя
        int redValue = Color.red(pixel);
        int blueValue = Color.blue(pixel);
        int greenValue = Color.green(pixel);

        //Цикл поиска совпадений по цветам
        for (int i = 0; i < mColors.length; i++) {

//            В связи с тем что на разных устройствах один и тот жепиксель имеет разное значение RGB
//            формируем диапазон значений RGB
            int cRed = Color.red(mColors[i]);
            int cRedMax = cRed + 4;
            int cRedMin = cRed - 4;
            int cGreen = Color.green(mColors[i]);
            int cGreenMax = cGreen + 4;
            int cGreenMin = cGreen - 4;
            int cBlue = Color.blue(mColors[i]);
            int cBlueMax = cBlue + 4;
            int cBlueMin = cBlue - 4;

            //Проверяем совпадение по каждому из значений RGB в отдельности
            if (redValue <= cRedMax && redValue >= cRedMin &&
                    greenValue <= cGreenMax && greenValue >= cGreenMin &&
                    blueValue <= cBlueMax && blueValue >= cBlueMin) {

                //Если находи совпадение - выводит значение позиции цвета в массиве
                backingI = i;
                break;
            }
        }
        return backingI;
    }

    //Метод для создания AlertDialog с названием страны
    private void dialogCreate(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        builder.show();

    }
}
