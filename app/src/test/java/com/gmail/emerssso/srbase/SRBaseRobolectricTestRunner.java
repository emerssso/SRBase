package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.BuildConfig;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

/**
 * A testrunner for the SRBase project, which provides the needed details for Robolectric tests to work.
 */
public class SRBaseRobolectricTestRunner extends RobolectricTestRunner {
    private static final int MAX_SDK_SUPPORTED_BY_ROBOLECTRIC = 18;

    public SRBaseRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    protected String getAndroidManifestPath() {
        return "/src/main/AndroidManifest.xml";
    }

    protected String getResPath() {
        return "/src/main/res";
    }

    @Override
    protected final AndroidManifest getAppManifest(Config config) {
        return new AndroidManifest(Fs.fileFromPath(getAndroidManifestPath()),
                Fs.fileFromPath(getResPath())) {
            @Override
            public int getTargetSdkVersion() {
                return MAX_SDK_SUPPORTED_BY_ROBOLECTRIC;
            }
        };
    }
}
