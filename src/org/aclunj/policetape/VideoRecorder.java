package org.aclunj.policetape;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class VideoRecorder extends SurfaceView implements SurfaceHolder.Callback{

 MediaRecorder recorder;
 SurfaceHolder holder;
  String path;
  Context c;
  Camera mCamera;
  
  /**
   * Creates a new audio recording at the given path (relative to root of SD card).
   */
  public VideoRecorder(Context con, AttributeSet attrs) {
      super(con, attrs);
      c = con;
      
      holder = getHolder();
      holder.addCallback(this);
      holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
      //holder.lockCanvas().rotate(90);
      //recorder.
      
      

  }
  
  public static Camera getCameraInstance(){
	  Camera c = null;
	  try{
		  c = Camera.open();
	  }
	  catch(Exception e){
		  System.out.println("hmmmm");
	  }
	  return c;
	  
  }
  
  public void setPath(String patha) {
      try {
          path = sanitizePath(patha);
          File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/rpath.txt");
          f.delete();
          f.createNewFile();
          FileOutputStream fOut = new FileOutputStream(f);
          OutputStreamWriter osw = new OutputStreamWriter(fOut); 
          osw.write(path);
          osw.flush();
          osw.close();
      }catch(IOException e) {
          e.printStackTrace();
      }
  }

  private String sanitizePath(String path) {
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    if (!path.contains(".")) {
      path += ".mp4";
    }
    return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
  }

  /**
   * Starts a new recording.
   */
  //@SuppressLint({ "NewApi", "NewApi" })
public void start(Context c) throws IOException {
    String state = android.os.Environment.getExternalStorageState();
    if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
        throw new IOException("SD Card is not mounted.  It is " + state + ".");
    }

    // make sure the directory we plan to store the recording in exists
    File directory = new File(path).getParentFile();
    if (!directory.exists() && !directory.mkdirs()) {
      throw new IOException("Path to file could not be created.");
    }

    WindowManager mWinMgr = (WindowManager)c.getSystemService(Context.WINDOW_SERVICE);
    int displayWidth = mWinMgr.getDefaultDisplay().getWidth();

	mCamera = getCameraInstance();
    mCamera.setDisplayOrientation(90);
    recorder = new MediaRecorder();
    recorder.setCamera(mCamera);

    // Step 1: Unlock and set camera to MediaRecorder
    mCamera.unlock();
    recorder.setCamera(mCamera);

    // Step 2: Set sources
    recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
    recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

    // Step 4: Set output file
    //mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

    // Step 5: Set the preview output
    //mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

    // Step 6: Prepare configured MediaRecorder

    recorder.setOutputFile(path);
    Surface s = holder.getSurface();
    recorder.setPreviewDisplay(s);
    
    recorder.prepare();
    recorder.start();
      }

  /**
   * Stops a recording that has been previously started.
   */
  public void stop() throws IOException {
    try {
        recorder.stop();
        recorder.release();
    }
    catch(Exception e) {
    }
  }
  
  public String getPath() {
      return path;
  }
  
public void surfaceChanged(SurfaceHolder sholder, int format, int width,
        int height) {
    // TODO Auto-generated method stub
}

public void surfaceCreated(SurfaceHolder holder) {
    // TODO Auto-generated method stub
    
}

public void surfaceDestroyed(SurfaceHolder holder) {
    // TODO Auto-generated method stub
    
}

}
