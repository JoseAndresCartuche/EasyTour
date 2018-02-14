package com.desarrollador.easytour;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;


public class DetectedActivitiesIntentService extends IntentService {

    private static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();

    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Lo primero es obtener el resultado de reconocimiento.
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Luego, obtén actividad más probable.
        DetectedActivity detectedActivity = result.getMostProbableActivity();

        Log.d(TAG, "Actividad dectectada: Tipo " +
                Constants.getStringActivity(detectedActivity.getType()) +
                " - Confianza " + detectedActivity.getConfidence());

        int type = detectedActivity.getType();

        if (DetectedActivity.ON_FOOT == type) {
            type = walkingOrRunning(result.getProbableActivities());
        } else if (DetectedActivity.IN_VEHICLE == type) {
            type = walkingOrRunning(result.getProbableActivities());
        }
        // Ahora, crea un intent con una acción personalizada.
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

        // El siguiente paso, es poner las actividades en el intent
        localIntent.putExtra(Constants.ACTIVITY_KEY, type);

        // Y finalmente envía los datos hacia la actividad
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private int walkingOrRunning(List<DetectedActivity> probableActivities) {
        int walkActivity = 0, runningActivity = 0, onFOOT=0, onBICYCLE=0, inVEHICLE=0, still=0, desconocida=0;

        for (DetectedActivity probableActivity : probableActivities) {
/*
            switch (probableActivity.getType()){
                case DetectedActivity.WALKING:
                case DetectedActivity.ON_FOOT:
                    walkActivity= probableActivity.getConfidence();
                    onFOOT= walkActivity;
                    return (walkActivity >= onFOOT) ? DetectedActivity.WALKING : DetectedActivity.ON_FOOT;
                case DetectedActivity.RUNNING:
                    runningActivity= probableActivity.getConfidence();
                    return (runningActivity >= 0) ? DetectedActivity.UNKNOWN : DetectedActivity.RUNNING;
                case DetectedActivity.STILL:
                    still= probableActivity.getConfidence();
                    return (still >= 0) ? DetectedActivity.UNKNOWN : DetectedActivity.STILL;
                case DetectedActivity.IN_VEHICLE:
                    inVEHICLE= probableActivity.getConfidence();
                    return (inVEHICLE >= 0) ? DetectedActivity.UNKNOWN : DetectedActivity.IN_VEHICLE;
                case DetectedActivity.ON_BICYCLE:
                    onBICYCLE= probableActivity.getConfidence();
                    return (onBICYCLE >= 0) ? DetectedActivity.UNKNOWN : DetectedActivity.ON_BICYCLE;
                case DetectedActivity.UNKNOWN:
                    desconocida= probableActivity.getConfidence();
                    return (desconocida >= 0) ? DetectedActivity.UNKNOWN : DetectedActivity.STILL;

            }
*/
            if (probableActivity.getType() == DetectedActivity.WALKING) {
                walkActivity = probableActivity.getConfidence();
            } else if (probableActivity.getType() == DetectedActivity.RUNNING) {
                runningActivity = probableActivity.getConfidence();
            } else if (probableActivity.getType() == DetectedActivity.ON_BICYCLE) {
                onBICYCLE = probableActivity.getConfidence();
            } else if (probableActivity.getType() == DetectedActivity.IN_VEHICLE) {
                inVEHICLE = probableActivity.getConfidence();
            } else if (probableActivity.getType() == DetectedActivity.STILL) {
                still = probableActivity.getConfidence();
            } else if (probableActivity.getType() == DetectedActivity.UNKNOWN) {
                desconocida = probableActivity.getConfidence();
            }
        }
/*
        if (inVEHICLE == 0) {
            return DetectedActivity.IN_VEHICLE;
        } else if (onBICYCLE == 1) {
            return DetectedActivity.ON_BICYCLE;
        } else if (still == 3) {
            return DetectedActivity.STILL;
        } else if (desconocida == 4) {
            return DetectedActivity.UNKNOWN;
        } else if (walkActivity == 7) {
            return DetectedActivity.WALKING;
        } else if (runningActivity == 8) {
            return DetectedActivity.RUNNING;
        }
*/
        return (walkActivity >= runningActivity) ? DetectedActivity.WALKING : DetectedActivity.RUNNING;
    }

}
