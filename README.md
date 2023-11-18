# KMP-NativeCoroutines

A library to use Kotlin Coroutines from Swift code in KMP apps.

> [!IMPORTANT]
> Looking to upgrade from the 0.x releases? Checkout the [migration steps](MIGRATING_TO_V1.md).

## Why this library?

Both KMP and Kotlin Coroutines are amazing, but together they have some limitations.

The most important limitation is cancellation support.  
Kotlin suspend functions are exposed to Swift as functions with a completion handler.  
This allows you to easily use them from your Swift code, but it doesn't support cancellation.

> [!NOTE]
> While Swift 5.5 brings async functions to Swift, it doesn't solve this issue.  
> For interoperability with ObjC all functions with a completion handler can be called like an async function.  
> This means starting with Swift 5.5 your Kotlin suspend functions will look like Swift async functions.  
> But that's just syntactic sugar, so there's still no cancellation support.

Besides cancellation support, ObjC doesn't support generics on protocols.  
So all the `Flow` interfaces lose their generic value type which make them hard to use.

This library solves both of these limitations 😄.

## Compatibility

The latest version of the library uses Kotlin version `1.9.20`.  
Compatibility versions for older and/or preview Kotlin versions are also available:

| Version        | Version suffix       |   Kotlin   |    KSP     | Coroutines |
|----------------|----------------------|:----------:|:----------:|:----------:|
| **_latest_**   | **_no suffix_**      | **1.9.20** | **1.0.14** | **1.7.3**  |
| 1.0.0-ALPHA-19 | _no suffix_          |   1.9.20   |   1.0.13   |   1.7.3    |
| 1.0.0-ALPHA-18 | _no suffix_          |   1.9.10   |   1.0.13   |   1.7.3    |
| 1.0.0-ALPHA-17 | _no suffix_          |   1.9.0    |   1.0.12   |   1.7.3    |
| 1.0.0-ALPHA-13 | _no suffix_          |   1.9.0    |   1.0.11   |   1.7.2    |
| 1.0.0-ALPHA-12 | _no suffix_          |   1.8.22   |   1.0.11   |   1.7.2    |
| 1.0.0-ALPHA-10 | _no suffix_          |   1.8.21   |   1.0.11   |   1.7.1    |
| 1.0.0-ALPHA-8  | _no suffix_          |   1.8.21   |   1.0.11   |   1.6.4    |
| 1.0.0-ALPHA-7  | _no suffix_          |   1.8.20   |   1.0.10   |   1.6.4    |

You can choose from a couple of Swift implementations.  
Depending on the implementation you can support as low as iOS 9, macOS 10.9, tvOS 9 and watchOS 3:

| Implementation | Swift | iOS  | macOS | tvOS | watchOS |
|----------------|:-----:|:----:|:-----:|:----:|:-------:|
| Async          |  5.5  | 13.0 | 10.15 | 13.0 |   6.0   |
| Combine        |  5.0  | 13.0 | 10.15 | 13.0 |   6.0   |
| RxSwift        |  5.0  | 9.0  | 10.9  | 9.0  |   3.0   |

## Installation

The library consists of a Kotlin and Swift part which you'll need to add to your project.  
The Kotlin part is available on Maven Central and the Swift part can be installed via CocoaPods 
or the Swift Package Manager.

Make sure to always use the same versions for all the libraries!

### Kotlin

For Kotlin just add the plugin to your `build.gradle.kts`:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("com.rickclephas.kmp.nativecoroutines") version "1.0.0-ALPHA-21"
}
```
and make sure to opt in to the experimental `@ObjCName` annotation:
```kotlin
kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}
```

### Swift (Swift Package Manager)

The Swift implementations are available via the Swift Package Manager.  
Just add it to your `Package.swift` file:
```swift
dependencies: [
    .package(url: "https://github.com/rickclephas/KMP-NativeCoroutines.git", from: "1.0.0-ALPHA-21")
]
```

Or add it in Xcode by going to `File` > `Add Packages...` and providing the URL:
`https://github.com/rickclephas/KMP-NativeCoroutines.git`.

> [!NOTE]
> The version for the Swift package should not contain the Kotlin version suffix
> (e.g. `-new-mm` or `-kotlin-1.6.0`).

> [!NOTE]
> If you only need a single implementation you can also use the SPM specific versions with suffixes
> `-spm-async`, `-spm-combine` and `-spm-rxswift`.

### Swift (CocoaPods)

If you use CocoaPods add one or more of the following libraries to your `Podfile`:
```ruby
pod 'KMPNativeCoroutinesAsync', '1.0.0-ALPHA-21'    # Swift Concurrency implementation
pod 'KMPNativeCoroutinesCombine', '1.0.0-ALPHA-21'  # Combine implementation
pod 'KMPNativeCoroutinesRxSwift', '1.0.0-ALPHA-21'  # RxSwift implementation
```
> [!NOTE]
> The version for CocoaPods should not contain the Kotlin version suffix (e.g. `-new-mm` or `-kotlin-1.6.0`).

### IntelliJ / Android Studio

Install the [IDE plugin](https://plugins.jetbrains.com/plugin/22481) from the JetBrains Marketplace to get:
* Annotation usage validation
* Exposed coroutines warnings
* Quick fixes to add annotations

## Usage

Using your Kotlin Coroutines code from Swift is almost as easy as calling the Kotlin code.   
Just use the wrapper functions in Swift to get async functions, AsyncStreams, Publishers or Observables.

### Kotlin

The plugin will automagically generate the necessary code for you! 🔮  
Just annotate your coroutines declarations with `@NativeCoroutines` (or `@NativeCoroutinesState`).

#### Flows

Your `Flow` properties/functions get a native version:
```kotlin
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines

class Clock {
    // Somewhere in your Kotlin code you define a Flow property
    // and annotate it with @NativeCoroutines
    @NativeCoroutines
    val time: StateFlow<Long> // This can be any kind of Flow
}
```

<details><summary>Generated code</summary>
<p>

The plugin will generate this native property for you:
```kotlin
import com.rickclephas.kmp.nativecoroutines.asNativeFlow
import kotlin.native.ObjCName

@ObjCName(name = "time")
val Clock.timeNative
    get() = time.asNativeFlow()
```

For the `StateFlow` defined above the plugin will also generate this value property:
```kotlin
val Clock.timeValue
    get() = time.value
```

In case of a `SharedFlow` the plugin would generate a replay cache property:
```kotlin
val Clock.timeReplayCache
    get() = time.replayCache
```
</p>
</details>

#### StateFlows

Using `StateFlow` properties to track state (like in a view model)?  
Use the `@NativeCoroutinesState` annotation instead:
```kotlin
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState

class Clock {
    // Somewhere in your Kotlin code you define a StateFlow property
    // and annotate it with @NativeCoroutinesState
    @NativeCoroutinesState
    val time: StateFlow<Long> // This must be a StateFlow
}
```

<details><summary>Generated code</summary>
<p>

The plugin will generate these native properties for you:
```kotlin
import com.rickclephas.kmp.nativecoroutines.asNativeFlow
import kotlin.native.ObjCName

@ObjCName(name = "time")
val Clock.timeValue
    get() = time.value

val Clock.timeFlow
    get() = time.asNativeFlow()
```
</p>
</details>

#### Suspend functions

The plugin also generates native versions for your annotated suspend functions:
```kotlin
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines

class RandomLettersGenerator {
    // Somewhere in your Kotlin code you define a suspend function
    // and annotate it with @NativeCoroutines
    @NativeCoroutines
    suspend fun getRandomLetters(): String { 
        // Code to generate some random letters
    }
}
```

<details><summary>Generated code</summary>
<p>

The plugin will generate this native function for you:
```kotlin
import com.rickclephas.kmp.nativecoroutines.nativeSuspend
import kotlin.native.ObjCName

@ObjCName(name = "getRandomLetters")
fun RandomLettersGenerator.getRandomLettersNative() =
    nativeSuspend { getRandomLetters() }
```
</p>
</details>

#### Interface limitations

Unfortunately extension functions/properties aren't 
[supported](https://kotlinlang.org/docs/native-objc-interop.html#extensions-and-category-members)
on Objective-C protocols.  

However this limitation can be "overcome" with some Swift magic.  
Assuming `RandomLettersGenerator` is an `interface` instead of a `class` we can do the following:
```swift
import KMPNativeCoroutinesCore

extension RandomLettersGenerator {
    func getRandomLetters() -> NativeSuspend<String, Error, KotlinUnit> {
        RandomLettersGeneratorNativeKt.getRandomLetters(self)
    }
}
```

#### Exposed coroutines checks

When suspend functions and/or `Flow` declarations are exposed to ObjC/Swift,
the compiler and IDE plugin will produce a warning, reminding you to add one of the KMP-NativeCoroutines annotations.

You can customise the severity of these checks in your `build.gradle.kts` file:
```kotlin
nativeCoroutines {
    exposedSeverity = ExposedSeverity.ERROR
}
```

Or, if you are not interested in these checks, disable them:
```kotlin
nativeCoroutines {
    exposedSeverity = ExposedSeverity.NONE
}
```

### Swift Concurrency

The Async implementation provides some functions to get async Swift functions and `AsyncSequence`s.

#### Async functions

Use the `asyncFunction(for:)` function to get an async function that can be awaited:
```swift
import KMPNativeCoroutinesAsync

let handle = Task {
    do {
        let letters = try await asyncFunction(for: randomLettersGenerator.getRandomLetters())
        print("Got random letters: \(letters)")
    } catch {
        print("Failed with error: \(error)")
    }
}

// To cancel the suspend function just cancel the async task
handle.cancel()
```

or if you don't like these do-catches you can use the `asyncResult(for:)` function:
```swift
import KMPNativeCoroutinesAsync

let result = await asyncResult(for: randomLettersGenerator.getRandomLetters())
if case let .success(letters) = result {
    print("Got random letters: \(letters)")
}
```

#### AsyncSequence

For `Flow`s there is the `asyncSequence(for:)` function to get an `AsyncSequence`:
```swift
import KMPNativeCoroutinesAsync

let handle = Task {
    do {
        let sequence = asyncSequence(for: randomLettersGenerator.getRandomLettersFlow())
        for try await letters in sequence {
            print("Got random letters: \(letters)")
        }
    } catch {
        print("Failed with error: \(error)")
    }
}

// To cancel the flow (collection) just cancel the async task
handle.cancel()
```

### Combine

The Combine implementation provides a couple functions to get an `AnyPublisher` for your Coroutines code.

> [!NOTE]
> These functions create deferred `AnyPublisher`s.  
> Meaning every subscription will trigger the collection of the `Flow` or execution of the suspend function.

#### Publisher

For your `Flow`s use the `createPublisher(for:)` function:
```swift
import KMPNativeCoroutinesCombine

// Create an AnyPublisher for your flow
let publisher = createPublisher(for: clock.time)

// Now use this publisher as you would any other
let cancellable = publisher.sink { completion in
    print("Received completion: \(completion)")
} receiveValue: { value in
    print("Received value: \(value)")
}

// To cancel the flow (collection) just cancel the publisher
cancellable.cancel()
```

You can also use the `createPublisher(for:)` function for suspend functions that return a `Flow`:
```swift
let publisher = createPublisher(for: randomLettersGenerator.getRandomLettersFlow())
```

#### Future

For the suspend functions you should use the `createFuture(for:)` function:
```swift
import KMPNativeCoroutinesCombine

// Create a Future/AnyPublisher for the suspend function
let future = createFuture(for: randomLettersGenerator.getRandomLetters())

// Now use this future as you would any other
let cancellable = future.sink { completion in
    print("Received completion: \(completion)")
} receiveValue: { value in
    print("Received value: \(value)")
}

// To cancel the suspend function just cancel the future
cancellable.cancel()
```

### RxSwift

The RxSwift implementation provides a couple functions to get an `Observable` or `Single` for your Coroutines code.

> [!NOTE]
> These functions create deferred `Observable`s and `Single`s.  
> Meaning every subscription will trigger the collection of the `Flow` or execution of the suspend function.

#### Observable

For your `Flow`s use the `createObservable(for:)` function:
```swift
import KMPNativeCoroutinesRxSwift

// Create an observable for your flow
let observable = createObservable(for: clock.time)

// Now use this observable as you would any other
let disposable = observable.subscribe(onNext: { value in
    print("Received value: \(value)")
}, onError: { error in
    print("Received error: \(error)")
}, onCompleted: {
    print("Observable completed")
}, onDisposed: {
    print("Observable disposed")
})

// To cancel the flow (collection) just dispose the subscription
disposable.dispose()
```

You can also use the `createObservable(for:)` function for suspend functions that return a `Flow`:
```swift
let observable = createObservable(for: randomLettersGenerator.getRandomLettersFlow())
```

#### Single

For the suspend functions you should use the `createSingle(for:)` function:
```swift
import KMPNativeCoroutinesRxSwift

// Create a single for the suspend function
let single = createSingle(for: randomLettersGenerator.getRandomLetters())

// Now use this single as you would any other
let disposable = single.subscribe(onSuccess: { value in
    print("Received value: \(value)")
}, onFailure: { error in
    print("Received error: \(error)")
}, onDisposed: {
    print("Single disposed")
})

// To cancel the suspend function just dispose the subscription
disposable.dispose()
```

## Customize

There are a number of ways you can customize the generated Kotlin code.

### Name suffix

Don't like the naming of the generated properties/functions?  
Specify your own custom suffixes in your `build.gradle.kts` file:
```kotlin
nativeCoroutines {
    // The suffix used to generate the native coroutine function and property names.
    suffix = "Native"
    // The suffix used to generate the native coroutine file names.
    // Note: defaults to the suffix value when `null`.
    fileSuffix = null
    // The suffix used to generate the StateFlow value property names,
    // or `null` to remove the value properties.
    flowValueSuffix = "Value"
    // The suffix used to generate the SharedFlow replayCache property names,
    // or `null` to remove the replayCache properties.
    flowReplayCacheSuffix = "ReplayCache"
    // The suffix used to generate the native state property names.
    stateSuffix = "Value"
    // The suffix used to generate the `StateFlow` flow property names,
    // or `null` to remove the flow properties.
    stateFlowSuffix = "Flow"
}
```

### CoroutineScope

For more control you can provide a custom `CoroutineScope` with the `NativeCoroutineScope` annotation:
```kotlin
import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope

class Clock {
    @NativeCoroutineScope
    internal val coroutineScope = CoroutineScope(job + Dispatchers.Default)
}
```

> [!NOTE]
> Your custom coroutine scope must be either `internal` or `public`.

If you don't provide a `CoroutineScope` the default scope will be used which is defined as:
```kotlin
internal val defaultCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
```

> [!NOTE]
> KMP-NativeCoroutines has built-in support for [KMM-ViewModel](https://github.com/rickclephas/KMM-ViewModel).  
> Coroutines inside your `KMMViewModel` will (by default) use the `CoroutineScope` from the `ViewModelScope`. 

### Ignoring declarations

Use the `NativeCoroutinesIgnore` annotation to tell the plugin to ignore a property or function:
```kotlin
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore

@NativeCoroutinesIgnore
val ignoredFlowProperty: Flow<Int>

@NativeCoroutinesIgnore
suspend fun ignoredSuspendFunction() { }
```

### Refining declarations in Swift

If for some reason you would like to further refine your Kotlin declarations in Swift, you can use the
`NativeCoroutinesRefined` and `NativeCoroutinesRefinedState` annotations.  
These will tell the plugin to add the [`ShouldRefineInSwift`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.native/-should-refine-in-swift/)
annotation to the generated properties/function.

> [!NOTE]
> This currently requires a module-wide opt-in to `kotlin.experimental.ExperimentalObjCRefinement`.

You could for example refine your `Flow` property to an `AnyPublisher` property:
```kotlin
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined

class Clock {
    @NativeCoroutinesRefined
    val time: StateFlow<Long>
}
```

```swift
import KMPNativeCoroutinesCombine

extension Clock {
    var time: AnyPublisher<KotlinLong, Error> {
        createPublisher(for: __time)
    }
}
```
