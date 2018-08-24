
# react-native-hotspot-manager

## Getting started

`$ npm install --save greyparrot/react-native-hotspot-manager#master`

### Mostly automatic installation

`$ react-native link react-native-hotspot-manager`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-hotspot-manager` and add `RNHotspotManager.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNHotspotManager.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.hotspotmanager.RNHotspotManagerPackage;` to the imports at the top of the file
  - Add `new RNHotspotManagerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-hotspot-manager'
  	project(':react-native-hotspot-manager').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-hotspot-manager/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-hotspot-manager')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNHotspotManager.sln` in `node_modules/react-native-hotspot-manager/windows/RNHotspotManager.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Hotspot.Manager.RNHotspotManager;` to the usings at the top of the file
  - Add `new RNHotspotManagerPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNHotspotManager from 'react-native-hotspot-manager';

// TODO: What to do with the module?
RNHotspotManager.createHotspot('HOTSPOT NAME')
        .then((response) => {
          // {
          //   status: "Created Network!" || "Failed!"
          // }
        })
...

RNHotspotManager.disableHotspot();

```
   
