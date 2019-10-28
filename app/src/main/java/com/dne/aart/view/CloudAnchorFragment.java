package com.dne.aart.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dne.aart.R;
import com.dne.aart.util.CloudAnchorManager;
import com.dne.aart.util.StorageManager;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.core.Config;
import com.google.ar.core.Config.CloudAnchorMode;
import com.google.ar.core.Session;
import com.google.ar.core.Anchor.CloudAnchorState;

import java.util.Objects;

public class CloudAnchorFragment extends ArFragment {

    private int expoId;
    private int modelId;
    private TextView tvInfo;
    private Boolean isAdmin;
    private EditText etExpoId;
    private EditText etShortCode;
    private Scene arScene;
    private Button resolveButton;
    private Button clearButton;
    private Button showArtButton;
    private AnchorNode anchorNode;
    private Activity mHostActivity;
    private ModelRenderable modelRenderable;
    private final CloudAnchorManager cloudAnchorManager = new CloudAnchorManager();
    private final StorageManager storageManager = new StorageManager();
    private boolean showingModel = false;


    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    public void onAttach(Context context) {
        super.onAttach(context);
        mHostActivity = (Activity) context;

        if (getArguments() != null) {
            expoId = getArguments().getInt("expoId");
            modelId = getArguments().getInt("modelId");
            isAdmin = getArguments().getBoolean("isAdmin");
        }

        getModel(modelId);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate from the Layout XML file.
        View rootView = inflater.inflate(R.layout.fragment_cloud_anchor, container, false);
        LinearLayout arContainer = rootView.findViewById(R.id.ar_container);

        // Call the ArFragment's implementation to get the AR View.
        View arView = super.onCreateView(inflater, arContainer, savedInstanceState);
        arContainer.addView(arView);

        clearButton = rootView.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> onClearButtonPressed());

        resolveButton = rootView.findViewById(R.id.resolve_button);
        resolveButton.setOnClickListener(v -> onResolveButtonPressed());

        assert getParentFragment() != null;
        showArtButton = Objects.requireNonNull(getParentFragment().getView()).findViewById(R.id.btn_Show_art);

        etExpoId = rootView.findViewById(R.id.et_expo_id);
        etShortCode = rootView.findViewById(R.id.et_resolve_id);

        LinearLayout adminPanel = rootView.findViewById(R.id.admin_panel);

        tvInfo = rootView.findViewById(R.id.tv_info);

        if (!isAdmin) {
            adminPanel.setVisibility(View.GONE);
            tvInfo.setVisibility(View.GONE);
        }

        arScene = getArSceneView().getScene();
        arScene.addOnUpdateListener(frameTime -> cloudAnchorManager.onUpdate());
        setOnTapArPlaneListener((hitResult, plane, motionEvent) -> onArPlaneTap(hitResult));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resolve hosted model automatically on plane detection is soon as session is available.
        if (!isAdmin) {
            while (getArSceneView().getSession() == null) {
                // system log message in case session does not load right away.
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                System.out.println("Session is loading...");
                            }
                        },
                        1000
                );
            }
            onResolveButtonPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    private synchronized void onArPlaneTap(HitResult hitResult) {

        // check if expo ID is provided start hosting the model anchor to cloud on plane tap.
        if (!etExpoId.getText().toString().isEmpty()) {
            hideKeyboard(mHostActivity);
            if (anchorNode != null) {
                // Do nothing if there was already an anchor in the Scene.
                return;
            }
            Anchor anchor = hitResult.createAnchor();
            setNewAnchor(anchor);

            resolveButton.setEnabled(false);

            tvInfo.setText("Now hosting...");

            cloudAnchorManager.hostCloudAnchor(
                    Objects.requireNonNull(getArSceneView().getSession()), anchor, this::onHostedAnchorAvailable);
        } else {
            tvInfo.setText("Please enter Expo ID first");
        }
    }

    private synchronized void onClearButtonPressed() {
        // Clear the anchor from the scene.
        cloudAnchorManager.clearListeners();
        resolveButton.setEnabled(true);
        tvInfo.setText("");
        setNewAnchor(null);
        showingModel = false;
    }

    private synchronized void onResolveButtonPressed() {
        Float shortCode;
        String str = etShortCode.getText().toString();

        // Use provided shortcode for loading model.
        // If no shortcode provided generate it.
        if (!str.isEmpty()) {
            shortCode = Float.valueOf(str);
            int mModelId = Integer.valueOf(str.substring(str.indexOf(".") + 1));
            getModel(mModelId);
        } else {
            int mModelId = modelId;
            int mExpoId = getExpoId();
            shortCode = storageManager.generateShortCode(mExpoId, mModelId);
        }

        onShortCodeEntered(shortCode);

        String cloudAnchorId =
                storageManager.getCloudAnchorId(mHostActivity, shortCode);

        cloudAnchorManager.resolveCloudAnchor(
                Objects.requireNonNull(getArSceneView().getSession()),
                cloudAnchorId,
                anchor -> onResolvedAnchorAvailable(anchor, shortCode));
    }

    // Modify the renderables when a new anchor is available.
    @SuppressLint("SetTextI18n")
    private synchronized void setNewAnchor(@Nullable Anchor anchor) {
        if (anchorNode != null) {
            // If an AnchorNode existed before, remove and nullify it.
            arScene.removeChild(anchorNode);
            anchorNode = null;
        }
        if (anchor != null) {
            if (modelRenderable == null) {
                tvInfo.setText("Model was not loaded.");
                return;
            }
            // Create the Anchor.
            anchorNode = new AnchorNode(anchor);
            arScene.addChild(anchorNode);

            // Create the transformable andy and add it to the anchor.
            TransformableNode andy = new TransformableNode(getTransformationSystem());
            andy.setParent(anchorNode);
            andy.setRenderable(modelRenderable);
            andy.select();
        }
    }

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = super.getSessionConfiguration(session);
        config.setCloudAnchorMode(CloudAnchorMode.ENABLED);
        return config;
    }

    @SuppressLint("SetTextI18n")
    private synchronized void onHostedAnchorAvailable(Anchor anchor) {
        CloudAnchorState cloudState = anchor.getCloudAnchorState();
        if (cloudState == CloudAnchorState.SUCCESS) {
            EditText et = Objects.requireNonNull(getView()).findViewById(R.id.et_expo_id);
            int mExpoId = getExpoId();
            int mModelId = modelId;
            Float shortCode = storageManager.generateShortCode(mExpoId, mModelId);

            storageManager.storeUsingShortCode(mHostActivity, shortCode, anchor.getCloudAnchorId());

            tvInfo.setText("Cloud Anchor Hosted. Short code: " + shortCode
                    + "\n Anchor ID: " + anchor.getCloudAnchorId());

            setNewAnchor(anchor);

        } else tvInfo.setText("Error while hosting: " + cloudState.toString());
    }

    @SuppressLint("SetTextI18n")
    private synchronized void onShortCodeEntered(Float shortCode) {
        String cloudAnchorId = storageManager.getCloudAnchorId(Objects.requireNonNull(getActivity()), shortCode);
        if (cloudAnchorId == null || cloudAnchorId.isEmpty()) {
            tvInfo.setText("A Cloud Anchor ID for the short code " + shortCode + " was not found.");

            return;
        }
        resolveButton.setEnabled(false);
        cloudAnchorManager.resolveCloudAnchor(
                Objects.requireNonNull(getArSceneView().getSession()),
                cloudAnchorId,
                anchor -> onResolvedAnchorAvailable(anchor, shortCode));
    }

    @SuppressLint("SetTextI18n")
    private synchronized void onResolvedAnchorAvailable(Anchor anchor, Float shortCode) {
        CloudAnchorState cloudState = anchor.getCloudAnchorState();
        if (cloudState == CloudAnchorState.SUCCESS) {


            tvInfo.setText("Cloud Anchor Resolved. Short code: " + shortCode);

            setNewAnchor(anchor);

            showingModel = true;
        } else {
            tvInfo.setText("Error while resolving anchor with short code "
                    + shortCode
                    + ". Error: "
                    + cloudState.toString());

            resolveButton.setEnabled(true);
        }
    }

    private void getModel(int modelId) {
        String modelName;
        switch (modelId) {
            case 1:
                modelName = "green_man.sfb";
                break;
            case 2:
                modelName = "scene.sfb";
                break;
            case 3:
                modelName = "deer_test.sfb";
                break;
            case 4:
                modelName = "angel.sfb";
                break;
            case 5:
                modelName = "lowpolytree.sfb";
                break;
            case 6:
                modelName = "skull_two.sfb";
                break;
            default:
                modelName = "green_man.sfb";
        }
        Uri modelUri = Uri.parse(modelName);
        ModelRenderable.builder()
                .setSource(mHostActivity, modelUri)
                .build()
                .thenAccept(renderable -> modelRenderable = renderable);
    }

    private int getExpoId() {
        int id;
        if (isAdmin) {
            id = Integer.valueOf(etExpoId.getText().toString());
        } else id = expoId;
        return id;
    }

    // Hide the soft keyboard
    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }
}
