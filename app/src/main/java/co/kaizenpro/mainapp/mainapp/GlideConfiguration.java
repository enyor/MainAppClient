package co.kaizenpro.mainapp.mainapp;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import okhttp3.OkHttpClient;


public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
        //Unsafe Okhttp client

        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        glide.register(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(client));

    }
}
