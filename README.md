# KMP-NativeCoroutines

A library to use Kotlin Coroutines from Swift code in KMP apps.

> :warning: **Note:** this branch contains the Swift Async/Await wrapper functions.  
Swift Async/Await is a Swift 5.5 feature and requires Xcode 13 beta.  
These wrapper functions can only be used in apps targeting iOS 15 and macOS 12.

## Why this library?

Both KMP and Kotlin Coroutines are amazing but together they have same limitations.

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
implementation("com.rickclephas.kmp:kmp-nativecoroutines-core:0.2.0")
```

and add the Swift library to your `Podfile`:
```ruby
pod 'KMPNativeCoroutinesCombine' # For Swift Combine
pod 'KMPNativeCoroutinesAsync' # For Swift Async/Await
```

## Usage

To use Kotlin Coroutines from Swift you will need to:
1. Add some extension properties/functions to your Kotlin native code.
2. Use the wrapper functions in Swift to get Combine publishers.

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

#### Swift Async/Await

Use the `asyncFunction(for:)` function to get an async function for the `NativeSuspend`:

```swift
let letters = try await asyncFunction(for: randomLettersGenerator.getRandomLettersNative())
```

or use the `asyncResult(for:)` function to get the result of the `NativeSuspend`:

```swift
let result = await asyncResult(for: randomLettersGenerator.getRandomLettersNative())
```
