/*
 * DO NOT EDIT.  THIS FILE IS GENERATED FROM nsIComposer.idl
 */

#ifndef __gen_nsIComposer_h__
#define __gen_nsIComposer_h__

#include "nsISupports.h" /* interface nsISupports */
#include "nsID.h" /* interface nsID */

#ifdef XPIDL_JS_STUBS
#include "jsapi.h"
#endif

/* starting interface nsIComposer */

/* {82041532-D73E-11d2-82A9-00805F2A0107} */
#define NS_ICOMPOSER_IID_STR "82041532-D73E-11d2-82A9-00805F2A0107"
#define NS_ICOMPOSER_IID \
  {0x82041532, 0xd73e, 0x11d2, \
    { 0x82, 0xa9, 0x0, 0x80, 0x5f, 0x2a, 0x1, 0x7 }}

class nsIComposer : public nsISupports {
 public: 
  static const nsIID& GetIID() {
    static nsIID iid = NS_ICOMPOSER_IID;
    return iid;
  }

#ifdef XPIDL_JS_STUBS
  static NS_EXPORT_(JSObject *) InitJSClass(JSContext *cx);
  static NS_EXPORT_(JSObject *) GetJSObject(JSContext *cx, nsIComposer *priv);
#endif
};

#endif /* __gen_nsIComposer_h__ */
