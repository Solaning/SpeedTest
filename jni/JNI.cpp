#include <jni.h>
#include <string.h>

extern "C"

jstring JNICALL Java_com_kinth_mmspeed_util_AES_keyFromJNI(JNIEnv* env,
		jobject thiz) {
	return (env)->NewStringUTF("ZHgIrwLhn4lUTx50MtOW");
}
