### React-Native-VLC-Player

*The fork was necessary as the project seems abandoned*

> VLC Player for react-native

*Only Android support now.*

#### Integrate

##### Android

##### Install via npm
`npm i react-native-vlc-player --save`

##### Add dependency to `android/settings.gradle`
```
...
include ':libvlc'
project(':libvlc').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-vlc-player/android/libvlc')

include ':react-native-vlc-player'
project(':react-native-vlc-player').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-vlc-player/android/vlc')
```

##### Add `android/app/build.gradle`
```
...
dependencies {
    ...
    implementation project(':react-native-vlc-player')
}
```
##### Register module in `MainApplication.java`
```Java
import com.ghondar.vlcplayer.*;  // <--- import

@Override
 protected List<ReactPackage> getPackages() {
   return Arrays.<ReactPackage>asList(
      new VLCPlayerPackage(),  // <------- here
      new MainReactPackage()
   );
 }
```

#### Usage

```Javascript
import React, { AppRegistry, StyleSheet, Component, View, Text, TouchableHighlight } from 'react-native'
import { play, playList } from 'react-native-vlc-player'


class Example extends Component {
  constructor(props, context) {
    super(props, context)
  }

  render() {

    return (
      <View style={styles.container}>
          { play('file:///storage/emulated/0/example.avi') }
          /*
            { playList(['example1.avi', 'example2.avi', 'example3.avi'], 0) }
          */
      </View>
    )
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  }
});

AppRegistry.registerComponent('example', () => Example);
```

#### USAGE

1. Swipe Up to exit
2. Swipe Left or Right to change media
2. Click to pause and unpause (does not work yet)


#### LICENSE
MIT

