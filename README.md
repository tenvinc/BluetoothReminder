# Bluetooth Reminder

![Project Image](images/ic_launcher_round.png)

> Bluetooth Reminder is an android app that helps to keep track of bluetooth beacons around you and notify you when they
go out of range.

---

### Table of Contents

- [Description](#description)
- [How To Use](#how-to-use)
- [References](#references)
- [License](#license)

---

## Description

It is very common for people to misplace things. To alleviate this problem, there are bluetooth trackers to be attached to things you want to keep track of. However, these trackers are often rather expensive
but come with many features. Thus, the aim of this project is to create DIY bluetooth trackers which are cheap and easy to use, albeit more limited in functionality.
Bluetooth Reminder is an app that communicates with these DIY beacons and triggers a notification whenever these beacons go out of range.

[Back To The Top](#read-me-template)

---

## How To Use

#### Installation

Go to Git releases and download the apk file on your corresponding Android devices. When prompted, press install.
Upon launch of the app, you should see a page with 2 buttons with a gear icon on the top right corner.

The gear icon when clicked will lead to the settings page where you can customise the scan settings.

The first button, __"SCAN FOR NEW BEACONS"__, will lead you to a page which shows all iBeacons in range.
To add a beacon to tracked, click on it and enter your desired name. After a few seconds, you should see that beacon disappear from the list, signifying that it has been added.
Each beacon also has a distance associated to it which can be used to distinguish beacons.

The second button, __"SEE TRACKED BEACONS"__, allows you to manage your tracked beacons, adding, removing, editting their names and turning them on or off.
When the tracked beacon is turned off, it will no longer trigger any notifications.

For more information on the distance calculation, please visit [AltBeacon](https://altbeacon.github.io/android-beacon-library/distance-calculations.html).

---

## API Reference

AltBeacon API is used to manage the beacons. For more information, please visit [AltBeacon](https://altbeacon.github.io/android-beacon-library/javadoc/reference/org/altbeacon/beacon/BeaconManager.html)
The beacon protocol used in this app is iBeacon. There are many beacon protocols out there and you can change the beacon protocol in the following code excerpt.

```java
    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
```

```java
    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_LAYOUT));
```

---

## References
- [AltBeacon library](https://altbeacon.github.io/android-beacon-library/javadoc/reference/packages.html)
- [AltBeacon Example code](https://github.com/AltBeacon/android-beacon-library-reference/blob/master/app/src/main/java/org/altbeacon/beaconreference/BeaconReferenceApplication.java)
- [AT commands for AT-09](https://www.tinyosshop.com/datasheet/Tinysine%20Serial%20Bluetooth4%20user%20manual.pdf)

[Back To The Top](#read-me-template)

---

## License

MIT License

Copyright (c) [2017] [Vincent Neo Guo Zhong]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

[Back To The Top](#read-me-template)

---
