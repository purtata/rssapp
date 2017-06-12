LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := com_tieto_incubator2017_notificationapp_boardcontroller_ScreenControllerJni.c

LOCAL_LDLIBS = -llog
LOCAL_MODULE := screencontroller

include $(BUILD_SHARED_LIBRARY)
