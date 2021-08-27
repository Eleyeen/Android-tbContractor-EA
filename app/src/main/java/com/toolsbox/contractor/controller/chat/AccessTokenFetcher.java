package com.toolsbox.contractor.controller.chat;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.interFace.TaskCompletionListener;
import com.toolsbox.contractor.common.model.api.TwilioAccessTokenData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.GlobalUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.toolsbox.contractor.common.utils.DeviceUtil.getAndroidUniqueID;

public class AccessTokenFetcher {
  private static final String TAG = "AccessTokenFetcher";
  private Context context;

  public AccessTokenFetcher(Context context) {
    this.context = context;
  }

  public void fetch(final TaskCompletionListener<String, String> listener) {
    String androidID = getAndroidUniqueID(context);
    String identity = GlobalUtils.getChatIdentity();
    ApiUtils.fetchTwilioAccessToken(context, identity, androidID, new Notify() {
      @Override
      public void onSuccess(Object object) {
        TwilioAccessTokenData data = (TwilioAccessTokenData) object;
        if (data != null){
          if (data.status == 0){
            Log.e(TAG, "Twilio Access Token =" + data.info);
            String accessToken = data.info;
            listener.onSuccess(accessToken);
          } else {
            if (listener != null) {
              listener.onError(data.message);
            }
          }
        } else {
          if (listener != null) {
            listener.onError("Server Not response");
          }
        }
      }

      @Override
      public void onFail() {
        listener.onError("Check network connection");
      }
    });
  }



}
