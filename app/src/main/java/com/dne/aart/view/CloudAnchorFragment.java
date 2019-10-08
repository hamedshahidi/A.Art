package com.dne.aart.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.dne.aart.R;
import com.dne.aart.util.CloudAnchorManager;
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

public class CloudAnchorFragment extends ArFragment {

    private Scene arScene;
    private AnchorNode anchorNode;
    private ModelRenderable andyRenderable;
    private final CloudAnchorManager cloudAnchorManager = new CloudAnchorManager();

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    public void onAttach(Context context) {
        super.onAttach(context);
        Uri modelUri = Uri.parse("green_man.sfb");
        ModelRenderable.builder()
                .setSource(context, modelUri)
                .build()
                .thenAccept(renderable -> andyRenderable = renderable);
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

        Button clearButton = rootView.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> onClearButtonPressed());

        arScene = getArSceneView().getScene();
        arScene.addOnUpdateListener(frameTime -> cloudAnchorManager.onUpdate());
        setOnTapArPlaneListener((hitResult, plane, motionEvent) -> onArPlaneTap(hitResult));
        return rootView;
    }

    private synchronized void onArPlaneTap(HitResult hitResult) {
        if (anchorNode != null) {
            // Do nothing if there was already an anchor in the Scene.
            return;
        }
        Anchor anchor = hitResult.createAnchor();
        setNewAnchor(anchor);

        Toast.makeText(getContext(), "Now hosting...", Toast.LENGTH_LONG).show();
        cloudAnchorManager.hostCloudAnchor(
                getArSceneView().getSession(), anchor, this::onHostedAnchorAvailable);
    }

    private synchronized void onClearButtonPressed() {
        // Clear the anchor from the scene.
        cloudAnchorManager.clearListeners();
        setNewAnchor(null);
    }

    // Modify the renderables when a new anchor is available.
    private synchronized void setNewAnchor(@Nullable Anchor anchor) {
        if (anchorNode != null) {
            // If an AnchorNode existed before, remove and nullify it.
            arScene.removeChild(anchorNode);
            anchorNode = null;
        }
        if (anchor != null) {
            if (andyRenderable == null) {
                // Display an error message if the renderable model was not available.
                Toast toast = Toast.makeText(getContext(), "Andy model was not loaded.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            // Create the Anchor.
            anchorNode = new AnchorNode(anchor);
            arScene.addChild(anchorNode);

            // Create the transformable andy and add it to the anchor.
            TransformableNode andy = new TransformableNode(getTransformationSystem());
            andy.setParent(anchorNode);
            andy.setRenderable(andyRenderable);
            andy.select();
        }
    }

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = super.getSessionConfiguration(session);
        config.setCloudAnchorMode(CloudAnchorMode.ENABLED);
        return config;
    }

    private synchronized void onHostedAnchorAvailable(Anchor anchor) {
        CloudAnchorState cloudState = anchor.getCloudAnchorState();
        if (cloudState == CloudAnchorState.SUCCESS) {
            Toast.makeText(getContext(),
                    "Cloud Anchor Hosted. ID: " + anchor.getCloudAnchorId(),
                    Toast.LENGTH_LONG).show();
            setNewAnchor(anchor);
        } else {
            Toast.makeText(getContext(),
                    "Error while hosting: " + cloudState.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
