package com.ymg.beststatusandquotes.Activity;

import static androidx.core.content.FileProvider.getUriForFile;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yalantis.ucrop.UCrop;
import com.ymg.beststatusandquotes.R;

import java.io.File;
import java.util.List;

public class ImagePickerActivity extends AppCompatActivity {
    private static final String TAG = ImagePickerActivity.class.getSimpleName();
    public static final String INTENT_IMAGE_PICKER_OPTION = "image_picker_option";
    public static final String INTENT_ASPECT_RATIO_X = "aspect_ratio_x";
    public static final String INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y";
    public static final String INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio";
    public static final String INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality";
    public static final String INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height";
    public static final String INTENT_BITMAP_MAX_WIDTH = "max_width";
    public static final String INTENT_BITMAP_MAX_HEIGHT = "max_height";


    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_GALLERY_IMAGE = 1;

    private boolean lockAspectRatio = false, setBitmapMaxWidthHeight = false;
    private int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 1000, bitmapMaxHeight = 1000;
    private int IMAGE_COMPRESSION = 80;
    public static String fileName;

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    cropImage(getCacheImagePath(fileName));
                } else {
                    setResultCancelled();
                }
            });

    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    cropImage(imageUri);
                } else {
                    setResultCancelled();
                }
            });

    ActivityResultLauncher<Intent> uCropActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    handleUCropResult(result.getData());
                } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(result.getData());
                    Log.e(TAG, "Crop error: " + cropError);
                    setResultCancelled();
                } else {
                    setResultCancelled();
                }
            });

    public interface PickerOptionListener {
        void onTakeCameraSelected();

        void onChooseGallerySelected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_image_intent_null), Toast.LENGTH_LONG).show();
            return;
        }

        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X);
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y);
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION);
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false);
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false);
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth);
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight);

        int requestCode = intent.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeCameraImage();
        } else {
            chooseImageFromGallery();
        }
    }

    public static void showImagePickerOptions(Context context, PickerOptionListener listener) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.lbl_set_profile_photo));

        // add a list
        String[] animals = {context.getString(R.string.lbl_take_camera_picture), context.getString(R.string.lbl_choose_from_gallery)};
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    listener.onTakeCameraSelected();
                    break;
                case 1:
                    listener.onChooseGallerySelected();
                    break;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void takeCameraImage() {
        if (checkPermission()) {
            fileName = System.currentTimeMillis() + ".jpg";
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                cameraActivityResultLauncher.launch(takePictureIntent);
            }
        } else {
            requestPermission();
        }
    }

    private void chooseImageFromGallery() {
        if (checkPermission()) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryActivityResultLauncher.launch(pickPhoto);
        } else {
            Log.d(TAG, "chooseImageFromGallery: " + "requestPermission : " + checkPermission());
            requestPermission();
        }
    }


    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted && writeStorageAccepted) {
                    // you can do your work now
                } else {
                    Toast.makeText(this, "Permission Denied, You cannot use local drive and camera. Please grant permissions.", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            showMessageOKCancel("You need to allow access to the permissions",
                                    (dialog, which) -> {
                                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                PERMISSION_REQUEST_CODE);
                                    });
                        }
                    }
                }
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ImagePickerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: " + requestCode);
//        switch (requestCode) {
//            case REQUEST_IMAGE_CAPTURE:
//                if (resultCode == RESULT_OK){
//                    cropImage(getCacheImagePath(fileName));
//                } else {
//                    setResultCancelled();
//                }
//                break;
//            case REQUEST_GALLERY_IMAGE:
//                if (resultCode == RESULT_OK){
//                    Uri imageUri = data.getData();
//                    cropImage(imageUri);
//                } else {
//                    setResultCancelled();
//                }
//                break;
//            case UCrop.REQUEST_CROP:
//                if (resultCode == RESULT_OK){
//                    handleUCropResult(data);
//                } else {
//                    setResultCancelled();
//                }
//                break;
//            case UCrop.RESULT_ERROR:
//                final Throwable cropError = UCrop.getError(data);
//                Log.e(TAG, "Crop error: " + cropError);
//                setResultCancelled();
//                break;
//            default:
//                setResultCancelled();
//        }
//    }

    private void cropImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);

        // Set colors for toolbar, status bar and widget (optional)
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorAccent));

        if (lockAspectRatio)
            options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);

        if (setBitmapMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);

        uCropActivityResultLauncher.launch(UCrop.of(sourceUri, destinationUri).withOptions(options).getIntent(this));
    }


    private void handleUCropResult(Intent data) {
        if (data == null) {
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        setResultOk(resultUri);
    }

    private void setResultOk(Uri imagePath) {
        Intent intent = new Intent();
        intent.putExtra("path", imagePath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(ImagePickerActivity.this, getPackageName() + ".provider", image);
    }

    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
    public static void clearCache(Context context) {
        File path = new File(context.getExternalCacheDir(), "camera");
        if (path.exists() && path.isDirectory()) {
            for (File child : path.listFiles()) {
                child.delete();
            }
        }
    }
}