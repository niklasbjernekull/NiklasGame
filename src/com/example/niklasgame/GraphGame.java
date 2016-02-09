package com.example.niklasgame;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2012-03-15
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public class GraphGame  extends Activity
{
    private GraphView gView;
    //private SensorManager sensorManager;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Display display = getWindowManager().getDefaultDisplay();

        gView = new GraphView(this, display);
        setContentView(gView);
    }

    @Override
    public void onBackPressed() {
        Log.i("Pressed back button!", "****************");

        gView.setIsPaused(!gView.getIsPaused());
        
        /*AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Game paused");

        alert.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        }); */
    }

    public class GraphView extends View implements SensorEventListener {

        private SensorManager sensorManager;

        float x_value;
        float y_value;
        float x_acc;
        float y_acc;
        float x_old = 0;
        float y_old = 0;
        int x_max;
        int y_max;
        float friction = 0.98f;
        boolean first=true;
        float[] points_cord;
        float[] monster_cord;

        int pause_size = 40;
        int pause_x;
        int pause_y = 0;
        private boolean isPaused = false;
        int pause_title_width = 350;
        int pause_title_height = 100;

        int pause_screen_x = 50;
        int pause_screen_y = 200;

        int continue_button_width = 350;
        int continue_button_height = 100;

        float scale_variable = 1;
        float scale_var_height = 1;
        float scale_var_width = 1;

        int score_points=0;

        int tiltSensitivity = 10;
        
        //int reSize = 2;

        int ball_size = 60;
        //int ball_height = ball_width;
        //float ball_angle = 0;
        //float angle_step = 5;
        private float IMAGE_ANGLE_CORRECTION = 45; //degrees

        int candy_size = 40;
        //int candy_height = candy_width;
        int candy_type = 0;

        int grass_width = 158;
        int grass_height = 100;
        
        int flower_size = 30;
        //int flower_height = flower_width;
        ArrayList<int[]> flowerCord = new ArrayList<int[]>();
        ArrayList<int[]> flowerGrow = new ArrayList<int[]>();
        ArrayList<int[]> flowerShrink = new ArrayList<int[]>();
        //int flowerPos = 0;

        int monster_width = 84;
        int monster_height = 120;
        float monsterMovement = 0.5f;
        float monsterMovementIncrease = 0.02f;

        private float[][] grassPos = new float[6][2];

        Bitmap bitball;
        Bitmap bGround;
        Bitmap grassblades;
        Bitmap redFlower;
        Bitmap purpleFlower;
        Bitmap candy_blue;
        Bitmap candy_green;
        Bitmap candy_pink;
        Bitmap candy_pinkblue;
        Bitmap monster_L1Img;
        Bitmap monster_L2Img;
        Bitmap monster_R1Img;
        Bitmap monster_R2Img;
        Bitmap pause_icon;
        Bitmap paused_title;
        Bitmap continue_button;

        public GraphView(Context context, Display display) {
            super(context);

            x_max = display.getWidth();
            y_max = display.getHeight();

            scale_variable = (float)(x_max + y_max) / 1334;
            scale_var_width = (float)x_max / 480;
            scale_var_height = (float)y_max / 854;

            ball_size = (int)(ball_size * scale_variable);
            candy_size = (int)(candy_size * scale_variable);
            grass_width = (int)(grass_width * scale_variable);
            grass_height = (int)(grass_height * scale_variable);
            flower_size = (int)(flower_size * scale_variable);
            monster_width = (int)(monster_width * scale_variable);
            monster_height = (int)(monster_height * scale_variable);
            pause_size = (int)(pause_size * scale_variable);
            pause_title_width = (int)(pause_title_width * scale_variable);
            pause_title_height = (int)(pause_title_height * scale_variable);
            continue_button_width = (int)(continue_button_width * scale_variable);
            continue_button_height = (int)(continue_button_height * scale_variable);

            Bitmap temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dino_s);
            bitball = Bitmap.createScaledBitmap(temp_bitmap, ball_size, ball_size, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.candy_blue);
            candy_blue = Bitmap.createScaledBitmap(temp_bitmap, candy_size, candy_size, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.candy_greenblue);
            candy_green = Bitmap.createScaledBitmap(temp_bitmap, candy_size, candy_size, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.candy_peach);
            candy_pink = Bitmap.createScaledBitmap(temp_bitmap, candy_size, candy_size, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.candy_pinkblue);
            candy_pinkblue = Bitmap.createScaledBitmap(temp_bitmap, candy_size, candy_size, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grassblades5);
            grassblades = Bitmap.createScaledBitmap(temp_bitmap, grass_width, grass_height, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.redflower);
            redFlower = Bitmap.createScaledBitmap(temp_bitmap, flower_size, flower_size, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.purpleflower);
            purpleFlower = Bitmap.createScaledBitmap(temp_bitmap, flower_size, flower_size, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monster_l1);
            monster_L1Img = Bitmap.createScaledBitmap(temp_bitmap, monster_width, monster_height, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monster_l2);
            monster_L2Img = Bitmap.createScaledBitmap(temp_bitmap, monster_width, monster_height, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monster_r1);
            monster_R1Img = Bitmap.createScaledBitmap(temp_bitmap, monster_width, monster_height, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monster_r2);
            monster_R2Img = Bitmap.createScaledBitmap(temp_bitmap, monster_width, monster_height, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_pause);
            pause_icon = Bitmap.createScaledBitmap(temp_bitmap, pause_size, pause_size, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.paused);
            paused_title = Bitmap.createScaledBitmap(temp_bitmap, pause_title_width, pause_title_height, true);

            temp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.continue_button);
            continue_button = Bitmap.createScaledBitmap(temp_bitmap, continue_button_width, continue_button_height, true);

            Bitmap field = BitmapFactory.decodeResource(getResources(), R.drawable.grassjungle);
            bGround = Bitmap.createScaledBitmap(field, x_max, y_max, true);

            sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
            // add listener. The listener will be HelloAndroid (this) class
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME);

            x_value=x_max/2;
            y_value=y_max/2;
            x_acc=0;
            y_acc=0;
            points_cord = new float[3];
            points_cord[2] = candy_size;
            monster_cord = new float[2];
            monster_cord[0] = x_max - monster_width;
            monster_cord[1] = y_max - monster_height;
            pause_x = x_max-pause_size;

            monsterMovement = monsterMovement * scale_variable;
            monsterMovementIncrease = monsterMovementIncrease * scale_variable;

            grassPos[0][0] = x_max-grass_width; grassPos[0][1] = 770*scale_var_height;
            grassPos[1][0] = -15*scale_var_width; grassPos[1][1] = 785*scale_var_height;
            grassPos[2][0] = 0; grassPos[2][1] = 0;
            grassPos[3][0] = 350*scale_var_width; grassPos[3][1] = -15*scale_var_height;
            grassPos[4][0] = -55*scale_var_width; grassPos[4][1] = 500*scale_var_height;
            grassPos[5][0] = 380*scale_var_width; grassPos[5][1] = 200*scale_var_height;
            
            //flowerCord = new int[6][3];

            setPoints_cord();

            /*Intent data = new Intent();
            data.putExtra("Points", Integer.toString(125));
            setResult(Activity.RESULT_OK, data);
            finish();*/
        }

        /*** Set candy coordinates***/
        public void setPoints_cord() {
            points_cord[0] = (float) Math.random()*x_max-points_cord[2]; //(Math.random()*1000) % x_max-points_cord[2];
            points_cord[1] = (float) Math.random()*y_max-points_cord[2]; //(Math.random()*1000) % y_max-points_cord[2];

            float ballpos_x1 = x_value - 100;
            float ballpos_x2 = x_value + 100;
            float ballpos_y1 = y_value - 100;
            float ballpos_y2 = y_value + 100;

            if((ballpos_x1 < points_cord[0] && points_cord[0] < ballpos_x2) &&
                    (ballpos_y1 < points_cord[1] && points_cord[1] < ballpos_y2)) {setPoints_cord();}

            if(points_cord[0] < points_cord [2] || points_cord[1] < points_cord [2]) {setPoints_cord();}
            
            /*Which candy color is it*/
            float randit = (float) Math.random() * 4;
            candy_type = (int)randit;
        }
        
        public void flowerCreation(int posx, int posy) {
            int[] flower = new int[]{posx, posy, 0};
            flowerGrow.add(flower);
        }

        public void flowerHandling() {
            for(int i=flowerGrow.size()-1; i >= 0; i--) {
                int[] flower = flowerGrow.get(i);
                flower[2]++;
                if(flower[2] >= flower_size) {
                    int[] newFlower = new int[]{flower[0], flower[1]};
                    flowerCord.add(newFlower);
                    flowerGrow.remove(i);
                }
            }

            while(flowerCord.size() > 10) {
                int[] item = flowerCord.get(0);
                int[] flower = new int[]{item[0], item[1], flower_size};
                flowerShrink.add(flower);
                flowerCord.remove(0);
            }

            for(int i=flowerShrink.size()-1; i >= 0; i--) {
                int[] flower = flowerShrink.get(i);
                flower[2]--;
                if(flower[2] <= 0) {
                    flowerShrink.remove(i);
                }
            }
        }
        
        public void setIsPaused(boolean toPause) {
            isPaused = toPause;
        }
        
        public boolean getIsPaused() {
            return isPaused;
        }

        @Override
        public void onSensorChanged(SensorEvent event){

            /*Paused*/
            if(isPaused) {
                return;
            }
            //Log.i("******", "Getting through! " + isPaused);
            // Get instance of Vibrator from current Context
            //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            // check sensor type
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;

            /***Calibration***
            if(first) {x_old = 0.06f; y_old = -0.06f; first=false;}//x_old = event.values[0]/tiltSensitivity; y_old = -event.values[1]/tiltSensitivity; first=false;}
            // assign directions*/

            float accx_new = x_old - event.values[0]/tiltSensitivity;
            float accy_new = y_old + event.values[1]/tiltSensitivity;

            x_acc = x_acc * friction;
            y_acc = y_acc * friction;

            x_acc = x_acc-accx_new;
            y_acc = y_acc-accy_new;

            x_value = x_value-x_acc;
            y_value = y_value-y_acc;

            if(x_value < 0) {x_value = -x_value; x_acc=-x_acc; //v.vibrate(100);
            }else if(x_value > x_max-ball_size) {x_value = x_max-ball_size; x_acc=-x_acc; //v.vibrate(100);
            }

            if(y_value < 0) {y_value = -y_value; y_acc=-y_acc; //v.vibrate(100);
            }else if(y_value > y_max-ball_size) {y_value = y_max-ball_size; y_acc=-y_acc; //v.vibrate(100);
            }

            /***Point collision***/
            float points_x1 = points_cord[0];
            float points_x2 = points_cord[0]+points_cord[2];
            float points_y1 = points_cord[1];
            float points_y2 = points_cord[1]+points_cord[2];
            float ballpos_x1 = x_value;
            float ballpos_x2 = x_value+ball_size;
            float ballpos_y1 = y_value;
            float ballpos_y2 = y_value+ball_size;

            if((ballpos_x1 <= points_x2 && ballpos_x2 >= points_x1) && (ballpos_y1 <= points_y2 && ballpos_y2 >= points_y1)) {
                // Vibrate for 50 milliseconds
                //v.vibrate(50);

                score_points++;
                x_acc = x_acc * 0.95f;
                y_acc = y_acc * 0.95f;

                //increase monster movement
                if(monsterMovement < 3) {
                    monsterMovement += monsterMovementIncrease;
                    Log.i("DING! Movement : " + monsterMovement, ", Inc: " + monsterMovementIncrease);
                    Log.i("Monster movement: ", Float.toString(monsterMovement));
                }
                
                flowerCreation((int)(points_cord[0]+points_cord[2]/2), (int)(points_cord[1]+points_cord[2]/2));
                setPoints_cord();
            }

            /***Monster Movement***/
            if(monster_cord[0] < x_value) {monster_cord[0] += monsterMovement;
            }else { monster_cord[0] -= monsterMovement; }

            if(monster_cord[1] < y_value) {monster_cord[1] += monsterMovement;
            }else { monster_cord[1] -= monsterMovement; }

            /***Monster Collision***/
            //10,6 - 28,52 is the monster hitbox
            if((monster_cord[0]+(20) <= ballpos_x2 && monster_cord[0]+(56) >= ballpos_x1)
                    && (monster_cord[1]+(12) <= ballpos_y2 && monster_cord[1]+(104) >= ballpos_y1)) {
                /***Game Over ***/
                //v.vibrate(100);

                Intent data = new Intent();
                data.putExtra("Points", Integer.toString(score_points));
                setResult(Activity.RESULT_OK, data);

                //v.cancel();
                
                finish();  //Exit
         
            }
            
            /***Handle the Flower Growth***/
            flowerHandling();

            //Log.i("onSensorChanged", "X value:" + Float.toString(x_value));
            //Log.i("onSensorChanged", "Y value:" + Float.toString(y_value));
        }

        public void onAccuracyChanged(Sensor sensor,int accuracy){

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float touched_x = event.getX();
            float touched_y = event.getY();
            
            if(isPaused) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if((touched_x >= (x_max-continue_button_width)/2 && touched_x <= ((x_max-continue_button_width)/2)+continue_button_width)
                    && (touched_y >= y_max-pause_screen_y-continue_button_height-20 && touched_y <= y_max-pause_screen_y-continue_button_height-20+continue_button_height )) {
                        isPaused = false;
                        Log.i("###########", "Setting Pause! " + isPaused);
                    }
                }
            } else {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if((touched_x >= pause_x && touched_x <= (pause_x+pause_size)) &&
                            (touched_y >= pause_y && touched_y <= (pause_y+pause_size))) {
                        isPaused = true;
                        Log.i("###########", "Setting Pause! " + isPaused);
                    }
                }
            }

            return true;
        }

        protected float getDirectionAngle() {
            //float angle = 180;

            double rads = Math.atan2(y_acc, x_acc);

            /*if (rads < 0)
                rads = Math.abs(rads);
            else
                rads = 2*Math.PI - rads;

            return (float)Math.toDegrees(rads);   */


            //Log.i("Angle:" + rads, "Acc Y:" + y_acc + ", X:" +x_acc);

            return (float) Math.toDegrees(rads);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            /***Background***/
            canvas.drawBitmap(bGround, 0, 0, null);

            /*If game is paused*/
            if(isPaused) {
                drawPause(canvas);
                invalidate();
                return;
            }

            //Log.i("******", "Getting through! " + isPaused);
            
            /***Flowers***/
            drawFlowers(canvas);

            /***Dino***/
            Matrix rotMatrix = new Matrix();
            rotMatrix.reset();
            rotMatrix.setTranslate(x_value, y_value);

            float angle = getDirectionAngle();

            rotMatrix.postRotate(angle + IMAGE_ANGLE_CORRECTION, x_value+(ball_size/2), y_value+(ball_size/2));
            canvas.drawBitmap(bitball, rotMatrix, null);
            //canvas.drawBitmap(bitball, x_value, y_value, null);  //x_max/2 - x_value*15, y_max/2 + y_value*20

            /***Candy***/
            //Log.i("!!!!!!CANDY TYPE: ", " " + candy_type);
            if(candy_type == 0) {
                canvas.drawBitmap(candy_blue, points_cord[0], points_cord[1], null);
            } else if(candy_type == 1) {
                canvas.drawBitmap(candy_green, points_cord[0], points_cord[1], null);
            } else if(candy_type == 2) {
                canvas.drawBitmap(candy_pink, points_cord[0], points_cord[1], null);
            } else {
                canvas.drawBitmap(candy_pinkblue, points_cord[0], points_cord[1], null);
            }

            /***Monster***/
            if(x_value-1 < monster_cord[0]) {
                //Log.i("Y modulus: ", Integer.toString((int)y_value%2));
                if(((int)(monster_cord[1]/10)%2) == 1) {
                    //Log.i("Monster bitmap: ", "L1");
                    canvas.drawBitmap(monster_L1Img, monster_cord[0], monster_cord[1], null);
                } else {
                    //Log.i("Monster bitmap: ", "L2");
                    canvas.drawBitmap(monster_L2Img, monster_cord[0], monster_cord[1], null);
                }
            } else {
                if(((int)(monster_cord[1]/10)%2) ==1) {
                    //Log.i("Monster bitmap: ", "R1");
                    canvas.drawBitmap(monster_R1Img, monster_cord[0], monster_cord[1], null);
                } else {
                    //Log.i("Monster bitmap: ", "R2");
                    canvas.drawBitmap(monster_R2Img, monster_cord[0], monster_cord[1], null);
                }
            }

            /***Grass***/
            drawGrass(canvas);

            /***Score***/
            Paint textPaint = new Paint();
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(50);
            canvas.drawText(Integer.toString(score_points),0,40,textPaint);

            /***Direction line and acc***/
            /*Paint linePaint = new Paint();
            linePaint.setColor(Color.RED);
            canvas.drawLine(x_value, y_value, (-x_acc*10)+x_value, (-y_acc*10)+y_value, linePaint);
            canvas.drawText(Float.toString(angle),(-x_acc*10)+x_value, (-y_acc*10)+y_value,textPaint); */

            /***Pause button***/
            canvas.drawBitmap(pause_icon, pause_x, pause_y, null);
            
            invalidate();


            /*Paint myPaint = new Paint();
           myPaint.setColor(Color.rgb(0, 0, 0));
           //myPaint.setStrokeWidth(10);
           canvas.drawRect(x_max/2-50, 0, x_max/2+50, 28, myPaint);
           canvas.drawRect(x_max/2-50, y_max-28, x_max/2+50, y_max, myPaint); */

            /*Paint pointPaint = new Paint();
            pointPaint.setColor(Color.MAGENTA);
            canvas.drawCircle(points_cord[0], points_cord[1], points_cord[2], pointPaint);*/
        }
        
        private void drawPause(Canvas canvas) {
            Log.i("******", "Drawing Pause! " + isPaused);
            drawGrass(canvas);

            Paint rectanglePaint = new Paint();
            rectanglePaint.setColor(Color.argb(50, 255, 255, 255));
            canvas.drawRoundRect(new RectF(pause_screen_x, pause_screen_y, x_max-pause_screen_x, y_max-pause_screen_y), 20, 20, rectanglePaint);

            canvas.drawBitmap(paused_title, (x_max-pause_title_width)/2, pause_screen_y+20, null);
            canvas.drawBitmap(continue_button, (x_max-continue_button_width)/2, y_max-pause_screen_y-continue_button_height-20, null);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(70);
            canvas.drawText("SCORE",(x_max/2)-100,400,textPaint);
            textPaint.setTextSize(60);
            canvas.drawText(Integer.toString(score_points),(x_max/2)-20,460,textPaint);

            //canvas.drawBitmap(paused_title, 100, 220, null);
        }
        
        public void drawGrass(Canvas canvas) {
            /***GRASSSSSSSS!!!***/

            canvas.drawBitmap(grassblades, grassPos[0][0], grassPos[0][1], null);

            Matrix rotMatrix = new Matrix(); //rotMatrix.reset();
            rotMatrix.setTranslate(grassPos[1][0], grassPos[1][1]);
            rotMatrix.postRotate(25, (grass_width/2)+grassPos[1][0], (grass_height/2)+grassPos[1][1]);
            canvas.drawBitmap(grassblades, rotMatrix, null);
            //canvas.drawBitmap(grassblades, 0, 770, null);

            rotMatrix.reset();
            rotMatrix.setTranslate(grassPos[2][0], grassPos[2][1]);
            rotMatrix.postRotate(180, (grass_width/2), (grass_height/2));
            canvas.drawBitmap(grassblades, rotMatrix, null);

            rotMatrix.reset();
            rotMatrix.setTranslate(grassPos[3][0], grassPos[3][1]);
            rotMatrix.postRotate(200, (grass_width/2)+grassPos[3][0], (grass_height/2)+grassPos[3][1]);
            canvas.drawBitmap(grassblades, rotMatrix, null);

            rotMatrix.reset();
            rotMatrix.setTranslate(grassPos[4][0], grassPos[4][1]);
            rotMatrix.postRotate(70, (grass_width/2)+grassPos[4][0], (grass_height/2)+grassPos[4][1]);
            canvas.drawBitmap(grassblades, rotMatrix, null);

            rotMatrix.reset();
            rotMatrix.setTranslate(grassPos[5][0], grassPos[5][1]);
            rotMatrix.postRotate(-80, (grass_width/2)+grassPos[5][0], (grass_height/2)+grassPos[5][1]);
            canvas.drawBitmap(grassblades, rotMatrix, null);
        }
        
        public void drawFlowers(Canvas canvas) {
            int[] flower;
            Bitmap flowerBit;

            Iterator iter = flowerGrow.iterator();
            while(iter.hasNext()) {
                flower = (int[]) iter.next();
                if((flower[0] + flower[1])%2 == 0) {
                    flowerBit = redFlower;
                } else {
                    flowerBit = purpleFlower;
                }
                int filler = (flower_size-flower[2])/2;
                canvas.drawBitmap(Bitmap.createScaledBitmap(flowerBit,flower[2], flower[2], false), flower[0]+filler, flower[1]+filler, null);
            }

            iter = flowerCord.iterator();
            while(iter.hasNext()) {
                flower = (int[]) iter.next();
                if((flower[0] + flower[1])%2 == 0) {
                    flowerBit = redFlower;
                } else {
                    flowerBit = purpleFlower;
                }
                canvas.drawBitmap(flowerBit, flower[0], flower[1], null);
            }

            iter = flowerShrink.iterator();
            while(iter.hasNext()) {
                flower = (int[]) iter.next();
                if((flower[0] + flower[1])%2 == 0) {
                    flowerBit = redFlower;
                } else {
                    flowerBit = purpleFlower;
                }
                int filler = (flower_size-flower[2])/2;
                canvas.drawBitmap(Bitmap.createScaledBitmap(flowerBit,flower[2], flower[2], false), flower[0]+filler, flower[1]+filler, null);
            }
        }


    }
}
