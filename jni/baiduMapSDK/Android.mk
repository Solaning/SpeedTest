LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libBaiduMapSDK
LOCAL_SRC_FILES := libBaiduMapSDK_v3_1_1.so
include $(PREBUILT_SHARED_LIBRARY)
