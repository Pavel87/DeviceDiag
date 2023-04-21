package com.pacmac.devicediag;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.UpToDateEnum;
import com.pacmac.devinfo.utils.Utility;
import com.pacmac.devinfo.utils.Utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class GeneralTests {

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testGetprop() {
//        List<UIObject> list = Utils.getBuildPropsList(context);
//        Assert.assertTrue(list.size() > 0);
    }


    @Test
    public void versionCheck(){

        Assert.assertEquals(UpToDateEnum.YES, Utils.INSTANCE.hasVersionIncreased("2.0.0", "1.9.97"));
        Assert.assertEquals(UpToDateEnum.YES, Utils.INSTANCE.hasVersionIncreased("1.9.97", "1.9.97"));
        Assert.assertEquals(UpToDateEnum.YES, Utils.INSTANCE.hasVersionIncreased("1.9.98", "1.9.97"));
        Assert.assertEquals(UpToDateEnum.YES, Utils.INSTANCE.hasVersionIncreased("1.10.97", "1.9.97"));

        Assert.assertEquals(UpToDateEnum.NO, Utils.INSTANCE.hasVersionIncreased("1.9.96", "1.9.97"));
        Assert.assertEquals(UpToDateEnum.NO, Utils.INSTANCE.hasVersionIncreased("1.1.97", "1.9.97"));
        Assert.assertEquals(UpToDateEnum.NO, Utils.INSTANCE.hasVersionIncreased("1.1.98", "1.9.97"));
        Assert.assertEquals(UpToDateEnum.NO, Utils.INSTANCE.hasVersionIncreased("1.10.97", "2.0.0"));

        Assert.assertEquals(UpToDateEnum.YES, Utils.INSTANCE.hasVersionIncreased("2.0.0", "2.0.0"));
        Assert.assertEquals(UpToDateEnum.YES, Utils.INSTANCE.hasVersionIncreased("2.0.1", "2.0.0"));



    }
}