package com.desarrollador.easytour.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.desarrollador.easytour.R;


/**
 * Clase de utilidad para acceder a los permisos de tiempo de ejecución.
 * Created by José Andrés Cartuche on 06/02/2018.
 */

public abstract class PermissionUtils {

    /**
     * Código de petición para solicitar permiso de camara.
     */
    private static final int CAMERA_PERMISSION_REQUEST_ID = 100;

    /**
     * Código de petición para solicitar permiso de lectura/escritura en el almacenamiento externo.
     */
    private static final int LOCATION_PERMISSION_REQUEST_ID = 101;

    /**
     * Requests the fine location permission. If a rationale with an additional explanation should
     * be shown to the user, displays a dialog that triggers the request.
     */
    public static void requestPermission(AppCompatActivity activity, int requestId,
                                         String permission, boolean finishActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Display a dialog with rationale.
            int idMessage = 0;
            int idPermissionName = 0;
            switch (requestId)
            {
                case CAMERA_PERMISSION_REQUEST_ID:
                    idMessage = R.string.permission_rationale_camera;
                    idPermissionName = R.string.permission_camera_name;
                    break;
                case LOCATION_PERMISSION_REQUEST_ID:
                    idMessage = R.string.permission_rationale_location;
                    idPermissionName = R.string.permission_location_name;
                    break;
            }
            //String message = getString(idMessage);
            PermissionUtils.RationaleDialog.newInstance(requestId, finishActivity, permission, idMessage, idPermissionName)
                    .show(activity.getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestId);
        }
    }

    /**
     * Checks if the result contains a {@link PackageManager#PERMISSION_GRANTED} result for a
     * permission from a runtime permissions request.
     *
     * @see ActivityCompat.OnRequestPermissionsResultCallback
     */
    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                              String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    /**
     * Un diálogo que muestra un mensaje de permiso denegado.
     */
    public static class PermissionDeniedDialog extends DialogFragment {

        private static final String ARGUMENT_FINISH_ACTIVITY = "finish";

        private static final String ARGUMENT_MESSAGE_DIALOG = "idMessage";

        private static final String ARGUMENT_PERMISSION_NAME = "idPermissionName";

        private boolean mFinishActivity = false;

        /**
         * Crea una nueva instancia de este cuadro de diálogo y opcionalmente finaliza la
         * actividad de llamada cuando se hace clic en el botón 'Aceptar'.
         */
        public static PermissionDeniedDialog newInstance(boolean finishActivity, int idMessage, int idPermissionName) {
            Bundle arguments = new Bundle();
            arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);
            arguments.putInt(ARGUMENT_MESSAGE_DIALOG, idMessage);
            arguments.putInt(ARGUMENT_PERMISSION_NAME, idPermissionName);
            PermissionDeniedDialog dialog = new PermissionDeniedDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        public static PermissionDeniedDialog newInstance(boolean finishActivity, int idMessage) {
            return PermissionDeniedDialog.newInstance(false, idMessage, -1);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            mFinishActivity = arguments.getBoolean(ARGUMENT_FINISH_ACTIVITY);
            int idMessage = arguments.getInt(ARGUMENT_MESSAGE_DIALOG);
            Resources res = getResources();
            String sMessage = res.getString(idMessage);
            String sActivatePerm = res.getString(R.string.suggestion_activate_permissions);

            String messageDialog = sMessage.concat(sActivatePerm);

            return new AlertDialog.Builder(getActivity())
                    .setMessage(messageDialog)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (mFinishActivity) {
                Bundle arguments = getArguments();
                int idPermissionName = arguments.getInt(ARGUMENT_PERMISSION_NAME);
                if(idPermissionName != -1)
                {
                    Resources res = getResources();
                    String sPermission = res.getString(idPermissionName);
                    String sPermissionRequired = res.getString(R.string.permission_required_toast);
                    String textToast = String.format(sPermissionRequired, sPermission);
                    Toast.makeText(getActivity(), textToast,
                            Toast.LENGTH_SHORT).show();
                }
                this.getActivity().finish();
            }
        }
    }

    /**
     * A dialog that explains the use of the permission and requests the necessary permission.
     * <p>
     * The activity should implement
     * {@link ActivityCompat.OnRequestPermissionsResultCallback}
     * to handle permit or denial of this permission request.
     */
    public static class RationaleDialog extends DialogFragment {

        private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";

        private static final String ARGUMENT_FINISH_ACTIVITY = "finish";

        private static final String ARGUMENT_PERMISSION = "permission";

        private static final String ARGUMENT_MESSAGE_DIALOG = "idMessage";

        private static final String ARGUMENT_PERMISSION_NAME = "idPermissionName";

        private boolean mFinishActivity = false;

        /**
         * Creates a new instance of a dialog displaying the rationale for the use of the
         * permission.
         * <p>
         * The permission is requested after clicking 'ok'.
         *
         * @param requestCode    Id of the request that is used to request the permission. It is
         *                       returned to the
         *                       {@link ActivityCompat.OnRequestPermissionsResultCallback}.
         * @param finishActivity Whether the calling Activity should be finished if the dialog is
         *                       cancelled.
         * @param idMessage
         * @param idPermissionName
         */
        public static RationaleDialog newInstance(int requestCode, boolean finishActivity, String permission, int idMessage, int idPermissionName) {
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
            arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);
            arguments.putString(ARGUMENT_PERMISSION, permission);
            arguments.putInt(ARGUMENT_MESSAGE_DIALOG, idMessage);
            arguments.putInt(ARGUMENT_PERMISSION_NAME, idPermissionName);
            RationaleDialog dialog = new RationaleDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);
            mFinishActivity = arguments.getBoolean(ARGUMENT_FINISH_ACTIVITY);
            final String permission = arguments.getString(ARGUMENT_PERMISSION);
            int idMessage = arguments.getInt(ARGUMENT_MESSAGE_DIALOG);

            return new AlertDialog.Builder(getActivity())
                    .setMessage(idMessage)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // After click on Ok, request the permission.
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{permission},
                                    requestCode);
                            // Do not finish the Activity while requesting permission.
                            mFinishActivity = false;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (mFinishActivity) {
                Bundle arguments = getArguments();
                int idPermissionName = arguments.getInt(ARGUMENT_PERMISSION_NAME);
                Resources res = getResources();
                String sPermission = res.getString(idPermissionName);
                String sPermissionRequired = res.getString(R.string.permission_required_toast);
                String textToast = String.format(sPermissionRequired, sPermission);
                Toast.makeText(getActivity(),
                        textToast,
                        Toast.LENGTH_SHORT)
                        .show();
                getActivity().finish();
            }
        }
    }
}