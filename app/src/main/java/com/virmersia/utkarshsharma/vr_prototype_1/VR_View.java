package com.virmersia.utkarshsharma.vr_prototype_1;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.wikitude.WikitudeSDK;
import com.wikitude.WikitudeSDKStartupConfiguration;
import com.wikitude.camera.CameraManagerListener;
import com.wikitude.common.camera.CameraSettings;
import com.wikitude.*;

import com.wikitude.common.rendering.RenderExtension;
import com.wikitude.rendering.*;
import com.wikitude.rendering.InternalRendering;

import java.sql.Driver;

public class VR_View extends Activity implements ExternalRendering, AdapterView.OnItemSelectedListener,CameraManagerListener{

    private static final String TAG = "CameraControlsActivity";

    private WikitudeSDK _wikitudeSDK;

    private Driver _driver;
String WIKITUDE_SDK_KEY="DQZ93pZtU/UpSd0HMjFZBmZBHp2g1cAIZx97+mXtRFs/uQqNPBijB0Jx2ABu8h2x5PuPuI9VEufB/I2EIjWzZG9O3PcwwC49Ws2+zUSMYJ7nZKAvb4PcTlpE8jkhgVo532sd7OgMG07r2/hml+rJ2VVYXTl2AFIBPzj3SEFcMl1TYWx0ZWRfXxzCvr/xYQfWzgUAad9Bwl9fE1xtsyXS0hK7wBP+XG2+R0VAJY2rQrNJPw0wL8Uw35/h5PFeK4UYIYiuBWvzeSSDd2eViG5hOXWEY3s5DMPeVXsqHbEJMDPgMSZlyxC+LiV7f5T8APmGMENMjUUWUs6mo/5ED2Tlx48jkWTn36jRd1mnzke/7soJbhwXeb+g5khGqOTfUJM8dxaSS11/jm2Hi9vJ2CsvXDJOew4givLaVnti9Sh3w2CVjfWNT2t7M4FtzqJbTtHy1SCNzhY6jK1mNsv489mpIbpOr/bl0Llm9AKNw07rPOeXagJRIM9G2FPoEgg50P8ehMcgaaTXo/0f+YXGuxwkD5hZKHsEajVUw4VERQsIg/VMC44Z/bob1v0p1pgBGdp+ZtL+5uRO6RJCFihonR0nK3jsxKGCSXbCsT2KchfEJWtl4jitLV+UGISLe0lZ3y+iI1DdkzE+RuThH2xeRYCTbuMYmzo5TjymKSAWdcgJh/U=";
    private boolean _isCameraOpen;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _wikitudeSDK = new WikitudeSDK(this);
        _wikitudeSDK.getCameraManager().setListener(this);
        WikitudeSDKStartupConfiguration startupConfiguration = new WikitudeSDKStartupConfiguration(WIKITUDE_SDK_KEY, CameraSettings.CameraPosition.BACK, CameraSettings.CameraFocusMode.CONTINUOUS);

        _wikitudeSDK.onCreate(getApplicationContext(), this, startupConfiguration);
    }

   

    @Override
    public void onRenderExtensionCreated(final RenderExtension renderExtension_) {

    }

    @Override
    public void onItemSelected(final AdapterView<?> adapterView_, final View view_, final int position_, final long id_) {
        if (_isCameraOpen) {
            switch (adapterView_.getId()) {
                case R.id.focusMode:
                    if (position_ == 0) {
                        _wikitudeSDK.getCameraManager().setFocusMode(CameraSettings.CameraFocusMode.CONTINUOUS);
                    } else {
                        _wikitudeSDK.getCameraManager().setFocusMode(CameraSettings.CameraFocusMode.ONCE);
                    }
                    break;
                case R.id.cameraPosition:
                    if (position_ == 0) {
                        _wikitudeSDK.getCameraManager().setCameraPosition(CameraSettings.CameraPosition.BACK);
                    } else {
                        _wikitudeSDK.getCameraManager().setCameraPosition(CameraSettings.CameraPosition.FRONT);
                    }
                    break;
            }
        } else {
            Log.e("CAMERA_OPEN", "camera is not open");
        }
    }

    @Override
    public void onNothingSelected(final AdapterView<?> adapterView_) {

    }

@Override
    public void onCameraOpen(Camera camera) {

        if (!_isCameraOpen) {
            FrameLayout viewHolder = new FrameLayout(getApplicationContext());
            setContentView(viewHolder);



            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            LinearLayout controls = (LinearLayout) inflater.inflate(R.layout.activity_vr__view, null);
            viewHolder.addView(controls);

            Spinner cameraPositionSpinner = (Spinner) findViewById(R.id.cameraPosition);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.camera_positions, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cameraPositionSpinner.setAdapter(adapter);
            cameraPositionSpinner.setOnItemSelectedListener(this);

            Spinner focusModeSpinner = (Spinner) findViewById(R.id.focusMode);
            adapter = ArrayAdapter.createFromResource(this, R.array.focus_mode, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            focusModeSpinner.setAdapter(adapter);
            focusModeSpinner.setOnItemSelectedListener(this);

            Switch flashToggleButton = (Switch) findViewById(R.id.flashlight);
            flashToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                    if (isChecked) {
                        _wikitudeSDK.getCameraManager().enableCameraFlashLight();
                    } else {
                        _wikitudeSDK.getCameraManager().disableCameraFlashLight();
                    }

                }
            });

            SeekBar zoomSeekBar = (SeekBar) findViewById(R.id.zoomSeekBar);
            zoomSeekBar.setMax(((int) _wikitudeSDK.getCameraManager().getMaxZoomLevel()) * 100);
            zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                    if (progress > 0) {
                        _wikitudeSDK.getCameraManager().setZoomLevel((float) progress / 100.0f);
                    }
                }

                @Override
                public void onStartTrackingTouch(final SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {

                }
            });
        }
        _isCameraOpen = true;
    }

    @Override
    public void onCameraReleased() {
    }

    @Override
    public void onCameraOpenFailure() {
    }
}
