package com.pacmac.devicediag;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.pacmac.devinfo.BuildProperty;
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
        List<BuildProperty> list = Utility.getBuildPropsList(context);
        Assert.assertTrue(list.size() > 0);
    }
}