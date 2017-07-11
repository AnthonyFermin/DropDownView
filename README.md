# DropDownView

[![](https://jitpack.io/v/AnthonyFermin/DropDownView.svg)](https://jitpack.io/#AnthonyFermin/DropDownView)

![Partial Drop Down](https://media.giphy.com/media/26FmQTErmydlNAvks/giphy.gif) ![Full Drop Down](https://media.giphy.com/media/3oKIPeHezv11Zk6y5i/giphy.gif)

Add view to xml:
```
<com.anthonyfdev.dropdownview.DropDownView
        android:id="@+id/drop_down_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:containerBackgroundColor="#b71c1c"
        app:overlayColor="#64000000"/>
```

Bind views:
```
        dropDownView = (DropDownView) findViewById(R.id.drop_down_view);
        collapsedView = LayoutInflater.from(this).inflate(R.layout.view_my_drop_down_header, null, false);
        expandedView = LayoutInflater.from(this).inflate(R.layout.view_my_drop_down_expanded, null, false);
```

Set header and expanded views to DropDownView:
```
        dropDownView.setHeaderView(collapsedView);
        dropDownView.setExpandedView(expandedView);
```

Call expand or collapse:
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

Done!

View the example app at https://github.com/AnthonyFermin/DropDownViewExample
