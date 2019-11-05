# A.Art

A new form of digital art exhibition.

Android mobile application - presenting/viewing 3D digital art in AR space. 

#### Technologies:
Sceneform | ARCore Cloud Anchors | Kotlin | Android Studio | Google Map API


### What is A.Art?

- Purpose of A.Art is to bring AR ( Augmented Reality ) to use for digital art exhibitions.
- You can present your 3D art in AR space locked on location with ARCore Cloud Anchors.
- You can view list of different exhibitions, their locations and how far they are from your location.
- You can get a closer look at expo location, view music broadcast radius on map and recieve notification when you are close.
- You can choose different 3D art models of the expo and view them in AR space through your phone camera.


### Target user group for A.Art:

- Artists or expo orginizers who want to present 3D digital art/models in AR space.
- People who want to have new digital art expo experience with AR.


# App demo:
### Presenting/placing 3D model (admin)

- Go to prefered presentation area
- Log in as admin
- Select model from the list
- Enter exhibition number
- Move camera for plane/surface detection
- Place model in AR space by tapping on detected plane
- Wait for app to give you the Short code after hosting an ARCore Cloud Anchor attached to your model in the cloud. 
- You can test you hosted model by clearing it and resolving it again using Short Code provided to you.

![alt text][admin]
---

### Browsing Exhibitions

- View list of exhibitions and their distance
- Open expo for details such as description and location
- Open detailed location to view your own location, digital art location and music streaming area radius on the map

![alt text][expo]
---

### Viewing 3D model arts in AR space

- Select different models from expo's model list
- Move your camera around to detect surface/plane
- Model appears as soon as you find the right spot
- You can move around the model and enjoy the beauty of AR :)

![alt text][art]



[admin]: https://github.com/hamedshahidi/A.Art/blob/master/demo/aart_admin.gif "Presenting/placing 3D model"
[expo]: https://github.com/hamedshahidi/A.Art/blob/master/demo/aart_user_expo.gif "Browsing Exhibitions"
[art]: https://github.com/hamedshahidi/A.Art/blob/master/demo/aart_user_art.gif "Viewing 3D model arts in AR space"







