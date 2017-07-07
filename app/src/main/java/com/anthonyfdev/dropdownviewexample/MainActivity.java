package com.anthonyfdev.dropdownviewexample;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anthonyfdev.dropdownview.DropDownView;

public class MainActivity extends AppCompatActivity {

    public static final int NUM_OF_STANDS = 4;
    private RecyclerView recyclerView;
    private DropDownAdapter adapter;
    private int selectedStandId;
    private String[] waitTimes;
    private TextView selectedStandTitleTV;
    private TextView selectedStandStatusTV;
    private View collapsedView;
    private DropDownView dropDownView;
    private View expandedView;
    private ImageView headerChevronIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        setupList();

        dropDownView.setHeaderView(collapsedView);
        dropDownView.setExpandedView(expandedView);
        dropDownView.setDropDownListener(dropDownListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        headerChevronIV.setRotation(dropDownView.isExpanded()
                ? 180f : 0f);
    }

    public void setStandStateWithId(String waitTime, int standId) {
        if (standId >= 0 && standId < NUM_OF_STANDS) {
            waitTimes[standId] = waitTime;
            adapter.notifyItemChanged(standId);
        }

        // Should update currently selected stand wait time as well
        if (selectedStandId == standId) {
            selectedStandStatusTV.setText(waitTime);
        }
    }

    private void setupList() {
        waitTimes = new String[] {"3 minute wait", "Closed", "No wait time", "10 minute wait"};
        viewActions.setSelectedStand(1);
        adapter = new DropDownAdapter(viewActions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        for (int i = 0; i < waitTimes.length; i++) {
            setStandStateWithId(waitTimes[i], i);
        }
    }

    private void setupViews() {
        dropDownView = (DropDownView) findViewById(R.id.drop_down_view);
        collapsedView = LayoutInflater.from(this).inflate(R.layout.view_my_drop_down_header, null, false);
        expandedView = LayoutInflater.from(this).inflate(R.layout.view_my_drop_down_expanded, null, false);

        selectedStandTitleTV = (TextView) collapsedView.findViewById(R.id.selected_stand_title);
        selectedStandStatusTV = (TextView) collapsedView.findViewById(R.id.selected_stand_status);
        recyclerView = (RecyclerView) expandedView.findViewById(R.id.recyclerView);
        headerChevronIV = (ImageView) collapsedView.findViewById(R.id.chevron_image);
    }

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

    private final DropDownAdapter.ViewActions viewActions = new DropDownAdapter.ViewActions() {
        @Override
        public void collapseDropDown() {
            dropDownView.collapseDropDown();
        }

        @Override
        public void setSelectedStand(int standId) {
            selectedStandTitleTV.setText(getStandTitle(standId ));
            selectedStandStatusTV.setText(getStandStatus(standId));
            selectedStandId = standId;
        }

        @Override
        public String getStandTitle(int standId) {
            String title = "";
            switch (standId) {
                case 0:
                    title = getString(R.string.standBrooklynLemonade);
                    break;
                case 1:
                    title = getString(R.string.standManhattanBourjeeSliders);
                    break;
                case 2:
                    title = getString(R.string.standQueensAndCakes);
                    break;
                case 3:
                    title = getString(R.string.standBronxTea);
                    break;
            }
            return title;
        }

        @Override
        public String getStandStatus(int standId) {
            return waitTimes[standId] != null ? waitTimes[standId] : "";
        }

        @Override
        public int getSelectedStand() {
            return selectedStandId;
        }
    };

}
