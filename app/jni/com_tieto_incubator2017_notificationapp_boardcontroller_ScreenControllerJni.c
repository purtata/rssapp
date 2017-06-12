#include <android/log.h>
#include <stdio.h>
#include "com_tieto_incubator2017_notificationapp_boardcontroller_ScreenControllerJni.h"
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/un.h>
#include<fcntl.h>

#define SOCK_PATH "/dev/socket/notd_socket"
#define SEND_ERROR -1

int sendNewsMessage(const char *message) {
    return 0;
}

int sendUserMessage(const char *message) {
    int socket_file_descriptor;
    int len;
    struct sockaddr_un remote;

    if ((socket_file_descriptor = socket(AF_UNIX, SOCK_STREAM, 0)) == -1) {
        __android_log_print(ANDROID_LOG_INFO, "App to NotD", "socket failed");
        return SEND_ERROR;
    }
    __android_log_print(ANDROID_LOG_INFO, "App to NotD", "socket ok");
    remote.sun_family = AF_UNIX;
    strcpy(remote.sun_path, SOCK_PATH);
    __android_log_print(ANDROID_LOG_INFO, "App to NotD path ok", "%s",remote.sun_path);
    len = (int)strlen(remote.sun_path) + sizeof(remote.sun_family);
    if (connect(socket_file_descriptor, (struct sockaddr *)&remote, (socklen_t)len) == -1) {
        __android_log_print(ANDROID_LOG_INFO, "App to NotD", "connect failed");
        return SEND_ERROR;
    }
    __android_log_print(ANDROID_LOG_INFO, "App to NotD", "connect ok");

    if (send(socket_file_descriptor, message, strlen(message), 0) == -1) {
        __android_log_print(ANDROID_LOG_INFO, "App to NotD", "send failed");
        return SEND_ERROR;
    }
    close(socket_file_descriptor);
    __android_log_print(ANDROID_LOG_INFO, "App to NotD", "send ok");

    return 0;
}

JNIEXPORT int JNICALL
Java_com_tieto_incubator2017_notificationapp_boardcontroller_ScreenControllerJni_sendMessage
        (JNIEnv *jni, jobject object, jstring message) {
    const char *inCStr = (*jni)->GetStringUTFChars(jni, message, NULL);
    (*jni)->ReleaseStringUTFChars(jni, message, inCStr);
    return sendUserMessage(inCStr);
}

JNIEXPORT int JNICALL
Java_com_tieto_incubator2017_notificationapp_boardcontroller_ScreenControllerJni_sendListOfMessages
        (JNIEnv *jni, jobject object, jobjectArray listOfMessage) {
    int stringCount = (*jni)->GetArrayLength(jni, listOfMessage);
    for (int i = 0; i < stringCount; i++) {
        jstring string = (jstring) ((*jni)->GetObjectArrayElement(jni, listOfMessage, i));
        const char *inCStr = (*jni)->GetStringUTFChars(jni, string, NULL);
        (*jni)->ReleaseStringUTFChars(jni, string, inCStr);
        if (sendNewsMessage(inCStr) != 0) {
            return SEND_ERROR;
        }
    }
    return 0;
}

JNIEXPORT int JNICALL
Java_com_tieto_incubator2017_notificationapp_boardcontroller_ScreenControllerJni_clearScreen
        (JNIEnv *jni, jobject object) {
    return sendUserMessage("");
}
