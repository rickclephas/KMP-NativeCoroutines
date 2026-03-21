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

At the moment Swift export has some issues with functional return types and generics.

Unfortunately KMP-NativeCoroutines heavily relies on functional return types, making it incompatible with Swift Export.
For now the plugin just clones your original functions and properties to prevent your Kotlin builds from failing.

## ⚠️ `Flow` cancellation doesn't fully work yet

Cancelling a `Flow` from Swift doesn't properly cancel the Flow on the Kotlin side
([KT-85159](https://youtrack.jetbrains.com/issue/KT-85159)).

## ⚠️ `Flow` with `null` values is canceled

A `Flow` that emits `null` values will be canceled at the first `null` value
([KT-84485](https://youtrack.jetbrains.com/issue/KT-84485)).

## ⚠️ `Flow` with `Unit` values crashes

A `Flow` with `Unit` values will crash with a force cast exception
([KT-85163](https://youtrack.jetbrains.com/issue/KT-85163)).

# Enabling Swift export

To enable Swift export with KMP-NativeCoroutines you'll need to activate the Swift export compatibility mode:
```kotlin
// build.gradle.kts
nativeCoroutines {
    swiftExport = true
}
```

# Usage

Only some coroutines related code can be used when Swift export is enabled.

> [!NOTE]
> You can also use the generated properties for the `StateFlow.value` and `SharedFlow.replayCache` values.

## Suspend functions

You can use suspend functions as async functions in Swift without any changes:
```swift
let letters = try await asyncFunction(for: randomLettersGenerator.getRandomLetters(throwException: throwException))
```

> [!NOTE]
> It's recommended to keep using the `asyncFunction(for:)` function for now.  
> However this function is a no-op and can eventually be removed.

For Combine and RxSwift there are helper functions available, e.g.:
```diff
- let future = createFuture(for: randomLettersGenerator.getRandomLetters(throwException: throwException))
+ let future = createFuture(for: { await self.randomLettersGenerator.getRandomLetters(throwException: throwException) })
```

## Flows

You can use `Flow`s by adding the following helper function to your project:
```swift
import KotlinCoroutineSupport

/// This function provides source compatibility during the migration to Swift export.
///
/// This is a no-op function and it can be safely removed once you have fully migrated to Swift export.
@available(*, deprecated, message: "Kotlin Coroutines are supported by Swift export")
public func asyncSequence<T>(
    for flow: any KotlinTypedFlow<T>
) -> KotlinFlowSequence<T> {
    return flow.asAsyncSequence()
}
```

After that you can use `Flow`s as `AsyncSequence`s without any changes:
```swift
let sequence = asyncSequence(for: clock.time)
```

> [!NOTE]
> Upon cancellation Swift export will throw a `CancellationError` instead of ending the iteration by returning `nil`.

> [!NOTE]
> It's recommended to keep using the `asyncSequence(for:)` function for now.  
> However this function is a no-op and can eventually be replaces with a call to `asAsyncSequence()`.

For Combine and RxSwift there are helper functions available, e.g.:
```diff
- let publisher = createPublisher(for: clock.time)
+ let publisher = createPublisher(for: asyncSequence(for: clock.time))
```
