package com.pacmac.devinfo.storage;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;

import static com.pacmac.devinfo.storage.StorageUtils.getDeviceStorage;

public class StorageViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "storage_info";

    private MutableLiveData<List<UIObject>> storageInfo = new MutableLiveData<>();


    public MutableLiveData<List<UIObject>> getStorageInfo(Context context) {
        new Thread(() -> loadStorageInfo(context)).start();
        return storageInfo;
    }


    public List<UIObject> getStorageInfoForExport(Context context) {

        if (storageInfo.getValue() != null) {
            List<UIObject> list = new ArrayList<>();
            list.add(new UIObject(context.getString(R.string.title_activity_storage_info), "", 1));
            list.add(new UIObject(context.getString(R.string.param), context.getString(R.string.value), 1));
            list.addAll(storageInfo.getValue());

            return list;
        }

        return null;
    }

    private void loadStorageInfo(Context context) {
        List<UIObject> list = new ArrayList<>();

        list.add(new UIObject(context.getString(R.string.device_ram), "", 1));

        String flashHW = StorageUtils.getRAMHardware();
        if (flashHW != null) {
            list.add(new UIObject(context.getString(R.string.device_ram_hw), flashHW));
        }

        StorageUtils.ByteValue totalRAM = StorageUtils.getTotalMemory(context);
        list.add(new UIObject(context.getString(R.string.device_total), totalRAM.getValue(), totalRAM.getUnit()));

        StorageUtils.ByteValue available = StorageUtils.getAvailableMemory(context);
        list.add(new UIObject(context.getString(R.string.device_available), available.getValue(), available.getUnit()));

        list.add(new UIObject(context.getString(R.string.device_low_memory), StorageUtils.getLowMemoryStatus(context)));


        //retrieve STORAGE OPTIONS

        List<StorageUtils.StorageSpace> listStorage = getDeviceStorage(context);

        if (listStorage.size() > 0) {
            if (listStorage.size() > 1) {
                long total = 0;
                long free = 0;
                for (StorageUtils.StorageSpace storage : listStorage) {
                    total += storage.getTotal();
                    free += storage.getFree();
                }

                list.add(new UIObject(context.getString(R.string.device_storage_label), "", 1));
                StorageUtils.ByteValue totalStorage = StorageUtils.byteConvertor(total);
                StorageUtils.ByteValue availableStorage = StorageUtils.byteConvertor(free);
                list.add(new UIObject(context.getResources().getString(R.string.device_total), totalStorage.getValue(), totalStorage.getUnit()));
                list.add(new UIObject(context.getString(R.string.device_available), availableStorage.getValue(), availableStorage.getUnit()));
                StorageUtils.ByteValue used = StorageUtils.byteConvertor(total - free);
                list.add(new UIObject(context.getString(R.string.device_used), used.getValue(), used.getUnit()));
            }

            for (StorageUtils.StorageSpace s : listStorage) {
                list.add(new UIObject(StorageUtils.getTypeString(context, s.getType()), "", 1));

                StorageUtils.ByteValue totalSD = StorageUtils.byteConvertor(s.getTotal());
                list.add(new UIObject(context.getResources().getString(R.string.device_total), totalSD.getValue(), totalSD.getUnit()));

                StorageUtils.ByteValue freeSD = StorageUtils.byteConvertor(s.getFree());
                list.add(new UIObject(context.getString(R.string.device_available), freeSD.getValue(), freeSD.getUnit()));

                StorageUtils.ByteValue usedSD = StorageUtils.byteConvertor(s.getTotal() - s.getFree());
                list.add(new UIObject(context.getString(R.string.device_used), usedSD.getValue(), usedSD.getUnit()));
            }
        }
        storageInfo.postValue(list);
    }


}
