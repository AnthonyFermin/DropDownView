# DropDownView

[![](https://jitpack.io/v/AnthonyFermin/DropDownView.svg)](https://jitpack.io/#AnthonyFermin/DropDownView)

![Partial Drop Down](https://media.giphy.com/media/26FmQTErmydlNAvks/giphy.gif) ![Full Drop Down](https://media.giphy.com/media/3oKIPeHezv11Zk6y5i/giphy.gif)

## Gradle

### Ensure your minSdkVersion is 19 or above:

```
android {
    compileSdkVersion 25
    defaultConfig {
	applicationId "com.anthonyfdev.dropdownviewexample"
	minSdkVersion 19
	targetSdkVersion 25
    }
}
```

#### Add jitpack repository to your root build.gradle:
```
allprojects {
	repositories {
	...
        maven { url 'https://jitpack.io' }
    }
}
```

#### Add dependency to app level build.gradle:
```
dependencies {
    compile 'com.github.AnthonyFermin:DropDownView:1.0.1'
}
```

### Usage
[JavaDocs](https://jitpack.io/com/github/AnthonyFermin/DropDownView/1.0.1/javadoc/)

#### Add view to xml:
```
<com.anthonyfdev.dropdownview.DropDownView
    android:id="@+id/drop_down_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:containerBackgroundColor="#b71c1c"
    app:overlayColor="#64000000"/>
```
**Note: containerBackgroundColor defaults to colorPrimary (#3F51B5 if you don't have that defined)
and overlayColor defaults to #99000000 (60% alpha on black)**

#### Bind views:
```
dropDownView = (DropDownView) findViewById(R.id.drop_down_view);
collapsedView = LayoutInflater.from(this).inflate(R.layout.view_my_drop_down_header, null, false);
expandedView = LayoutInflater.from(this).inflate(R.layout.view_my_drop_down_expanded, null, false);
```

#### Set header and expanded views to DropDownView:
```
dropDownView.setHeaderView(collapsedView);
dropDownView.setExpandedView(expandedView);
```

#### Call expand or collapse:
```
collapsedView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (dropDownView.isExpanded()) {
            dropDownView.collapseDropDown();
        } else {
            dropDownView.expandDropDown();
        }
    }
});
```

#### Optional DropDownListener:
```
/**
* A listener that wraps functionality to be performed when the drop down is successfully expanded
* or collapsed.
*/
private final DropDownView.DropDownListener dropDownListener = new DropDownView.DropDownListener() {
	@Override
	public void onExpandDropDown() {
	    adapter.notifyDataSetChanged();
	    ObjectAnimator.ofFloat(headerChevronIV, View.ROTATION.getName(), 180).start();
	}
  
	@Override
	public void onCollapseDropDown() {
	    ObjectAnimator.ofFloat(headerChevronIV, View.ROTATION.getName(), -180, 0).start();
	}
    };
     
...
 
dropDownView.setDropDownListener(dropDownListener);
```

#### Done!

## License

```
Copyright 2017 DropDownView

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
    
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
