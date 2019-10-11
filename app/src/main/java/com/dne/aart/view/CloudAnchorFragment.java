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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText editText;
    private Scene arScene;
    private Button resolveButton;
    private Button clearButton;
    private Button showArtButton;
    private AnchorNode anchorNode;
    private Activity mHostActivity;
    private ModelRenderable modelRenderable;
    private final CloudAnchorManager cloudAnchorManager = new CloudAnchorManager();
    private final StorageManager storageManager = new StorageManager();


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

        Uri modelUri = getModel(modelId);
        ModelRenderable.builder()
                .setSource(context, modelUri)
                .build()
                .thenAccept(renderable -> modelRenderable = renderable);
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

        showArtButton = getParentFragment().getView().findViewById(R.id.btn_Show_art);

        editText = rootView.findViewById(R.id.et_resolve_id);

        LinearLayout adminPanel = rootView.findViewById(R.id.admin_panel);

        tvInfo = rootView.findViewById(R.id.tv_info);

        if (!isAdmin) {
            adminPanel.setVisibility(View.GONE);
            /*clearButton.setVisibility(View.GONE);
            resolveButton.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);*/
            tvInfo.setVisibility(View.GONE);
            showArtButton.setOnClickListener(v -> onResolveButtonPressed());
        }

        arScene = getArSceneView().getScene();
        arScene.addOnUpdateListener(frameTime -> cloudAnchorManager.onUpdate());
        setOnTapArPlaneListener((hitResult, plane, motionEvent) -> onArPlaneTap(hitResult));
        return rootView;
    }

    @SuppressLint("SetTextI18n")
    private synchronized void onArPlaneTap(HitResult hitResult) {
        if (anchorNode != null) {
            // Do nothing if there was already an anchor in the Scene.
            return;
        }
        Anchor anchor = hitResult.createAnchor();
        setNewAnchor(anchor);

        resolveButton.setEnabled(false);

        tvInfo.setText("Now hosting...");

        cloudAnchorManager.hostCloudAnchor(
                getArSceneView().getSession(), anchor, this::onHostedAnchorAvailable);
    }

    private synchronized void onClearButtonPressed() {
        // Clear the anchor from the scene.
        cloudAnchorManager.clearListeners();
        resolveButton.setEnabled(true);
        tvInfo.setText("");
        setNewAnchor(null);
    }

    private synchronized void onResolveButtonPressed() {

        //String shortCode = editText.getText().toString();
        //int shortCode = Integer.valueOf(editText.getText().toString());
        int mModelId = modelId;
        int mExpoId = getExpoId();

        Float shortCode = storageManager.generateShortCode(mExpoId, mModelId);
        onShortCodeEntered(shortCode);
        String cloudAnchorId =
                storageManager.getCloudAnchorId(mHostActivity, shortCode);

        cloudAnchorManager.resolveCloudAnchor(
                getArSceneView().getSession(),
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
                // Display an error message if the renderable model was not available.
                //Toast toast = Toast.makeText(getContext(), "Andy model was not loaded.", Toast.LENGTH_LONG);
                //toast.setGravity(Gravity.CENTER, 0, 0);
                //toast.show();
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
            //int shortCode = storageManager.nextShortCode(mHostActivity);
            EditText et = getView().findViewById(R.id.et_expo_id);
            int mExpoId = getExpoId();
            int mModelId = modelId;
            Float shortCode = storageManager.generateShortCode(mExpoId, mModelId);

            storageManager.storeUsingShortCode(mHostActivity, shortCode, anchor.getCloudAnchorId());
            //storageManager.saveWithAnchorID(mHostActivity, anchor.getCloudAnchorId());

            tvInfo.setText("Cloud Anchor Hosted. Anchor ID: " + anchor.getCloudAnchorId());
            //tvInfo.setText("Cloud Anchor Hosted. Short code: " + shortCode);
            //editText.setText(shortCode, EditText.BufferType.EDITABLE);
            Log.d("ANCHOR", anchor.getCloudAnchorId());

            Toast.makeText(getContext(),
                    "Cloud Anchor Hosted. Short code: " + shortCode,
                    Toast.LENGTH_LONG).show();

            setNewAnchor(anchor);

        } else tvInfo.setText("Error while hosting: " + cloudState.toString());
    }

    @SuppressLint("SetTextI18n")
    private synchronized void onShortCodeEntered(Float shortCode) {
        String cloudAnchorId = storageManager.getCloudAnchorId(getActivity(), shortCode);
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
            Log.d("SHORTCODE", "Cloud Anchor Resolved. Short code: " + shortCode);

            setNewAnchor(anchor);
        } else {
            tvInfo.setText("Error while resolving anchor with short code "
                    + shortCode
                    + ". Error: "
                    + cloudState.toString());

            resolveButton.setEnabled(true);
        }
    }

    private Uri getModel(int modelId) {
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
        Uri uriModel = Uri.parse(modelName);
        return uriModel;
    }

    private int getExpoId(){
        int id;
        if (isAdmin) {
            if (!editText.getText().toString().isEmpty()) {
                id = Integer.valueOf(editText.getText().toString());
            } else id = 0;
        }
        else id = expoId;
        return id;
    }
}
