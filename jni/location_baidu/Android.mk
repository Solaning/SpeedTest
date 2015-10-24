LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := baiduLocation
LOCAL_SRC_FILES := liblocSDK4d.so
include $(PREBUILT_SHARED_LIBRARY)
