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

To use `Flow`s you'll need to add the following import:
```swift
import KotlinCoroutineSupport
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
