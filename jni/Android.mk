LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := JNI
LOCAL_SRC_FILES := JNI.cpp

include $(BUILD_SHARED_LIBRARY)

LOCAL_SHARED_LIBRARIES := location_baidu/baiduLocation baiduMapSDK/libBaiduMapSDK
include $(LOCAL_PATH)/location_baidu/Android.mk $(LOCAL_PATH)/baiduMapSDK/Android.mk
