# Swift Export compatibility and migration

Starting with Kotlin 2.2.20 you can use the (experimental)
[Swift export](https://kotlinlang.org/docs/native-swift-export.html), which provides direct Kotlin - Swift interoperability.

> [!WARNING]
> Swift export is still experimental and actively being developed! It doesn't yet support all language features,
> might still contain bugs, and its behaviour could change in future versions.
> It is NOT recommended to use Swift export in production just yet!

Once Swift export is complete and stable it'll likely completely remove the need for KMP-NativeCoroutines.

So to support you in your journey to Swift export:

* KMP-NativeCoroutines will try to be "compatible" with Swift export as much as possible, 
allowing you to start testing with Swift export as soon as possible.
* A migration path will be provided once coroutines support in Swift export becomes stable.


# Known limitations

These are the known limitations with Swift export and KMP-NativeCoroutines.

## 🚨 `NativeSuspend` and `NativeFlow` are unsupported

At the moment Swift export doesn't support functional return types yet.

Unfortunately KMP-NativeCoroutines heavily relies on functional return types making it incompatible with Swift Export.
For now the plugin just clones your original functions and properties to prevent your Kotlin builds from failing.

**Temporary workaround:**  
You should disable any relevant code in Swift if you would like try Swift export.

## ⚠️ `@ObjCName` is ignored

The `@ObjCName` annotation is (currently) ignored by Swift export.  
This prevents KMP-NativeCoroutines from reusing your original function or property name.

**Temporary workaround:**  
You should update your Swift code with the `Native` name suffix in order to access the generated declarations.

# Enabling Swift export

To enable Swift export with KMP-NativeCoroutines you start by following the
[official documentation](https://kotlinlang.org/docs/native-swift-export.html).

Once Swift export is enabled you'll need to activate the Swift export compatibility mode:
```kotlin
// build.gradle.kts
nativeCoroutines {
    swiftExport = true
}
```

# Usage

At the moment you can't use any coroutines related code when Swift export is enabled.

You can still use the generated properties for the `StateFlow.value` and `SharedFlow.replayCache` values,
but keep in mind the `@ObjCName` limitation. 
