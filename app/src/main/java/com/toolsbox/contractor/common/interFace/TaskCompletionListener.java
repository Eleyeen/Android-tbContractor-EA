package com.toolsbox.contractor.common.interFace;

public interface TaskCompletionListener<T, U> {

  void onSuccess(T t);

  void onError(U u);
}
