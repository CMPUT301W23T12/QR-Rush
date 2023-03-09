package com.example.qrrush;

import static androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.mlkit.vision.MlKitAnalyzer;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.Collections;
import java.util.List;

/**
 * The fragment which opens the camera, scanning for a QR code. Upon scanning one, we move to the
 * screen which shows the user what they've scanned.
 */
public class CameraFragment extends Fragment {
    PreviewView previewView;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    CameraController cameraController;
    User user;

    public CameraFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        BarcodeScanner barcodeScanner = BarcodeScanning.getClient(options);

        ImageAnalysis analysis = new ImageAnalysis.Builder().build();
        analysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()),
                new MlKitAnalyzer(Collections.singletonList(barcodeScanner), COORDINATE_SYSTEM_ORIGINAL,
                        ContextCompat.getMainExecutor(requireContext()), (result) -> {
                    List<Barcode> results = result.getValue(barcodeScanner);
                    if (results == null) {
                        return;
                    }

                    for (Barcode b : results) {
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_view, new QRConfirmFragment(user, b)).commit();
                    }
                }));

        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraController = new LifecycleCameraController(requireContext());
        previewView = result.findViewById(R.id.camera_view);
        previewView.setController(cameraController);

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                bindImageAnalysis(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));

        return result;
    }
}
