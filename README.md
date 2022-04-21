# ESRC Extension Marvrus for Android

[![Platform](https://img.shields.io/badge/platform-android-orange.svg)](https://github.com/esrc-official/ESRC-Android-Extension-Marvrus)
[![Languages](https://img.shields.io/badge/language-java-orange.svg)](https://github.com/esrc-official/ESRC-Android-Extension-Marvrus)
[![Commercial License](https://img.shields.io/badge/license-Commercial-brightgreen.svg)](https://github.com/esrc-official/ESRC-Android-Extension-Marvrus/blob/master/LICENSE.md)

<br />

## Introduction

[ESRC](http://esrc.co.kr) provides the vision API and SDK for your mobile app, enabling real-time recognizing facial expression, heart response and emotion using a camera.

<br />

## Installation

To use our Android samples, you should first install [ESRC SDK Extension Marvrus for Android](https://github.com/esrc-official/ESRC-SDK-Android-Extension-Marvrus) 2.4.6 or higher on your system and should be received License Key by requesting by our email: **esrc@esrc.co.kr** <br /> 

<br />

## Before getting started

### Requirements

The minimum requirements for ESRC SDK for Android are:

- Android 4.2.0 (API level 17) or later
- Java 8 or later
- Gradle 4.0.0 or later

```groovy
// build.gradle(app)
android {
    defaultConfig {
        minSdkVersion 17
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

<br />

## Getting started

if you would like to try the sample app specifically fit to your usage, you can do so by following the steps below.

### Step 1: Initialize the ESRC SDK

Initialization binds the ESRC SDK to Android’s context, thereby allowing it to use a camera in your mobile. To the `init()` method, pass the **App ID** of your ESRC application to initialize the ESRC SDK and the **ESRCLicenseHandler** to received callback for validation of the App ID.

```java
Marvrus.init(APP_ID, getApplicationContext(), new ESRCLicense.ESRCLicenseHandler() {
    @Override
    public void onValidatedLicense() {
        …
    }
    
    @Override
    public void onInvalidatedLicense() {
        …
    }
});
```

> Note: The `Marvrus.init()` method must be called once across your Android app. It is recommended to initialize the ESRC SDK in the `onCreate()` method of the Application instance.

### (Optional) Step 2: Bind the ESRC Fragment

If you don't want to develop a layout that uses the camera, you can ues the ESRC Fragment provided from the ESRC SDK. Include the **container** to bind the ESRC Fragment in your layout `.xml` file. Please skip the Step 4: Feed the ESRC SDK. The ESRC Fragment will feed the image to our SDK itself.

```xml
<FrameLayout
    android:id=”@+id/container”
    android:layout_width=”match_parent”
    android:layout_height=”match_parent” />
```

> Note: FrameLayout is just one of examples. You can change to other layout type to purpose your app.

Bind the ESRC Fragment to display the image taken with the camera on the screen. ESRC Fragment send the image to the ESRC SDK in real-time to be able to recognize facial expression, heart response and emotion. ESRC Fragment automatically display the image to fit the size of your custom layout.

```java
// Bind LAYOUT.xml on your Activity.
setContentView(R.layout.LAYOUT);

// Replace the container in LAYOUT as the ESRC Fragment .
getSupportFragmentManager().beginTransaction()
    .replace(R.id.container, ESRCFragment.newInstance())
    .commit();
```

### Step 3: Start the ESRC SDK

Start the ESRC SDK to recognize your facial expression, heart response and emotion. To the `start()` method, pass the `MarvrusType.Property` to select analysis modules and the `Marvrus.MarvrusHandler` to handle the results. You should implement the callback method of `Marvrus.MarvrusHandler` interface. So, you can receive the results of face, facial landmark, head pose, attention, facial expression, heart rate, heart rate variability and engagement. Please refer to **[sample app](https://github.com/esrc-official/ESRC-Android-Extension-Marvrus)**.

```java
Marvrus.start(
    new MarvrusType.Property(
        true,  // Whether visualize result or not. It is only valid If you bind the ESRC Fragment (i.e., Step 2).
        true,  // Whether analyze measurement environment or not.
        true,  // Whether detect face or not.
        true,  // Whether detect facial landmark or not. If enableFace is false, it is also automatically set to false.
        true,  // Whether analyze facial action unit or not. If enableFace or enableFacialLandmark is false, it is also automatically set to false.
        true,  // Whether recognize basic facial expression or not. If enableFace is false, it is also automatically set to false.
        true,  // Whether recognize valence facial expression or not. If enableFace is false, it is also automatically set to false.
        true,  // Whether estimate remote hr or not. If enableFace is false, it is also automatically set to false.
        true,  // Whether analyze HRV not not. If enableFace or enableRemoteHR is false, it is also automatically set to false.
        true,  // Whether recognize engagement or not. If enableRemoteHR and enableHRV are false, it is also automatically set to false.    
        true),  // Whether recognize MEE index or not.    
    new Marvrus.MarvrusHandler() {
        @Override
        public void onRecognizedESRC(int id, MarvrusType.Face face, ..., ESRCException e) {
            if (e != null) {
                // Handle error.
            )
            
            // The ESRC results are recognized.
            // Through the paraemters of the onRecognizedESRC() callback method,
            // you can get the information of the face and heart response from the result object
            // that ESRC SDK Extension Marvrus has passed to the onRecognizedESRC().
        }
    });
```

### (Optional) Step 4: Set the MEE property

Set the ESRC SDK to recognize MEE index by passing the `MarvrusType.MEEProperty`.

```java
Marvrus.setMEEProperty(MEEProperty)
```

### (Optional) Step 5: Feed the ESRC SDK

Feed `Mat` on the ESRC SDK. To the `feed()` method, pass the `Mat` image received using a camera in real-time. Please do it at 10 fps. You can skip this step if you follow Step 2: Bind the ESRC Fragment.

```java
Marvrus.feed(Mat);
```

### Step 6: Stop the ESRC SDK

When your app is not use the camera or destroyed, stop the ESRC SDK.

```java
Marvrus.stop();
```

<br />

## Android sample

You can **clone** the project from the [Sample repository](https://github.com/esrc-official/ESRC-Android-Extension-Marvrus).

```
// Clone this repository
git clone git@github.com:esrc-official/ESRC-Android-Extension-Marvrus.git

// Move to the sample
cd ESRC-Android-Extension-Marvrus
```

<br />

## Reference

For further detail on ESRC SDK for Android, reter to [ESRC SDK Extension Marvrus for Android README](https://github.com/esrc-official/ESRC-SDK-Android-Extension-Marvrus/blob/master/README.md).
