package com.toolsbox.contractor.controller.chat.channels;

import android.util.Log;

import com.toolsbox.contractor.common.interFace.TaskCompletionListener;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelDescriptor;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Paginator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelExtractor {
  private static final String TAG = "ChannelExtractor";

  public void extractAndSortFromChannelDescriptor(Paginator<ChannelDescriptor> paginator,
                                                  final TaskCompletionListener<List<Channel>, String> listener) {

    extractFromChannelDescriptor(paginator, new TaskCompletionListener<List<Channel>, String>() {
      @Override
      public void onSuccess(List<Channel> channels) {
        listener.onSuccess(channels);
      }

      @Override
      public void onError(String s) {
        listener.onError(s);
      }
    });
  }

  private void extractFromChannelDescriptor(Paginator<ChannelDescriptor> paginator,
                                            final TaskCompletionListener<List<Channel>, String> listener) {

    final List<Channel> channels = new ArrayList<>();
    final AtomicInteger channelDescriptorCount = new AtomicInteger(paginator.getItems().size());
    if (paginator.getItems().size() == 0){
      Log.e(TAG, "paginator size ==0");
      listener.onSuccess(new ArrayList<>());
    }
    for (ChannelDescriptor channelDescriptor : paginator.getItems()) {
      channelDescriptor.getChannel(new CallbackListener<Channel>() {
        @Override
        public void onSuccess(Channel channel) {
          channels.add(channel);
          int channelDescriptorsLeft = channelDescriptorCount.decrementAndGet();
          if(channelDescriptorsLeft == 0) {
            listener.onSuccess(channels);
          }
        }

        @Override
        public void onError(ErrorInfo errorInfo) {
          listener.onError(errorInfo.getMessage());
        }
      });
    }

  }
}
