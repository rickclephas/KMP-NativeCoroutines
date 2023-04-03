# Migrating to version 1.0

The 1.0 release will bring some improvements that require some changes in your code 😅.  

> **Warning**: the 1.0 release requires Kotlin 1.8.0-Beta or above.  
> Checkout the [README](README.md) for Kotlin compatibility info.

> **Note**: make sure to use the same library versions for your Kotlin and Swift code!

Known issues:
* [#83](https://github.com/rickclephas/KMP-NativeCoroutines/issues/83) 
Non-embeddable compiler JAR compilations are broken in v1.0

## KSP

Starting with v1.0 the plugin is using [KSP](https://github.com/google/ksp) to generate the required Kotlin code.  
So make sure to add KSP to your project (if you haven't already):
```diff
  plugins {
+     id("com.google.devtools.ksp") version "<ksp-version>"
      id("com.rickclephas.kmp.nativecoroutines") version "<version>"
  }
```

### Annotate your declarations

To tell the plugin what declarations should be refined for ObjC/Swift you'll need to annotate them:
```diff
+ @NativeCoroutines
  val time: StateFlow<Long>
  
+ @NativeCoroutines
  suspend fun getRandomLetters(): String = ""
```

> **Note**: error messages and IDE support are currently limited. 
> Please track [#81](https://github.com/rickclephas/KMP-NativeCoroutines/issues/81) and
> [#82](https://github.com/rickclephas/KMP-NativeCoroutines/issues/82) for improved error messages.

### Custom CoroutineScope

Custom `CoroutineScope`s are still supported, just make sure they are either `internal` or `public`.

### Extension properties/functions

The plugin is now generating extension properties/functions and no longer modifies the original class.  
ObjC/Swift interop have a couple of limitations with extension functions.  
Take a look at the Kotlin [docs](https://kotlinlang.org/docs/native-objc-interop.html#extensions-and-category-members)
for more information.

## Improved property/function names

Property and function names are now being reused for their native versions.  
So go ahead and remove all those `Native` suffixes from your Swift code:
```diff
- createPublisher(for: clock.timeNative)
+ createPublisher(for: clock.time)

- createFuture(for: randomLettersGenerator.getRandomLettersNative())
+ createFuture(for: randomLettersGenerator.getRandomLetters())
```

The value and replay cache property names also drop the `Native` suffix:
```diff
- let value = clock.timeNativeValue
+ let value = clock.timeValue

- let replayCache = clock.timeNativeReplayCache
+ let replayCache = clock.timeReplayCache
```

> **Note**: you can now customize the value and replay cache suffixes,  
> or if desired completely remove those properties from the generated code.  
> Checkout the [README](README.md#name-suffix) for more info.

## AsyncSequence

The `asyncStream(for:)` function has been renamed to `asyncSequence(for:)` and now returns an `AsyncSequence`.
```diff
- let lettersStream = asyncStream(for: randomLettersGenerator.getRandomLettersFlow())
+ let lettersStream = asyncSequence(for: randomLettersGenerator.getRandomLettersFlow())
  for try await letters in lettersStream {
      print("Got random letters: \(letters)")
  }
```

Collecting a `Flow` with an `AsyncSequence` will now apply backpressure.  
Meaning your Swift code is no longer buffering elements, 
but will suspend your Kotlin code in the same way collecting the `Flow` in Kotlin would.

## Swift CancellationError

The Swift Concurrency functions will throw a [`CancellationError`](https://developer.apple.com/documentation/swift/cancellationerror) 
instead of the `KotlinCancellationException` wrapped `NSError`.
