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

Unfortunately KMP-NativeCoroutines heavily relies on functional return types, making it incompatible with Swift Export.
For now the plugin just clones your original functions and properties to prevent your Kotlin builds from failing.

**Temporary workaround:**  
You should disable any relevant code in Swift if you would like to try Swift export.

## 🚨 `@Throws` suspend functions are unsupported

Throwing suspend functions aren't supported yet.

KMP-NativeCoroutines behaves as if a `@Throws(Exception::class)` annotation was added to all suspend functions.
Since throwing suspend functions aren't supported yet, any exception will currently cause a fatal crash.

## 🚨 Cancellation isn't supported yet

At the moment you can't cancel suspend functions.  
Meaning your suspend functions will keep running until they either complete or fail.

## ⚠️ `@ObjCName` is ignored

The `@ObjCName` annotation is (currently) ignored by Swift export.  
This prevents KMP-NativeCoroutines from reusing your original function or property name.

**Temporary workaround:**  
You should update your Swift code with the `Native` name suffix in order to access the generated declarations.

# Enabling Swift export

To enable Swift export with KMP-NativeCoroutines you start by following the
[official documentation](https://kotlinlang.org/docs/native-swift-export.html)
and enabling the experimental coroutines support:
```kotlin
// build.gradle.kts
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.swiftexport.SWIFT_EXPORT_COROUTINES_SUPPORT_TURNED_ON

kotlin {
    // ...
    swiftExport {
        // ...
        configure {
            settings.put(SWIFT_EXPORT_COROUTINES_SUPPORT_TURNED_ON, "true")
        }
    }
}
```

Once Swift export is enabled you'll need to activate the Swift export compatibility mode:
```kotlin
// build.gradle.kts
nativeCoroutines {
    swiftExport = true
}
```

# Usage

Only some coroutines related code can be used when Swift export is enabled.

> [!NOTE]
> You can also use the generated properties for the `StateFlow.value` and `SharedFlow.replayCache` values,
> but keep in mind the `@ObjCName` limitation. 

## Suspend functions

You can use suspend functions as async functions in Swift (but keep in mind the limitations):
```diff
- let letters = try await asyncFunction(for: randomLettersGenerator.getRandomLetters(throwException: throwException))
+ let letters = try await asyncFunction(for: randomLettersGenerator.getRandomLettersNative(throwException: throwException))
```

> [!NOTE]
> It's recommended to keep using the `asyncFunction(for:)` function for now.  
> However this function is a no-op and can eventually be removed.

For Combine and RxSwift there are helper functions available, e.g.:
```diff
- let future = createFuture(for: randomLettersGenerator.getRandomLetters(throwException: throwException))
+ let future = createFuture(for: { await self.randomLettersGenerator.getRandomLettersNative(throwException: throwException) })
```
