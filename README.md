# KMP-NativeCoroutines

A library to use Kotlin Coroutines from Swift code in KMP apps.

> :bulb: **Swift Async/Await:** checkout the [`feature/swift-async-await`](https://github.com/rickclephas/KMP-NativeCoroutines/tree/feature/swift-async-await) branch 
for some Swift 5.5 async wrapper functions.

## Why this library?

Both KMP and Kotlin Coroutines are amazing but together they have some limitations.

The most important limitation is cancellation support.  
Kotlin suspend functions are exposed to Swift as functions with a completion handler.  
This allows you to easily use them from your Swift code, but it doesn't support cancellation.

> :warning: Swift 5.5 brings async functions to Swift!  
> For interoperability with ObjC all function with a completion handler can be called like an async function.  
> This means starting with Swift 5.5 your Kotlin suspend functions will look like Swift async functions.  
> But that's just syntactic sugar, so there's still no cancellation support :cry:.

Besides cancellation support, ObjC doesn't support generics on protocols.  
So all the `Flow` interfaces lose their generic value type which make them hard to use.

This library solves both of these limitations :smile:.

## Installation

Add the Kotlin library to your common dependencies:
```kotlin
implementation("com.rickclephas.kmp:kmp-nativecoroutines-core:0.3.0")
```

and add the Swift library to your `Podfile`:
```ruby
pod 'KMPNativeCoroutinesCombine' # For Swift Combine
pod 'KMPNativeCoroutinesRxSwift' # For RxSwift
```

## Usage

To use Kotlin Coroutines from Swift you will need to:
1. Add some extension properties/functions to your Kotlin native code.
2. Use the wrapper functions in Swift to get Combine publishers or RxSwift observables.

### Flows

#### Kotlin

Create an extension property to expose the `Flow` as a `NativeFlow` to Swift:

```kotlin
val Clock.timeNative
    get() = time.asNativeFlow()
```

#### Swift Combine

Use the `createPublisher(for:)` function to get an `AnyPublisher` for the `NativeFlow`:

```swift
let publisher = createPublisher(for: clock.timeNative)
```

#### RxSwift

Use the `createObservable(for:)` function to get an `Observable` for the `NativeFlow`:

```swift
let observable = createObservable(for: clock.timeNative)
```

### Suspend functions

#### Kotlin

Create an extension function to expose the suspend function as a `NativeSuspend` to Swift:

```kotlin
fun RandomLettersGenerator.getRandomLettersNative() =
    nativeSuspend { getRandomLetters() }
```

#### Swift Combine

Use the `createFuture(for:)` function to get an `AnyPublisher` for the `NativeSuspend`:

```swift
let future = createFuture(for: randomLettersGenerator.getRandomLettersNative())
```

#### RxSwift

Use the `createSingle(for:)` function to get a `Single` for the `NativeSuspend`:

```swift
let single = createSingle(for: randomLettersGenerator.getRandomLettersNative())
```