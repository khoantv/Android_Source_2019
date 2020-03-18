package audiorecord.dac.com.ghiam;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecordActivity extends AppCompatActivity {

    //region .Properties.
    Button buttonStart, buttonStop, buttonPlayLastRecordAudio, buttonStopPlayingRecording ;
    TextView txtFileName;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    String FolderName,FileName, FileTextName;
    Boolean isExternal;
    Chronometer clock;
    List<String> requests;
    EditText edtText;
    long timeWhenStopped = 0;
    int count = 1;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Intent callerIntent = getIntent();
        Bundle goisangCaller = callerIntent.getBundleExtra("mypackage");

        FolderName = goisangCaller.getString("FolderName");
        FileTextName = goisangCaller.getString("FileName");
        isExternal = goisangCaller.getBoolean("isExternal");

        //Toast.makeText(RecordActivity.this, FolderName + "; "+ FileName+"; "+ isInternal, Toast.LENGTH_SHORT).show();

        //region .Init Control.
        buttonStart = (Button) findViewById(R.id.btnStart);
        buttonStop = (Button) findViewById(R.id.btnStop);
        buttonPlayLastRecordAudio = (Button) findViewById(R.id.btnPlay);
        buttonStopPlayingRecording = (Button)findViewById(R.id.btnStopPlay);
        clock = (Chronometer) findViewById(R.id.chronometerClock);
        txtFileName = (TextView) findViewById(R.id.txtFileName);
        edtText = (EditText) findViewById(R.id.edtText);

        buttonStart.setEnabled(true);
        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);
        //endregion

//
//        Latasa
//        Bật 0..10
//        Tắt 0..10
//        Bật hết
//        Tắt hết
        //region .Set data request.
        requests = new ArrayList<>();
        requests.add("latasa");
        requests.add("Bật 0");requests.add("Bật 1");
        requests.add("Bật 2");requests.add("Bật 3");requests.add("Bật 4");requests.add("Bật 5");
        requests.add("Bật 6");requests.add("Bật 7");requests.add("Bật 8");requests.add("Bật 9");
        requests.add("Bật 10");
        requests.add("Tắt 0");requests.add("Tắt 1");
        requests.add("Tắt 2");requests.add("Tắt 3");requests.add("Tắt 4");requests.add("Tắt 5");
        requests.add("Tắt 6");requests.add("Tắt 7");requests.add("Tắt 8");requests.add("Tắt 9");
        requests.add("Tắt 10");
        requests.add("Tắt hết");
        requests.add("Bật hết");
        //endregion

        edtText.setText(requests.get(randInt(0,24)));
        txtFileName.setText(edtText.getText().toString()+ "_"+count);

        //region .btnStart.
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkPermission()) {

                    String filepath;
                    File file;

                    if (isExternal)
                    {
                        filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/"+ FolderName;
                        file = new File(filepath,FolderName);
                        if(!file.exists()) {
                            file.mkdirs();
                        }
                    }
                    else
                    {
                        filepath = RecordActivity.this.getFilesDir() + "/"+ FolderName;
                        file = new File(filepath,FolderName);
                        if(!file.exists()) {
                            file.mkdirs();
                        }
                    }

                    file = new File(filepath,FileTextName);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    FileName = txtFileName.getText().toString();
                    File tempFile = new File(filepath,FileName);
//
//                    if(tempFile.exists())
//                        tempFile.delete();

                    AudioSavePathInDevice = file.getAbsolutePath() + "/" + FileName + ".wav";

                    // ????
                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();

                        clock.setBase(SystemClock.elapsedRealtime());
                        clock.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

//                    Toast.makeText(RecordActivity.this, "Recording started",
//                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }

            }
        });
        //endregion

        //region .btnStop.
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);

                //timeWhenStopped = clock.getBase() - SystemClock.elapsedRealtime();
                clock.stop();

//                edtText.setText(requests.get(randInt(0,24)));
//                txtFileName.setText(edtText.getText().toString()+ "_"+count);
//                Toast.makeText(RecordActivity.this, "Recording Completed",
//                        Toast.LENGTH_LONG).show();
            }
        });
        //endregion

        //region Play
        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    Log.i("SaveFilePath",AudioSavePathInDevice );
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(RecordActivity.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });
        //endregion

        //region Stop Play
        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
        });
        //endregion

    }

    //region .Save Record function.
    public void SaveRecord(View v)
    {
        clock.setBase(SystemClock.elapsedRealtime()); timeWhenStopped = 0;
        count++;
        edtText.setText(requests.get(randInt(0,24)));
        txtFileName.setText(edtText.getText().toString()+ "_"+count);
        if (mediaRecorder != null)
        {
            MediaRecorderReady();// lưu file ghi âm.
            if (isExternal)
            {
                writeToSDFile(txtFileName.getText().toString());
            }
            else
            {
                WriteToInternal(txtFileName.getText().toString());
            }
        }
        // lưu file Text ở đây.

    }
    //endregion

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    //region .Request Permission.
    private void requestPermission() {
        ActivityCompat.requestPermissions(RecordActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(RecordActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RecordActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    //endregion

    //region .Random Text.
    public void RandomText(View v)
    {
        edtText.setText(requests.get(randInt(0,24)));
        txtFileName.setText(edtText.getText().toString()+ "_"+count);
    }
    //endregion

    //region .Random Int.
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    //endregion

    //region .Write to SD card.
    private void writeToSDFile(String text){
        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();
        Log.i("Write_File", "External file system root: "+root);

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (root + "/"+ FolderName+"/"+ FileTextName);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, FileTextName+ ".txt");

        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(RecordActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(text);
            myOutWriter.append("\n\r");
            myOutWriter.close();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("Write_File", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(RecordActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    public void WriteToInternal(String text)
    {
        File root = RecordActivity.this.getFilesDir();
        Log.i("Write_File", "Internal file system root: "+root);

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (RecordActivity.this.getFilesDir() + "/"+ FolderName+"/"+ FileTextName);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, FileTextName+ ".txt");

        FileOutputStream fos = null;

        try {
            //fos = openFileOutput(file, Context.MODE_APPEND);
            fos = new FileOutputStream(file, true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(text);
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (fos != null) {
                try {
                    //Toast.makeText(this, "Write to " + demoFileName + " successfully!", Toast.LENGTH_LONG).show();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Failed to write!", Toast.LENGTH_LONG).show();
            }
        }
    }
}


