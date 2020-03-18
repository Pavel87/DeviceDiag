package com.pacmac.devicediag;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.Utility;

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
        List<UIObject> list = Utility.getBuildPropsList(context);
        Assert.assertTrue(list.size() > 0);
    }
}