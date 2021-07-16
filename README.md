# KMP-NativeCoroutines

A library to use Kotlin Coroutines from Swift code in KMP apps.

## Why this library?

Both KMP and Kotlin Coroutines are amazing but together they have some limitations.

The most important limitation is cancellation support.  
Kotlin suspend functions are exposed to Swift as functions with a completion handler.  
This allows you to easily use them from your Swift code, but it doesn't support cancellation.

> While Swift 5.5 brings async functions to Swift, it doesn't solve this issue.  
> For interoperability with ObjC all function with a completion handler can be called like an async function.  
> This means starting with Swift 5.5 your Kotlin suspend functions will look like Swift async functions.  
> But that's just syntactic sugar, so there's still no cancellation support.

Besides cancellation support, ObjC doesn't support generics on protocols.  
So all the `Flow` interfaces lose their generic value type which make them hard to use.

This library solves both of these limitations :smile: .

## Compatibility

As of version `0.2.0` the library uses Kotlin version `1.5.20`.  
Compatibility versions for older Kotlin versions are also available:

|Version suffix|Kotlin|Coroutines|
|---|:---:|:---:|
|_no suffix_|1.5.20|1.5.0-native-mt|
|-kotlin-1.5.10|1.5.10|1.5.0-native-mt|

You can choose from a couple of Swift implementations.  
Depending on the implementation you can support as low as iOS 9 and macOS 10.9:

|Implementation|Swift|iOS|macOS|
|---|:---:|:---:|:---:|
|RxSwift|5.0|9.0|10.9|
|Combine|5.0|13.0|10.15|
|Async :construction:|5.5|15.0|12.0|

> :construction: : the Async implementation requires Xcode 13 which is currently in beta!

## Installation

The library consists of a Kotlin and Swift part which you'll need to add to your project.  
The Kotlin part is available on Maven Central and the Swift part can be installed via CocoaPods.

Make sure to always use the same versions for all the libraries!

[![latest release](https://img.shields.io/github/v/release/rickclephas/KMP-NativeCoroutines?label=latest%20release&sort=semver)](https://github.com/rickclephas/KMP-NativeCoroutines/releases)

### Kotlin

On the Kotlin side you'll just add the library to your common dependencies:
```kotlin
implementation("com.rickclephas.kmp:kmp-nativecoroutines-core:$version")
```

### Swift

Now for Swift you can choose from a couple of implementations.  
Add one or more of the following libraries to your `Podfile`:
```ruby
pod 'KMPNativeCoroutinesCombine'  # Combine implementation
pod 'KMPNativeCoroutinesRxSwift'  # RxSwift implementation
pod 'KMPNativeCoroutinesAsync'    # Swift 5.5 Async/Await implementation
```

## Usage

To use your Kotlin Coroutines code from Swift you will need to:
1. Add some extension properties/functions to your Kotlin native code :eyes: .
2. Use the wrapper functions in Swift to get Observables, Publishers or async functions.

> :eyes: : as soon as the [codegen](https://github.com/rickclephas/KMP-NativeCoroutines/tree/feature/codegen) 
> compiler plugin is ready you won't even need to do anything on the Kotlin side anymore!

### Kotlin

The Kotlin part of the library provides 2 functions to convert your Coroutines code into something Swift understands.  

For `Flow`s there is the `asNativeFlow` function:
```kotlin
// Somewhere in your Kotlin code you'll have a Flow property
class Clock {
    val time: StateFlow<Long> // This can be any kind of Flow
}

// In your native Kotlin code you'll define the extension property
val Clock.timeNative
    get() = time.asNativeFlow()
```

and for suspend functions there is the `nativeSuspend` function:
```kotlin
// Somewhere in your Kotlin code you'll have a suspend function
class RandomLettersGenerator {
    suspend fun getRandomLetters(): String { 
        // Code to generate some random letters
    }
}

// In your native Kotlin code you'll define the extension function
fun RandomLettersGenerator.getRandomLettersNative() =
    nativeSuspend { getRandomLetters() }
```

It's also possible to combine the two functions if you have a suspend function that returns a `Flow`:
```kotlin
// Somewhere in your Kotlin code you'll have a suspend function returning a Flow
class RandomLettersGenerator {
    suspend fun getRandomLettersFlow(): Flow<String> {
        // Code to generate a Flow of random letters
    }
}

// In your native Kotlin code you'll define the extension function
fun RandomLettersGenerator.getRandomLettersFlowNative() =
    nativeSuspend { getRandomLettersFlow().asNativeFlow() }
```

#### Custom CoroutineScope

For more control you can provide a custom `CoroutineScope` that should be used for the native extensions:
```kotlin
@SharedImmutable
val customCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

val Clock.timeNative
    get() = time.asNativeFlow(customCoroutineScope)

fun RandomLettersGenerator.getRandomLettersNative() =
    nativeSuspend(customCoroutineScope) { getRandomLetters() }
```

If you don't provide a `CoroutineScope` the default scope will be used which is defined as:
```kotlin
@SharedImmutable
internal val defaultCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
```

### RxSwift

The RxSwift implementation provides a couple functions to get an `Observable` or `Single` for your Coroutines code.

For your `Flow`s use the `createObservable(for:)` function:
```swift
// Create an observable for your flow
let observable = createObservable(for: clock.timeNative)

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

For the suspend functions you should use the `createSingle(for:)` function:
```swift
// Create a single for the suspend function
let single = createSingle(for: randomLettersGenerator.getRandomLettersNative())

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

You can also use the `createObservable(for:)` function for suspend functions that return a `Flow`:
```swift
let observable = createObservable(for: randomLettersGenerator.getRandomLettersFlowNative())
```

**Note:** these functions create deferred `Observable`s and `Single`s.  
Meaning every subscription will trigger the collection of the `Flow` or execution of the suspend function.

### Combine

The Combine implementation provides a couple functions to get an `AnyPublisher` for your Coroutines code.

For your `Flow`s use the `createPublisher(for:)` function:
```swift
// Create an AnyPublisher for your flow
let publisher = createPublisher(for: clock.timeNative)

// Now use this publisher as you would any other
let cancellable = publisher.sink { completion in
    print("Received completion: \(completion)")
} receiveValue: { value in
    print("Received value: \(value)")
}

// To cancel the flow (collection) just cancel the publisher
cancellable.cancel()
```

For the suspend functions you should use the `createFuture(for:)` function:
```swift
// Create a Future/AnyPublisher for the suspend function
let future = createFuture(for: randomLettersGenerator.getRandomLettersNative())

// Now use this future as you would any other
let cancellable = future.sink { completion in
    print("Received completion: \(completion)")
} receiveValue: { value in
    print("Received value: \(value)")
}

// To cancel the suspend function just cancel the future
cancellable.cancel()
```

You can also use the `createPublisher(for:)` function for suspend functions that return a `Flow`:
```swift
let publisher = createPublisher(for: randomLettersGenerator.getRandomLettersFlowNative())
```

**Note:** these functions create deferred `AnyPublisher`s.  
Meaning every subscription will trigger the collection of the `Flow` or execution of the suspend function.

### Swift 5.5 Async/Await

> :construction: : the Async implementation requires Xcode 13 which is currently in beta!

The Async implementation provides some functions to get async Swift functions for your Kotlin suspend functions.

Use the `asyncFunction(for:)` function to get an async function that can be awaited:
```swift
let handle = async {
    do {
        let letters = try await asyncFunction(for: randomLettersGenerator.getRandomLettersNative())
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
async {
    let result = await asyncResult(for: randomLettersGenerator.getRandomLettersNative())
    if case let .success(letters) = result {
        print("Got random letters: \(letters)")
    }
}
```