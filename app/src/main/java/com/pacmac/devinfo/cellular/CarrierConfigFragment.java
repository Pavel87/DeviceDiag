package com.pacmac.devinfo.cellular;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * From API 26
 */
public class CarrierConfigFragment extends Fragment {

    private CellularViewModel cellularViewModel;

    private RecyclerView basicRecyclerView;
    private RecyclerView.LayoutManager basicLinearLayoutManager;
    private BasicItemAdapter basicItemAdapter;

    private TextView paramCount;

    public static CarrierConfigFragment newInstance() {
        CarrierConfigFragment fragment = new CarrierConfigFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cellularViewModel = new ViewModelProvider(getActivity()).get(CellularViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_carrier_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        paramCount = view.findViewById(R.id.paramCount);
        basicRecyclerView = view.findViewById(R.id.carrierConfigList);
        basicRecyclerView.setHasFixedSize(true);
        basicLinearLayoutManager = new LinearLayoutManager(getContext());
        basicRecyclerView.setLayoutManager(basicLinearLayoutManager);
        basicItemAdapter = new BasicItemAdapter(getContext(), new ArrayList<>());
        basicRecyclerView.setAdapter(basicItemAdapter);
        basicRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Observer<List<UIObject>> basicObserver = uiObjects -> {
                basicItemAdapter.updateData(uiObjects);
                paramCount.setText(String.format(Locale.ENGLISH, "%d / %d", uiObjects.size(), uiObjects.size()));
            };
            cellularViewModel.getCarrierConfig(getContext()).observe(getViewLifecycleOwner(), basicObserver);
        }
    }

//    SearchView searchView;
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_build_props, menu);
//
//        // Associate searchable configuration with the SearchView
//        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search_build_prop)
//                .getActionView();
//        searchView.setSearchableInfo(searchManager
//                .getSearchableInfo(getActivity().getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//
//        // listening to search query text change
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // filter recycler view when query submitted
////                buildPropsAdapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                // filter recycler view when text is changed
////                buildPropsAdapter.getFilter().filter(query);
//                return false;
//            }
//        });
//        return;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        if (id == R.id.action_search_build_prop) {
//            return true;
//        }
//
//        if (id == R.id.menu_item_share) {
////            Utility.exporData(BuildPropertiesActivity.this, getResources().getString(R.string.title_activity_build_properties), updateMessageForExport());
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
